package DatabaseFunctions;

import DatabaseAccess.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    // Gets the ID given a  name
    public static Integer getIdByName(String tableName, String nameColumn1, String nameColumn2, String name) {
        String query = "SELECT " + nameColumn1 + " FROM " + tableName + " WHERE " + nameColumn2 + " = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(nameColumn1);
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Checks if an entity exists by ID in the specified table
    public static boolean entityExistsById(String tableName, String idColumn, int id) {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", tableName, idColumn);
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Checks if an entity with a specific name exists in the specified table
    public static boolean entityExistsByName(String tableName, String nameColumn, String name) {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", tableName, nameColumn);
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Example of a method to check for unique account name for a client
    public static boolean isAccountNameUniqueForClient(String accountName, int clientId) {
        String query = "SELECT COUNT(*) FROM InvestmentAccount WHERE AccountName = ? AND ClientID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountName);
            stmt.setInt(2, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static double getSharesOwned(Connection conn,int accountId, String stockSymbol) {
            String query = "SELECT SharesOwned FROM AccountStocks WHERE AccountID = ? AND StockSymbol = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, accountId);
                stmt.setString(2, stockSymbol);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("SharesOwned");
                }
            }
         catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0.0 if shares owned not found or error occurred
    }


    public static List<Integer> getInvestmentAccountIdsByClient(int clientId) {
        List<Integer> accountIds = new ArrayList<>();
        String query = "SELECT AccountID FROM InvestmentAccount WHERE ClientID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accountIds.add(rs.getInt("AccountID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountIds;
    }

    public static Double getAverageCostBase(Connection conn ,int accountId, String stockSymbol) {
        String query = "SELECT AVG(ACB) AS AverageCostBase FROM AccountStocks WHERE AccountID = ? AND StockSymbol = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("AverageCostBase");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return null if ACB cannot be calculated
    }
    public static double getCurrentStockPrice(Connection conn, String stockSymbol) throws SQLException {
        String query = "SELECT CurrentPrice FROM Stock WHERE StockSymbol = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("CurrentPrice");
            }
        }
        return -1; // Return -1 if no trade value found
    }
    public static double getCashBalance(Connection conn, int accountId) throws SQLException {
        String query = "SELECT CashBalance FROM InvestmentAccount WHERE AccountID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("CashBalance");
            }
        }
        return 0; // Return 0 if account not found or cash balance not available
    }
    public static boolean updateCashBalance(Connection conn, int accountId, double amount) throws SQLException {
        String query = "UPDATE InvestmentAccount SET CashBalance = CashBalance + ? WHERE AccountID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
            return true;
        }
    }

    // Method to retrieve all account IDs from the database
    public static List<Integer> getAllAccountIds() {
        List<Integer> accountIds = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT AccountID FROM InvestmentAccount";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                // Iterate through the result set and add account IDs to the list
                while (resultSet.next()) {
                    int accountId = resultSet.getInt("AccountID");
                    accountIds.add(accountId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any errors gracefully
        }

        return accountIds;
    }
}
