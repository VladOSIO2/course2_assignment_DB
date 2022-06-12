package db.entities.queries;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.List;

public class AnswerQuery {
    public static int getAnswerID(String answer) throws SQLException {
        String query = "SELECT answer_id FROM answer WHERE text = '%s'"
                .formatted(answer);

        Integer id = SimpleQuery.getInt(query, "answer_id");
        return id == null ? 0 : id;
    }

    public static int insertAnswerIfNotExists(String answer) throws SQLException {
        String query = "INSERT INTO answer(text) VALUE ('%s')".formatted(answer);

        int id = getAnswerID(answer);
        if (getAnswerID(answer) == 0) {
            SimpleQuery.execute(query);
            id = getAnswerID(answer);
            SimpleQuery.log("answer #%d: created with text: %s".formatted(id, answer));
        }

        return id;
    }

    public static void deleteAnswerIfNotUsed(int answerID) throws SQLException {
        String queryUsed = """
                SELECT question_id FROM question_answer
                WHERE answer_id =""" + answerID;
        boolean isUsed = SimpleQuery.exists(queryUsed);
        if (!isUsed) {
            String answerText = SimpleQuery.getString(
                    "SELECT text FROM answer WHERE answer_id = " + answerID,
                    "text");
            String query = """
                DELETE FROM answer
                WHERE answer_id =""" + answerID;
            SimpleQuery.execute(query);
            SimpleQuery.log("deleted answer: " + answerText);
        }
    }

    public static List<String> getAnswers(int questionID) throws SQLException {
        String query = """
                SELECT text FROM question_answer
                JOIN answer USING (answer_id)
                WHERE question_id =""" + questionID;

        return SimpleQuery.getStringList(query, "text");
    }

    public static String getRightAnswer(int questionID) throws SQLException {
        String query = """
                SELECT text FROM question_answer
                JOIN answer USING (answer_id)
                WHERE is_right = 1
                AND question_id =""" + questionID;

        return SimpleQuery.getString(query, "text");
    }
}
