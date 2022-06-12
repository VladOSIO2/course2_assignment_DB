package db.entities.queries;

import db.SimpleQuery;
import db.entities.user.UserQuery;

import java.sql.SQLException;
import java.util.List;

public class AuthorQuery {
    public static List<String> getMaxQuestionAuthorNames() throws SQLException {
        String query = """
                SELECT CONCAT_WS(' ', name, surname, patronymic) AS fullname
                FROM question
                JOIN author USING (author_id)
                GROUP BY author_id
                HAVING COUNT(*) >= ALL (
                 SELECT COUNT(*) FROM question
                 GROUP BY author_id
                )
                """;
        return SimpleQuery.getStringList(query, "fullname");
    }

    public static Integer getMaxQuestionAuthorCount() throws SQLException {
        String query = """
                SELECT COUNT(*) AS total
                FROM question
                JOIN author USING (author_id)
                GROUP BY author_id
                HAVING total >= ALL (
                 SELECT COUNT(*) FROM question
                 GROUP BY author_id
                )
                """;
        return SimpleQuery.getInt(query, "total");
    }

    public static String getAuthorByQuestion(int questionID) throws SQLException {
        String query = """
                SELECT CONCAT_WS(' ', surname, name, patronymic) AS fullname
                FROM question
                JOIN author USING (author_id)
                WHERE question_id = %d
                """.formatted(questionID);
        return SimpleQuery.getString(query, "fullname");
    }

    public static int getAuthorIDByQuestion(int questionID) throws SQLException {
        String query = """
                SELECT author_id
                FROM question
                WHERE question_id = %d
                """.formatted(questionID);
        return SimpleQuery.getInt(query, "author_id");
    }

    public static String getAuthor(int authorID) throws SQLException {
        String query = """
                SELECT CONCAT_WS(' ', surname, name, patronymic) AS fullname
                FROM author
                WHERE author_id = %d
                """.formatted(authorID);
        return SimpleQuery.getString(query, "fullname");
    }

    public static int getUserAsAuthor(int userID) throws SQLException {
        List<String> user = UserQuery.getUserFullname(userID);
        String query = """
                SELECT author_id FROM author
                WHERE name = '%s'
                AND surname = '%s'
                AND patronymic = '%s'
                """.formatted(user.get(0), user.get(1), user.get(2));
        Integer i = SimpleQuery.getInt(query, "author_id");
        if (i == null) {
            insertUserAsAuthor(user);
            i = SimpleQuery.getInt(query, "author_id");
        }
        return i;
    }

    private static void insertUserAsAuthor(List<String> user) throws SQLException {
        String query = """
                INSERT INTO author(name, surname, patronymic)
                VALUE ('%s', '%s', '%s')
                """.formatted(user.get(0), user.get(1), user.get(2));
        SimpleQuery.execute(query);
        SimpleQuery.log("added author: " + String.join(" ", user));
    }
}
