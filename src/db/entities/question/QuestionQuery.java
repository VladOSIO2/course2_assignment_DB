package db.entities.question;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.Map;

public class QuestionQuery {
    public static Map<Integer, String> getQuestions(
            int themeID, int gradeID, String questionPart, boolean isNotInQuizzes
    ) throws SQLException {
        String query = """
                SELECT question_id, text FROM question
                JOIN theme USING (theme_id)
                JOIN subject USING (subject_id)
                WHERE text LIKE '%s'
                """.formatted('%' + questionPart + '%');
        //String subjCond = themeID == -1 ? "" : "\nAND subject_id =" + subjectID;
        String themeCond = themeID == -1 ? "" : "\nAND theme_id =" + themeID;
        String gradeCond = gradeID == -1 ? "" : "\nAND grade_id =" + gradeID;
        String quizCond = !isNotInQuizzes ? "" :
                "\nAND question_id NOT IN (SELECT DISTINCT question_id FROM question_quiz)";
        query += themeCond + gradeCond + quizCond;
        return SimpleQuery.getIntegerStringMap(query, "question_id", "text");
    }
}
