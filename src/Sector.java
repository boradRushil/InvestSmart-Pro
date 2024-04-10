import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Sector {
    private String sectorName;

    public Sector(String sectorName) {
        this.sectorName = sectorName;
    }
    public boolean addSector(String sectorName) {
        if (sectorName == null || sectorName.trim().isEmpty()) {
            System.out.println("Sector name cannot be null or empty.");
            return false;
        }

        String checkQuery = "SELECT COUNT(*) AS count FROM Sector WHERE SectorName = ?";
        String insertQuery = "INSERT INTO Sector (SectorName) VALUES (?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, sectorName);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt("count") > 0) {
                System.out.println("Sector already exists.");
                return false;
            }

            insertStmt.setString(1, sectorName);
            insertStmt.executeUpdate();
            System.out.println("Sector defined successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Getters and Setters
}
