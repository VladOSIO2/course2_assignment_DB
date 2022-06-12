package db.entities.queries;

import db.SimpleQuery;
import db.entities.Question;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class QuestionQuery {
    public static Map<Integer, String> getQuestions(
            int subjectID, int themeID, int gradeID, String questionPart, boolean isNotInQuizzes
    ) throws SQLException {
        String query = """
                SELECT question_id, text FROM question
                JOIN theme USING (theme_id)
                JOIN subject USING (subject_id)
                WHERE text LIKE '%s'
                """.formatted('%' + questionPart + '%');
        String subjCond = subjectID == -1 ? "" : "\nAND subject_id =" + subjectID;
        String themeCond = themeID == -1 ? "" : "\nAND theme_id =" + themeID;
        String gradeCond = gradeID == -1 ? "" : "\nAND grade_id =" + gradeID;
        String quizCond = !isNotInQuizzes ? "" :
                "\nAND question_id NOT IN (SELECT DISTINCT question_id FROM question_quiz)";
        query += subjCond + themeCond + gradeCond + quizCond;
        return SimpleQuery.getIntegerStringMap(query, "question_id", "text");
    }

    public static boolean isQuizzesHaveQuestion(int questionID) throws SQLException {
        String query = """
                SELECT * FROM question_quiz
                WHERE question_id =""" + questionID;
        return SimpleQuery.exists(query);
    }

    public static String getQuestionText(int questionID) throws SQLException {
        String query = """
                SELECT text FROM question
                WHERE question_id =""" + questionID;
        return SimpleQuery.getString(query, "text");
    }

    public static String deleteQuestion(int questionID) {
        String query = """
                DELETE FROM question
                WHERE question_id =""" + questionID;

        String queryAnswers = """
                DELETE FROM question_answer
                WHERE question_id =""" + questionID;

        try {
            if (isQuizzesHaveQuestion(questionID)) {
                return "Помилка: не можна видалити питання, що є в тестуваннях!";
            }
            String question = getQuestionText(questionID);
            SimpleQuery.execute(queryAnswers);
            SimpleQuery.execute(query);
            SimpleQuery.log("question#%d: deleted (%s)".formatted(questionID, question));
            return "Питання видалено: " + question;
        } catch (SQLException e) {
            return "Помилка SQL при видаленні:\n" + e.getMessage();
        }
    }

    public static String addQuestion(Question q) {
        try {
            if (q.getQuestionID() == -1) {
                insertQuestion(q);
                return "Додано питання: " + q.getText();
            } else {
                updateQuestion(q);
                return "Оновлено питання: " + q.getText();
            }
        } catch (SQLException e) {
            return "Помилка SQL:\n" + e.getMessage();
        }
    }

    private static void insertQuestion(Question q) throws SQLException {
        String queryQuestion = """
                INSERT INTO question(author_id, theme_id, grade_id, text)
                VALUE (%d, %d, %d, '%s')
                """.formatted(q.getAuthorID(), q.getThemeID(), q.getGradeID(), q.getText());
        SimpleQuery.execute(queryQuestion);

        Set<Integer> set = getQuestions(
                -1, q.getThemeID(), q.getGradeID(), q.getText(), true).keySet();
        int questionID;
        if (set.size() == 1) {
            questionID = (Integer) set.toArray()[0];
            q.setQuestionID(questionID);
        } else {
            throw new SQLException("Could not find newly inserted question");
        }
        insertQuestionAnswers(q);

        SimpleQuery.log("question#%d: inserted with %d answers"
                .formatted(questionID, q.getAnswers().size()));
    }

    private static void insertQuestionAnswers(Question q) throws SQLException {
        for (String answer : q.getAnswers()) {
            int answerID = AnswerQuery.insertAnswerIfNotExists(answer);
            String queryAnswer = """
                    INSERT INTO question_answer(question_id, answer_id, is_right)
                    VALUE(%d, %d, %d)
                    """.formatted(q.getQuestionID(), answerID,
                    answer.equals(q.getRightAnswer()) ? 1 : 0);
            SimpleQuery.execute(queryAnswer);
        }
        SimpleQuery.log("question#%d: new answers: %s"
                .formatted(q.getQuestionID(), String.join(", ", q.getAnswers())));
    }

    private static void deleteQuestionAnswers(int questionID) throws SQLException {
        String queryAnswer = """
                DELETE FROM question_answer
                WHERE question_id =""" + questionID;
        SimpleQuery.execute(queryAnswer);
        SimpleQuery.log("question#%d: deleted all answers".formatted(questionID));
    }


    private static void updateQuestion(Question q) throws SQLException {
        String queryQuestion = """
                UPDATE question
                SET theme_id = %d,
                    grade_id = %d,
                    text = '%s'
                WHERE question_id = %d
                """.formatted(q.getThemeID(), q.getGradeID(), q.getText(), q.getQuestionID());

        SimpleQuery.execute(queryQuestion);
        deleteQuestionAnswers(q.getQuestionID());
        insertQuestionAnswers(q);
        SimpleQuery.log("question#%d: updated values".formatted(q.getQuestionID()));
    }
}
