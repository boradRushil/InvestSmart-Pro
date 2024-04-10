import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
