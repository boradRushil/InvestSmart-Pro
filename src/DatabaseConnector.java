import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/investment_db";
    private static final String USER = "root";
    private static final String PASSWORD = "rushilb";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
