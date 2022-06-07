package gui.login;

import db.DBConnector;
import db.SimpleQuery;
import db.entities.user.UserType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBSession {
    private static UserType type = null;

    static boolean init(String userLogin, String userPassword) throws SQLException {
        boolean isInitialized = false;

        String query = """
            SELECT ut.name, u.user_id FROM user AS u
            JOIN user_type AS ut USING (type_id)
            WHERE u.login = '%s'
            AND u.password = '%s'
            """.formatted(userLogin, userPassword);

        Connection connection = DBConnector.getInstance().getConnection();
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);
        if (rs.next()) {
            type = UserType.valueOf(rs.getString("name"));
            SimpleQuery.log("user logged in");
            isInitialized = true;
        }
        rs.close();
        st.close();
        return isInitialized;
    }

    public static UserType getType() {
        return type;
    }

    public static int getId() {
        return type == null ? 0 : type.getId();
    }

    public static void logOut() throws SQLException {
        if (type != null) {
            SimpleQuery.log("user logged out");
            type = null;
        }
    }
}
