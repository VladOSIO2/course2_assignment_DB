package db.entities.queries;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

    public static Map<Integer, String> updateAndGetMostPopularQuizzes() throws SQLException {
        SimpleQuery.execute("UPDATE quiz SET info = NULL;");

        String query = """
                UPDATE quiz AS q
                SET q.info = 'most popular quiz'
                WHERE q.quiz_id IN (
                 SELECT quiz_id FROM quiz_completion
                 HAVING COUNT(DISTINCT responder_id) >= ALL (
                 SELECT COUNT(DISTINCT responder_id)
                 FROM quiz_completion AS qc
                 GROUP BY qc.quiz_id))
                """;
        SimpleQuery.execute(query);

        query = """
                SELECT quiz_id, quiz.name FROM quiz
                WHERE info = 'most popular quiz'
                """;
        Map<Integer, String> map =
                SimpleQuery.getIntegerStringMap(query, "quiz_id", "name");
        Set<String> idsAsStr = new TreeSet<>();
        map.keySet().forEach(i -> idsAsStr.add(Integer.toString(i)));
        SimpleQuery.log("updated most popular quizzes: now set to "
                + String.join(", ", idsAsStr));
        return map;
    }
}
