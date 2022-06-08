package db.entities.question;

import db.SimpleQuery;

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
}
