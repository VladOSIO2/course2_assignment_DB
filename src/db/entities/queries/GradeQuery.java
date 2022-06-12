package db.entities.queries;

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

    public static String getGradeByQuestion(int questionID) throws SQLException {
        String query = """
                SELECT grade.name AS g_name FROM question
                JOIN grade USING (grade_id)
                WHERE question_id =""" + questionID;
        return SimpleQuery.getString(query, "g_name");
    }
}
