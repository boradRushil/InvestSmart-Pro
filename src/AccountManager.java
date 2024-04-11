import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountManager {

    public int addAccount(int clientId, int advisorId, String accountName, String profileName, boolean reinvest) {

        if (accountName == null || accountName.trim().isEmpty()) {
            System.out.println("Account name cannot be null or empty, and initial cash balance cannot be negative.");
            return -1; // Return -1 to indicate failure
        }

        if (!DatabaseHelper.entityExistsById("Client", "ClientID", clientId)) {
            System.out.println("Client does not exist.");
            return -1; // Return -1 to indicate failure
        }

        if (!DatabaseHelper.entityExistsById("FinancialAdvisor", "AdvisorID", advisorId)) {
            System.out.println("Financial advisor does not exist.");
            return -1; // Return -1 to indicate failure
        }

        Integer profileId = DatabaseHelper.getProfileIdByName(profileName);
        if (profileId == null) {
            System.out.println("Investment profile does not exist.");
            return -1; // Return -1 to indicate failure
        }

        if (!DatabaseHelper.isAccountNameUniqueForClient(accountName, clientId)) {
            System.out.println("An account with this name already exists for the client.");
            return -1; // Return -1 to indicate failure
        }

        String insertQuery = "INSERT INTO InvestmentAccount (ClientID, AdvisorID, ProfileID, AccountName, ReinvestDividends) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Insert new account
            insertStmt.setInt(1, clientId);
            insertStmt.setInt(2, advisorId);
            insertStmt.setInt(3, profileId);
            insertStmt.setString(4, accountName);
            insertStmt.setBoolean(5, reinvest);
            insertStmt.executeUpdate();

            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newAccountId = generatedKeys.getInt(1);
                System.out.println("Investment account created successfully with ID: " + newAccountId);
                return newAccountId;
            } else {
                System.out.println("Failed to get the new account ID.");
                return -1; // Return -1 to indicate failure
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 to indicate failure
        }
    }

    public static boolean changeAdvisor(int accountId, int newAdvisorId) {
        if (accountId <= 0 || newAdvisorId <= 0) {
            System.out.println("Invalid account ID or advisor ID.");
            return false;
        }

        try {
            Connection conn = DatabaseConnector.getConnection();

            // Update the advisor ID for the specified account
            String updateQuery = "UPDATE InvestmentAccount SET AdvisorID = ? WHERE AccountID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setInt(1, newAdvisorId);
                stmt.setInt(2, accountId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Advisor changed successfully.");
                    return true;
                } else {
                    System.out.println("Failed to change advisor. Account not found.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}



