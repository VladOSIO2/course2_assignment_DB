package db.entities.question;

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
}
