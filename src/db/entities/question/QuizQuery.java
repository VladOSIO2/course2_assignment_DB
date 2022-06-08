package db.entities.question;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class QuizQuery {
    public static Map<Integer, String> getQuizzes(
            String quizNamePart, int subjectID, int themeID, boolean includeNotNowQuiz
    ) throws SQLException {
        String subjectCond = subjectID == -1 ? "" : "AND subject_id = %d\n".formatted(subjectID);
        String themeCond = themeID == -1 ? "" : "AND theme_id = %d\n".formatted(themeID);
        String dateCond = includeNotNowQuiz ? "" : "AND NOW() BETWEEN dt_start AND dt_end\n";
        String query = """
                SELECT DISTINCT quiz_id, quiz.name AS quiz_name FROM quiz
                JOIN question_quiz USING (quiz_id)
                JOIN question USING (question_id)
                JOIN theme USING (theme_id)
                JOIN subject USING (subject_id)
                WHERE quiz.name LIKE '%s'
                """.formatted('%' + quizNamePart + '%');
        query += themeCond + dateCond + subjectCond;
        return SimpleQuery.getIntegerStringMap(query, "quiz_id", "quiz_name");
    }

    public static int getMaxQuizMark(int quizID) throws SQLException {
        String query = """
                SELECT SUM(points) as points_sum FROM quiz
                JOIN question_quiz USING (quiz_id)
                JOIN question USING (question_id)
                JOIN grade USING (grade_id)
                WHERE quiz_id = %d
                """.formatted(quizID);
        return SimpleQuery.getInt(query, "points_sum");
    }

    public static int getResponderCount(int quizID) throws SQLException {
        String query = """
                SELECT COUNT(DISTINCT responder_id) AS total_count FROM quiz
                JOIN quiz_completion USING (quiz_id)
                WHERE quiz_id = %d
                """.formatted(quizID);
        return SimpleQuery.getInt(query, "total_count");
    }

    public static int getResponderCount(int quizID, int markFrom, int markTo) throws SQLException {
        String query = """
                SELECT COUNT(DISTINCT responder_id) AS total_count FROM quiz
                JOIN quiz_completion USING (quiz_id)
                WHERE quiz_id = %d
                AND mark BETWEEN %d AND %d
                """.formatted(quizID, markFrom, markTo);
        return SimpleQuery.getInt(query, "total_count");
    }
}
