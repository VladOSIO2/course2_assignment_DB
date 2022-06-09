package db;

import gui.login.DBSession;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class SimpleQuery {
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

    public static List<String> getStringList(String query, String... colNames) throws SQLException {
        List<String> values = new ArrayList<>();
        Connection con = DBConnector.getInstance().getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            for (String colName : colNames) {
                values.add(rs.getString(colName));
            }
        }
        rs.close();
        st.close();
        return values;
    }


    public static boolean exists(String query) throws SQLException {
        boolean exists = false;
        Connection con = DBConnector.getInstance().getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        if (rs.next()) {
            exists = true;
        }
        rs.close();
        st.close();
        return exists;
    }

    public static Map<Integer, String> getIntegerStringMap(
            String query, String intCol, String strCol
    ) throws SQLException {
        Map<Integer, String> map = new LinkedHashMap<>();
        Connection con = DBConnector.getInstance().getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            map.put(
                    rs.getInt(intCol),
                    rs.getString(strCol)
            );
        }
        rs.close();
        st.close();
        return map;
    }

    public static Integer getInt(String query, String intCol) throws SQLException {
        Integer value = null;
        Connection con = DBConnector.getInstance().getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        if (rs.next()) {
            value = rs.getInt(intCol);
        }
        rs.close();
        st.close();
        return value;
    }

    public static String getString(String query, String strCol) throws SQLException {
        String value = null;
        Connection con = DBConnector.getInstance().getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        if (rs.next()) {
            value = rs.getString(strCol);
        }
        rs.close();
        st.close();
        return value;
    }
}
