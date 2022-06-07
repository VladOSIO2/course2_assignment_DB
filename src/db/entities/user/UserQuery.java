package db.entities.user;

import db.DBConnector;
import db.SimpleQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserQuery {
    public static boolean hasLogin(String login) throws SQLException {
        String query = """
                SELECT login FROM user
                WHERE login = '%s'
                """.formatted(login);

        return SimpleQuery.exists(query);
    }

    public static void insertUser(
            String name, String surname, String patronymic,
            String login, String passwordEncrypted,
            UserType type) throws SQLException {
        int typeID = type != null ? type.getId()
                : UserType.RESPONDER.getId(); //lowest type if id is null
        String query = """
                INSERT INTO user(login, password, type_id, name, surname, patronymic)
                VALUE ('%s', '%s', %d, '%s', '%s', '%s')
                """.formatted(login, passwordEncrypted, typeID, name, surname, patronymic);
        SimpleQuery.execute(query);
        SimpleQuery.log("Created " + type + " with login: " + login);
    }
}
