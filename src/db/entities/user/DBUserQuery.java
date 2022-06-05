package db.entities.user;

import db.DBConnector;
import db.DBNoReturnQuery;
import db.DBUserType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUserQuery {
    public static boolean hasLogin(String login) throws SQLException {
        boolean hasLogin = false;
        String query = """
                SELECT login FROM user
                WHERE login = '%s'
                """.formatted(login);

        Connection con = DBConnector.getInstance().getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        if (rs.next()) {
            hasLogin = true;
        }
        rs.close();
        st.close();
        return hasLogin;
    }

    public void insertUser(
            String name, String surname, String patronymic,
            String login, String passwordEncrypted,
            DBUserType type) throws SQLException {
        int typeID = type != null ? type.getId()
                : DBUserType.RESPONDER.getId(); //lowest type if id is null
        String query = """
                INSERT INTO user(login, password, type_id, name, surname, patronymic)
                VALUE ('%s', '%s', %d, '%s', '%s', '%s')
                """.formatted(login, passwordEncrypted, typeID, name, surname, patronymic);
        DBNoReturnQuery.execute(query);
    }
}
