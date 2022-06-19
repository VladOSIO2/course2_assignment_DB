    package db;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;

    public class DBConnector {
        private static DBConnector instance;
        private final Connection connection;

        private DBConnector() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/db_pr?serverTimezone=UTC&";
            String username = "root";
            String password = "8SfVzhdBST4ZveCz";
            this.connection = DriverManager.getConnection(url, username, password);
        }

        public Connection getConnection() {
            return connection;
        }

        public static DBConnector getInstance() throws SQLException {
            if (instance == null) {
                instance = new DBConnector();
            } else if (instance.getConnection().isClosed()) {
                instance = new DBConnector();
            }

            return instance;
        }
    }
