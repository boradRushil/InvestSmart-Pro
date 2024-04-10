import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class InvestmentProfile {
    private int profileId;
    private String profileName;
    private Map<String, Integer> sectorHoldings; // Sector name to percentage

    // Constructor
    public InvestmentProfile(String profileName, Map<String, Integer> sectorHoldings) {
        this.profileName = profileName;
        this.sectorHoldings = sectorHoldings;
    }

    // Save the profile and its sector holdings to the database
    public boolean save() {
        final String profileInsertQuery = "INSERT INTO InvestmentProfile (ProfileName) VALUES (?)";
        final String sectorInsertQuery = "INSERT INTO ProfileSector (ProfileID, SectorID, Percentage) VALUES (?, ?, ?)";

        Connection conn = DatabaseConnector.getConnection();
        try (
             PreparedStatement profileStmt = conn.prepareStatement(profileInsertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false); // Start transaction

            // Insert the profile
            profileStmt.setString(1, this.profileName);
            int affectedRows = profileStmt.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            try (ResultSet generatedKeys = profileStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.profileId = generatedKeys.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // Insert sector holdings for the profile
            try (PreparedStatement sectorStmt = conn.prepareStatement(sectorInsertQuery)) {
                for (Map.Entry<String, Integer> entry : this.sectorHoldings.entrySet()) {
                    int sectorId = DatabaseHelper.getProfileIdByName(entry.getKey());
                    if (sectorId == -1) { // Assuming -1 indicates sector not found
                        conn.rollback();
                        return false;
                    }

                    sectorStmt.setInt(1, this.profileId);
                    sectorStmt.setInt(2, sectorId);
                    sectorStmt.setInt(3, entry.getValue());
                    sectorStmt.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback(); // Rollback transaction on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true); // Reset auto-commit to true
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
