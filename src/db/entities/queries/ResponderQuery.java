package db.entities.queries;

import db.SimpleQuery;
import db.entities.user.UserQuery;

import java.sql.SQLException;
import java.util.List;

public class ResponderQuery {
    private static void insertUserAsResponder(List<String> user) throws SQLException {
        String query = """
                INSERT INTO responder(name, surname, patronymic)
                VALUE ('%s', '%s', '%s')
                """.formatted(user.get(0), user.get(1), user.get(2));
        SimpleQuery.execute(query);
        SimpleQuery.log("added author: " + String.join(" ", user));
    }

    public static int getUserAsResponder(int userID) throws SQLException {
        List<String> user = UserQuery.getUserFullname(userID);
        String query = """
                SELECT responder_id FROM responder
                WHERE name = '%s'
                AND surname = '%s'
                AND patronymic = '%s'
                """.formatted(user.get(0), user.get(1), user.get(2));
        Integer i = SimpleQuery.getInt(query, "responder_id");
        if (i == null) {
            insertUserAsResponder(user);
            i = SimpleQuery.getInt(query, "responder_id");
        }
        return i;
    }
}
