import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.sql.Statement;

public class InvestmentProfile {

    public InvestmentProfile() {

 }
    // Save the profile and its sector holdings to the database
    public boolean updateInvestmentProfile(String profileName, Map<String, Integer> sectorHoldings) {
        if (profileName == null || profileName.trim().isEmpty() || sectorHoldings == null) {
            System.out.println("Profile name and sector holdings map cannot be null or empty.");
            return false;
        }

        // Ensure "cash" sector is included in the sector holdings map
        if (!sectorHoldings.containsKey("cash")) {
            sectorHoldings.put("cash", 0);
        }

        // Calculate total percentage for validation
        int totalPercentage = 0;
        for (int percentage : sectorHoldings.values()) {
            totalPercentage += percentage;
        }

        // Ensure total percentage does not exceed 100%
        if (totalPercentage != 100) {
            System.out.println("Total percentage exceeds 100%. Please adjust sector holdings.");
            return false;
        }

        // Insert the profile into the database
        String insertQuery = "INSERT INTO InvestmentProfile (ProfileName) VALUES (?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Insert the profile
            insertStmt.setString(1, profileName);
            insertStmt.executeUpdate();

            // Get the generated profile ID
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            int profileId;
            if (generatedKeys.next()) {
                profileId = generatedKeys.getInt(1);
            } else {
                System.out.println("Failed to get the generated profile ID.");
                return false;
            }

            // Insert sector holdings into the database
            String sectorInsertQuery = "INSERT INTO ProfileSector (ProfileID, SectorID, Percentage) VALUES (?, ?, ?)";
            for (Map.Entry<String, Integer> entry : sectorHoldings.entrySet()) {
                int sectorId = DatabaseHelper.getProfileIdByName(entry.getKey());
                if (sectorId == -1) {
                    System.out.println("Failed to retrieve sector ID for sector: " + entry.getKey());
                    return false;
                }

                try (PreparedStatement sectorInsertStmt = conn.prepareStatement(sectorInsertQuery)) {
                    sectorInsertStmt.setInt(1, profileId);
                    sectorInsertStmt.setInt(2, sectorId);
                    sectorInsertStmt.setInt(3, entry.getValue());
                    sectorInsertStmt.executeUpdate();
                }
            }

            System.out.println("Investment profile created successfully.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
