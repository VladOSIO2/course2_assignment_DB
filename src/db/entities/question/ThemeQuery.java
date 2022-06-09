package db.entities.question;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.List;
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

    public static void updateTheme() {

    }

    public static void deleteTheme() {

    }

    public static void insertTheme() {

    }
}
