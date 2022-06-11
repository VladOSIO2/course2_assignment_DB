package db.entities.question;

import db.SimpleQuery;

import java.sql.SQLException;

public class AnswerQuery {
    public static int getAnswerID(String answer) throws SQLException {
        String query = "SELECT answer_id FROM answer WHERE text = '%s'"
                .formatted(answer);

        return SimpleQuery.getInt(query, "answer_id");
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

    public static void deleteAnswer(int answerID) {

    }
}
