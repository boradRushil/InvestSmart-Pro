import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    // Gets the Profile ID given a profile name
    public static Integer getProfileIdByName(String profileName) {
        String query = "SELECT ProfileID FROM InvestmentProfile WHERE ProfileName = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, profileName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ProfileID");
            }
            return null;
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
    // Gets the value of a particular column based on unique parameters
    public static Object getColumnValue(String tableName, String targetColumn, String uniqueColumn, Object uniqueValue) {
        String query = String.format("SELECT %s FROM %s WHERE %s = ?", targetColumn, tableName, uniqueColumn);
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (uniqueValue instanceof Integer) {
                stmt.setInt(1, (int) uniqueValue);
            } else if (uniqueValue instanceof String) {
                stmt.setString(1, (String) uniqueValue);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(targetColumn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public static Double getAverageCostBase(int accountId, String stockSymbol) {
        String query = "SELECT AVG(ACB) AS AverageCostBase FROM AccountStocks WHERE AccountID = ? AND StockSymbol = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("AverageCostBase");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if ACB cannot be calculated
    }

    public static List<String> getStocksByAccountId(int accountId) {
        List<String> stocks = new ArrayList<>();
        String query = "SELECT StockSymbol FROM AccountStocks WHERE AccountID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stocks.add(rs.getString("StockSymbol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }
}
