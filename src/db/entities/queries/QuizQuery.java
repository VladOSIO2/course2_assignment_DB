package db.entities.queries;

import db.SimpleQuery;
import utility.Util;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public static LocalDateTime getDTStart(int quizID) throws SQLException {
        String query = """
                SELECT dt_start FROM quiz
                WHERE quiz_id =""" + quizID;
        String dt = SimpleQuery.getString(query, "dt_start");
        return Util.sqlDTToLocalDT(dt);
    }

    public static LocalDateTime getDTEnd(int quizID) throws SQLException {
        String query = """
                SELECT dt_end FROM quiz
                WHERE quiz_id =""" + quizID;
        String dt = SimpleQuery.getString(query, "dt_end");
        return Util.sqlDTToLocalDT(dt);
    }

    public static int getTimeToDo(int quizID) throws SQLException {
        String query = """
                SELECT time_to_do FROM quiz
                WHERE quiz_id =""" + quizID;
        return SimpleQuery.getInt(query, "time_to_do");
    }

    public static boolean hasResponders(int quizID) throws SQLException {
        String query = """
                SELECT * FROM quiz_completion
                WHERE quiz_id =""" + quizID;
        return SimpleQuery.exists(query);
    }

    public static void deleteQuiz(int quizID) throws SQLException {
        String query = """
                DELETE FROM quiz
                WHERE quiz_id =""" + quizID;

        deleteQuizQuestions(quizID);
        SimpleQuery.execute(query);
        SimpleQuery.log("quiz#%d: deleted".formatted(quizID));
    }

    public static void addQuiz(
            int quizID, String dateStart, String dateEnd,
            int timeToDo, String name, ArrayList<Integer> questions) throws SQLException {
        boolean isNew = quizID == -1;
        if (isNew) {
            quizID = insertQuiz(dateStart, dateEnd, timeToDo, name);
        } else {
            updateQuiz(quizID, dateStart, dateEnd, timeToDo, name);
        }
        if (questions != null) {
            if (!isNew) {
                deleteQuizQuestions(quizID);
            }
            insertQuizQuestions(quizID, questions);
        }
    }

    private static int insertQuiz(
            String dateStart, String dateEnd,
            int timeToDo, String name) throws SQLException {
        String query = """
                INSERT INTO quiz(dt_start, dt_end, time_to_do, name)
                VALUE ('%s', '%s', %d, '%s')
                """.formatted(dateStart, dateEnd, timeToDo, name);
        SimpleQuery.execute(query);
        SimpleQuery.log("quiz: created (%s)".formatted(name));

        String queryQuizID = """
                SELECT quiz_id FROM quiz
                WHERE dt_start = '%s'
                AND dt_end = '%s'
                AND time_to_do = %d
                AND name = '%s'
                """.formatted(dateStart, dateEnd, timeToDo, name);

        Set<Integer> set = SimpleQuery.getIntegerStringMap(
                queryQuizID, "quiz_id", "quiz_id").keySet();
        int quizID;
        if (set.size() == 1) {
            quizID = (Integer) set.toArray()[0];
        } else {
            throw new SQLException("can't find newly inserted quiz");
        }
        return quizID;
    }

    private static void updateQuiz(
            int quizID, String dateStart, String dateEnd,
            int timeToDo, String name) throws SQLException {
        String query = """
                UPDATE quiz
                SET dt_start = '%s',
                    dt_end = '%s',
                    time_to_do = %d,
                    name = '%s'
                WHERE quiz_id = %d
                """.formatted(dateStart, dateEnd, timeToDo, name, quizID);
        SimpleQuery.execute(query);
        SimpleQuery.log("quiz#%d: updated (%s)".formatted(quizID, name));
    }

    private static void deleteQuizQuestions(int quizID) throws SQLException {
        String query = """
                DELETE FROM question_quiz
                WHERE quiz_id =""" + quizID;
        SimpleQuery.execute(query);
        SimpleQuery.log("quiz#%d: deleted all question relations".formatted(quizID));
    }

    private static void insertQuizQuestions(int quizID, List<Integer> questions) throws SQLException {
        for (int questionID : questions) {
            String query = """
                    INSERT INTO question_quiz(question_id, quiz_id)
                    VALUE (%d, %d)
                    """.formatted(questionID, quizID);
            SimpleQuery.execute(query);
        }
        SimpleQuery.log("quiz#%d: added %d questions"
                .formatted(quizID, questions.size()));
    }
}
