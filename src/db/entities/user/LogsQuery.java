package db.entities.user;

import db.DBConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LogsQuery {
    public static List<String> getLogs(String loginPart) throws SQLException {
        String query = """
                SELECT timestamp, login, action FROM log
                JOIN user USING (user_id)
                WHERE login LIKE '%s'
                ORDER BY timestamp DESC
                """.formatted('%' + loginPart + '%');
        List<String> logs = new ArrayList<>();
        Connection con = DBConnector.getInstance().getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            String timestamp = rs.getString("timestamp");
            String login = rs.getString("login");
            String action = rs.getString("action");
            logs.add("[%s] %s: %s".formatted(timestamp, login, action));
        }
        rs.close();
        st.close();
        return logs;
    }
}
