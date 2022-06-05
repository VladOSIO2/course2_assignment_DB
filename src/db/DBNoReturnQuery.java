package db;

import gui.login.DBSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBNoReturnQuery {
    public static void execute(String query) throws SQLException {
        Connection conn = DBConnector.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);
        st.close();
    }

    public static void log(String action) throws SQLException {
        String query = """
                INSERT INTO log(user_id, action) VALUE
                (%d, '%s')
                """.formatted(DBSession.getId(), action);
        execute(query);
    }
}
