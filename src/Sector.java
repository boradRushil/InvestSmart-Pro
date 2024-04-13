import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Sector {

    public boolean addSector(String sectorName) {
        if (sectorName == null || sectorName.trim().isEmpty()) {
            System.out.println("Sector name cannot be null or empty.");
            return false;
        }

        if (DatabaseHelper.entityExistsByName("Sector", "SectorName", sectorName)) {
            System.out.println("Sector already exists.");
            return false;
        }

        String insertQuery = "INSERT INTO Sector (SectorName) VALUES (?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            insertStmt.setString(1, sectorName);
            insertStmt.executeUpdate();
            System.out.println("Sector defined successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

