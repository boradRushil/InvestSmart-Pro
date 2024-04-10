import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private int accountId;
    private String accountName;

    public Account(int accountId, String accountName) {
        this.accountId = accountId;
        this.accountName = accountName;
    }

    public boolean addAccount(int clientId, int advisorId, String accountName, String profileName, boolean reinvest, double initialCashBalance) {
        if (accountName == null || accountName.trim().isEmpty() || initialCashBalance < 0) {
            System.out.println("Account name cannot be null or empty, and initial cash balance cannot be negative.");
            return false;
        }

        if (!DatabaseHelper.entityExistsById("Client", "ClientID", clientId)) {
            System.out.println("Client does not exist.");
            return false;
        }

        if (!DatabaseHelper.entityExistsById("FinancialAdvisor", "AdvisorID", advisorId)) {
            System.out.println("Financial advisor does not exist.");
            return false;
        }

        Integer profileId = DatabaseHelper.getProfileIdByName(profileName);
        if (profileId == null) {
            System.out.println("Investment profile does not exist.");
            return false;
        }

        if (!DatabaseHelper.isAccountNameUniqueForClient(accountName, clientId)) {
            System.out.println("An account with this name already exists for the client.");
            return false;
        }
        String insertQuery = "INSERT INTO InvestmentAccount (ClientID, AdvisorID, ProfileID, AccountName, ReinvestDividends, CashBalance) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            // Insert new account
            insertStmt.setInt(1, clientId);
            insertStmt.setInt(2, advisorId);
            insertStmt.setInt(3, profileId);
            insertStmt.setString(4, accountName);
            insertStmt.setBoolean(5, reinvest);
            insertStmt.setDouble(6, initialCashBalance);
            insertStmt.executeUpdate();
            System.out.println("Investment account created successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


