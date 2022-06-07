package db.entities.question;

import db.SimpleQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SubjectQuery {
    public static Map<Integer, String> searchSubject(String subjectPart) throws SQLException {
        String query = """
                SELECT subject_id, name FROM subject
                WHERE name LIKE '%s'
                """.formatted('%' + subjectPart + '%');
        return SimpleQuery.getIntegerStringMap(query, "subject_id","name");
    }
}
