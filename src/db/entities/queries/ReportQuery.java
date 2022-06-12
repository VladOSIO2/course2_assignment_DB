package db.entities.queries;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.List;

public class ReportQuery {
    public static List<String> leastQuestionsThemeInEachSubject() throws SQLException {
        String query = """
                SELECT subject_id,
                       s.name AS subject_name,
                       t.name AS theme_name,
                       COUNT(question_id) AS count
                FROM subject s
                         JOIN theme t USING (subject_id)
                         JOIN question q USING (theme_id)
                GROUP BY theme_id
                HAVING count <= ALL(
                    SELECT COUNT(question_id)
                    FROM theme AS t2
                             JOIN subject s2 USING (subject_id)
                             JOIN question q2 USING (theme_id)
                    WHERE s2.subject_id = s.subject_id
                    GROUP BY theme_id
                )
                """;
        return SimpleQuery.getStringList(query, "subject_name", "theme_name", "count");
    }

    public static List<String> averageSuccess() throws SQLException {
        String query = """
                SELECT quiz_id, name AS q_name, 100 * avg_mark / total_points AS completion FROM (
                 SELECT quiz_id, SUM(points) AS total_points
                 FROM question_quiz
                 JOIN question USING (question_id)
                 JOIN grade USING (grade_id)
                 GROUP BY (quiz_id)
                ) AS A
                JOIN (
                 SELECT quiz_id, AVG(mark) AS avg_mark
                 FROM quiz_completion
                 GROUP BY quiz_id
                ) AS B USING (quiz_id)
                JOIN quiz USING (quiz_id);
                """;
        return SimpleQuery.getStringList(query, "quiz_id", "q_name", "completion");
    }

    public static List<String> getFullResults(int quizID) throws SQLException {
        String query = """
                SELECT responder_id,
                 CONCAT_WS(' ', r.name, surname, patronymic) AS fullname,
                 MAX(mark) AS mark,
                 'Не перебільшено' AS minutes_used,
                 'Здано вчасно' AS timestamp_exceed
                FROM quiz_completion AS qc
                JOIN quiz AS q USING (quiz_id)
                JOIN responder AS r USING (responder_id)
                WHERE quiz_id = %d
                 AND TIMESTAMPDIFF(MINUTE , qc.dt_start, qc.dt_end) <= q.time_to_do
                 AND TIMESTAMPDIFF(MINUTE, qc.dt_end, q.dt_end) >= 0
                GROUP BY fullname
                UNION ALL
                SELECT responder_id,
                 CONCAT_WS(' ', r.name, surname, patronymic) AS fullname,
                 mark,
                 CONCAT('Перебільшено на ', TIMESTAMPDIFF(MINUTE, qc.dt_start,
                qc.dt_end) - q.time_to_do, ' хв') AS minutes_used,
                 'Здано вчасно' AS timestamp_exceed
                FROM quiz_completion AS qc
                JOIN quiz AS q USING (quiz_id)
                JOIN responder AS r USING (responder_id)
                WHERE quiz_id = %d
                 AND TIMESTAMPDIFF(MINUTE , qc.dt_start, qc.dt_end) > q.time_to_do
                 AND TIMESTAMPDIFF(MINUTE, qc.dt_end, q.dt_end) >= 0
                UNION ALL
                SELECT responder_id,
                 CONCAT_WS(' ', r.name, surname, patronymic) AS fullname,
                 mark,
                 CONCAT('Перебільшено на ', TIMESTAMPDIFF(MINUTE, qc.dt_start,
                qc.dt_end) - q.time_to_do, ' хв') AS minutes_used,
                 CONCAT('Здано пізніше на ', TIMESTAMPDIFF(MINUTE, q.dt_end,
                qc.dt_end), ' хв') AS timestamp_exceed
                FROM quiz_completion AS qc
                JOIN quiz AS q USING (quiz_id)
                JOIN responder AS r USING (responder_id)
                WHERE quiz_id = %d
                 AND TIMESTAMPDIFF(MINUTE , qc.dt_start, qc.dt_end) > q.time_to_do
                 AND TIMESTAMPDIFF(MINUTE, qc.dt_end, q.dt_end) < 0
                UNION ALL
                SELECT responder_id,
                 CONCAT_WS(' ', r.name, surname, patronymic) AS fullname,
                 mark,
                 'Не перебільшено' AS minutes_used,
                 CONCAT('Здано пізніше на ', TIMESTAMPDIFF(MINUTE, q.dt_end,
                qc.dt_end), ' хв') AS timestamp_exceed
                FROM quiz_completion AS qc
                 JOIN quiz AS q USING (quiz_id)
                 JOIN responder AS r USING (responder_id)
                WHERE quiz_id = %d
                 AND TIMESTAMPDIFF(MINUTE , qc.dt_start, qc.dt_end) <= q.time_to_do
                 AND TIMESTAMPDIFF(MINUTE, qc.dt_end, q.dt_end) < 0
                """.formatted(quizID, quizID, quizID, quizID);
        return SimpleQuery.getStringList(query, "fullname", "mark", "minutes_used", "timestamp_exceed");
    }
}
