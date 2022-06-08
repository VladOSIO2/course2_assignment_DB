package db.entities.question;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.Map;

public class GradeQuery {
    public static Map<Integer, String> getGrades() throws SQLException {
        String query = """
                SELECT grade_id, name FROM grade
                """;
        return SimpleQuery.getIntegerStringMap(query, "grade_id", "name");
    }
}
