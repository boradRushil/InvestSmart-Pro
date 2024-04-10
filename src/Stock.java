import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock {

    public boolean addStock(String companyName, String stockSymbol, String sectorName) {
        if (companyName == null || companyName.trim().isEmpty() ||
                stockSymbol == null || stockSymbol.trim().isEmpty() ||
                sectorName == null || sectorName.trim().isEmpty()) {
            System.out.println("Error: Company name, stock symbol, and sector name cannot be null or empty.");
            return false;
        }

        if (!DatabaseHelper.entityExistsByName("Sector", "SectorName", sectorName)) {
            System.out.println("Error: Sector does not exist.");
            return false;
        }

        if (DatabaseHelper.entityExistsByName("Stock", "StockSymbol", stockSymbol)) {
            System.out.println("Stock already exists.");
            return false;
        }

        Integer sectorId = DatabaseHelper.getProfileIdByName(sectorName); // Assuming this method now correctly fetches the sector ID
        if (sectorId == null) {
            System.out.println("Error: Failed to retrieve sector ID.");
            return false;
        }

        String insertQuery = "INSERT INTO Stock (CompanyName, StockSymbol, SectorID) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            insertStmt.setString(1, companyName);
            insertStmt.setString(2, stockSymbol);
            insertStmt.setInt(3, sectorId);
            insertStmt.executeUpdate();
            System.out.println("Stock defined successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateStockPrice(String stockSymbol, double perSharePrice) {
        if (stockSymbol == null || stockSymbol.trim().isEmpty() || perSharePrice <= 0) {
            System.out.println("Stock symbol cannot be null or empty, and price must be positive.");
            return false;
        }

        String updateQuery = "UPDATE Stock SET CurrentPrice = ? WHERE StockSymbol = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            updateStmt.setDouble(1, perSharePrice);
            updateStmt.setString(2, stockSymbol);

            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Stock symbol not found.");
                return false;
            }
            System.out.println("Stock price updated successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
