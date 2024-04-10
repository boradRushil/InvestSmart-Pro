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

        // SQL queries
        String clientExistsQuery = "SELECT COUNT(*) FROM Client WHERE ClientID = ?";
        String advisorExistsQuery = "SELECT COUNT(*) FROM FinancialAdvisor WHERE AdvisorID = ?";
        String profileQuery = "SELECT ProfileID FROM InvestmentProfile WHERE ProfileName = ?";
        String accountExistsQuery = "SELECT COUNT(*) FROM InvestmentAccount WHERE AccountName = ? AND ClientID = ?";
        String insertQuery = "INSERT INTO InvestmentAccount (ClientID, AdvisorID, ProfileID, AccountName, ReinvestDividends, CashBalance) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement clientExistsStmt = conn.prepareStatement(clientExistsQuery);
             PreparedStatement advisorExistsStmt = conn.prepareStatement(advisorExistsQuery);
             PreparedStatement profileStmt = conn.prepareStatement(profileQuery);
             PreparedStatement accountExistsStmt = conn.prepareStatement(accountExistsQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Check if client exists
            clientExistsStmt.setInt(1, clientId);
            if (!entityExists(clientExistsStmt)) {
                System.out.println("Client does not exist.");
                return false;
            }

            // Check if advisor exists
            advisorExistsStmt.setInt(1, advisorId);
            if (!entityExists(advisorExistsStmt)) {
                System.out.println("Financial advisor does not exist.");
                return false;
            }

            // Get profile ID
            profileStmt.setString(1, profileName);
            ResultSet rsProfile = profileStmt.executeQuery();
            if (!rsProfile.next()) {
                System.out.println("Investment profile does not exist.");
                return false;
            }
            int profileId = rsProfile.getInt("ProfileID");

            // Check if account name is unique for the client
            accountExistsStmt.setString(1, accountName);
            accountExistsStmt.setInt(2, clientId);
            if (entityExists(accountExistsStmt)) {
                System.out.println("An account with this name already exists for the client.");
                return false;
            }

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

    private boolean entityExists(PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

}
