package db.entities.question;

import db.SimpleQuery;

import java.sql.SQLException;
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
}
