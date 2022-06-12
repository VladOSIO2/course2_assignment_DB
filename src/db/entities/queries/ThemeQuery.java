package db.entities.queries;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.Map;

public class ThemeQuery {
    public static Map<Integer, String> searchTheme(String themePart) throws SQLException {
        String query = """
                SELECT theme_id, name FROM theme
                WHERE name LIKE '%s'
                """.formatted('%' + themePart + '%');
        return SimpleQuery.getIntegerStringMap(query, "theme_id", "name");
    }

    public static Map<Integer, String> searchTheme(String themePart, int subjectID) throws SQLException {
        String query = """
                SELECT theme_id, theme.name AS themes FROM theme
                JOIN subject USING (subject_id)
                WHERE subject_id = %d
                AND theme.name LIKE '%s'
                """.formatted(subjectID, '%' + themePart + '%');
        return SimpleQuery.getIntegerStringMap(query, "theme_id", "themes");
    }

    public static String getThemeByID(int themeID) throws SQLException {
        String query = """
                SELECT name FROM theme
                WHERE theme_id =""" + themeID;
        return SimpleQuery.getString(query, "name");
    }

    public static int getThemeSubjectID(int themeID) throws SQLException {
        String query = """
                SELECT subject_id FROM theme
                WHERE theme_id =""" + themeID;
        return SimpleQuery.getInt(query, "subject_id");
    }

    public static boolean themeExists(int themeID) throws SQLException {
        String query = "SELECT theme_id FROM theme WHERE theme_id=" + themeID;
        return SimpleQuery.exists(query);
    }

    public static boolean hasQuestions(int themeID) throws SQLException {
        String query = "SELECT theme_id FROM question WHERE theme_id=" + themeID;
        return SimpleQuery.exists(query);
    }

    public static String getThemeByQuestion(int questionID) throws SQLException {
        String query = """
                SELECT theme.name AS t_name FROM question
                JOIN theme USING (theme_id)
                WHERE question_id =""" + questionID;
        return SimpleQuery.getString(query, "t_name");
    }

    public static String updateTheme(int themeID, int subjectID, String newName) {
        String queryUpdate = """
                UPDATE theme
                SET name = '%s', subject_id = %d
                WHERE theme_id = %d
                """.formatted(newName, subjectID, themeID);
        String res = "";
        try {
            if (themeExists(themeID)) {
                String oldName = getThemeByID(themeID);
                int oldSubject = getThemeSubjectID(themeID);
                SimpleQuery.execute(queryUpdate);
                if (!newName.equals(oldName)) {
                    SimpleQuery.log("theme #%d: updated name %s->%s"
                            .formatted(themeID, oldName, newName));
                    res += "Тема: змінено назву на " + newName + "\n";
                }
                if (subjectID != oldSubject) {
                    SimpleQuery.log("theme #%d: updated subject# %d->%d"
                            .formatted(themeID, oldSubject, subjectID));
                    res += "Тема: змінено предмет на "
                            + SubjectQuery.getSubjectByID(subjectID) + "\n";
                }
            } else {
                return "Помилка: не знайдено теми з ID " + themeID;
            }
        } catch (SQLException e) {
            return "Помилка SQL при зміні:\n" + e.getMessage();
        }
        return res;
    }

    public static String deleteTheme(int themeID) {
        String queryUpdate = "DELETE FROM theme WHERE theme_id = %d"
                .formatted(themeID);

        try {
            String name = getThemeByID(themeID);
            if (name != null) {
                if (!hasQuestions(themeID)) {
                    SimpleQuery.execute(queryUpdate);
                    SimpleQuery.log("theme #%d: deleted %s".formatted(themeID, name));
                    return "Тему видалено: %s".formatted(name);
                } else {
                    return "Помилка: не можна видалити тему, з якою пов'язані питання";
                }
            } else {
                return "Помилка: не знайдено теми з ID " + themeID;
            }
        } catch (SQLException e) {
            return "Помилка SQL при видаленні:\n" + e.getMessage();
        }
    }

    public static String insertTheme(String theme, int subjectID) {
        try {
            if (!SimpleQuery.exists("""
                     SELECT * FROM theme
                     WHERE name = '%s'
                     """.formatted(theme))) {
                String queryInsert = "INSERT INTO theme(name, subject_id) VALUE ('%s', %d)"
                        .formatted(theme, subjectID);
                SimpleQuery.execute(queryInsert);
                SimpleQuery.log("theme: added %s".formatted(theme));
                return "Додано тему: "+ theme;
            } else {
                return "Помилка: Така тема вже є";
            }
        } catch (SQLException e) {
            return "Помилка SQL при додаванні:\n" + e.getMessage();
        }
    }
}
