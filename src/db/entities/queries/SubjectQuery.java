package db.entities.queries;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.Map;

public class SubjectQuery {
    public static Map<Integer, String> searchSubject(String subjectPart) throws SQLException {
        String query = """
                SELECT subject_id, name FROM subject
                WHERE name LIKE '%s'
                """.formatted('%' + subjectPart + '%');
        return SimpleQuery.getIntegerStringMap(query, "subject_id","name");
    }

    public static String getSubjectByID(int subjectID) throws SQLException {
        String querySelectByID = "SELECT * FROM subject WHERE subject_id =" + subjectID;
        return SimpleQuery.getString(querySelectByID, "name");
    }

    public static String updateSubject(int subjectID, String newName) {
        String queryUpdate = "UPDATE subject SET name = '%s' WHERE subject_id = %d"
                .formatted(newName, subjectID);

        try {
            String oldName = getSubjectByID(subjectID);
            if (oldName != null) {
                SimpleQuery.execute(queryUpdate);
                SimpleQuery.log("subject #%d: updated name %s->%s"
                        .formatted(subjectID, oldName, newName));
                return "Предмет змінено: %s -> %s".formatted(oldName, newName);
            } else {
                return "Помилка: не знайдено предмету з ID " + subjectID;
            }
        } catch (SQLException e) {
            return "Помилка SQL при зміні:\n" + e.getMessage();
        }
    }

    public static String deleteSubject(int subjectID) {
        String queryUpdate = "DELETE FROM subject WHERE subject_id = %d"
                .formatted(subjectID);

        try {
            String name = getSubjectByID(subjectID);
            if (name != null) {
                if (!hasThemes(subjectID)) {
                    SimpleQuery.execute(queryUpdate);
                    SimpleQuery.log("subject #%d: deleted %s"
                            .formatted(subjectID, name));
                    return "Предмет видалено: %s".formatted(name);
                } else {
                    return "Помилка: не можна видалити предмет, з яким пов'язані теми";
                }
            } else {
                return "Помилка: не знайдено предмету з ID " + subjectID;
            }
        } catch (SQLException e) {
            return "Помилка SQL при видаленні:\n" + e.getMessage();
        }
    }

    public static String insertSubject(String subject) {
        try {
            if (!SimpleQuery.exists("""
                     SELECT * FROM subject
                     WHERE name = '%s'
                     """.formatted(subject))) {
                String queryInsert = "INSERT INTO subject(name) VALUE ('%s')"
                        .formatted(subject);
                SimpleQuery.execute(queryInsert);
                SimpleQuery.log("subject: added %s"
                        .formatted(subject));
                return "Додано предмет: "+ subject;
            } else {
                return "Помилка: Такий предмет вже є";
            }
        } catch (SQLException e) {
            return "Помилка SQL при додаванні:\n" + e.getMessage();
        }
    }

    private static boolean hasThemes(int subjectID) throws SQLException {
        return SimpleQuery.exists("SELECT * FROM theme WHERE subject_id = " + subjectID);
    }

    public static String getSubjectByTheme(int themeID) throws SQLException {
        int subjectID = SimpleQuery.getInt(
                "SELECT subject_id FROM theme WHERE theme_id = " + themeID,
                "subject_id");
        return SubjectQuery.getSubjectByID(subjectID);
    }

    public static String getSubjectByQuestion(int questionID) throws SQLException {
        String query = """
                SELECT subject.name AS s_name FROM question
                JOIN theme USING (theme_id)
                JOIN subject USING (subject_id)
                WHERE question_id =""" + questionID;
        return SimpleQuery.getString(query, "s_name");
    }
}
