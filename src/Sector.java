import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Sector {

    public boolean addSector(String sectorName) {

        if (validateSector(sectorName)) return false;

        String insertQuery = "INSERT INTO Sector (SectorName) VALUES (?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            insertStmt.setString(1, sectorName);
            insertStmt.executeUpdate();
            System.out.println("Sector defined successfully.");
            if (!DatabaseHelper.entityExistsByName("Sector", "SectorName", "cash")) {
                insertStmt.setString(1, "cash");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateSector(String sectorName) {
        if (sectorName == null || sectorName.trim().isEmpty()) {
            System.out.println("Sector name cannot be null or empty.");
            return true;
        }
        // Check if the "cash" sector exists, and if not, insert it
        if (!DatabaseHelper.entityExistsByName("Sector", "SectorName", "cash")) {
            insertCashSector();
        }

        if (DatabaseHelper.entityExistsByName("Sector", "SectorName", sectorName)) {
            System.out.println("Sector already exists.");
            return true;
        }
        return false;
    }

    // Helper method to insert the "cash" sector
    private void insertCashSector() {
        String insertCashQuery = "INSERT INTO Sector (SectorName) VALUES (?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertCashQuery)) {

            insertStmt.setString(1, "cash");
            insertStmt.executeUpdate();
            System.out.println("Cash sector defined successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

