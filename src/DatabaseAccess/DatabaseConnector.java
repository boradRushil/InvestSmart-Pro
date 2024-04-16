package DatabaseAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://db.cs.dal.ca:3306/rborad";
    private static final String USER = "rborad";
    private static final String PASSWORD = "B00977837";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
