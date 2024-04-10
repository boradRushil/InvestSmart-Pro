import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock {
    private String companyName;
    private String stockSymbol;
    private String sector;

    public Stock(String companyName, String stockSymbol, String sector) {
        this.companyName = companyName;
        this.stockSymbol = stockSymbol;
        this.sector = sector;
    }

    // Getters and Setters
    public boolean addStock(String companyName, String stockSymbol, String sectorName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            System.out.println("Error: Company name cannot be null or empty.");
            return false;
        }

        if (stockSymbol == null || stockSymbol.trim().isEmpty()) {
            System.out.println("Error: Stock symbol cannot be null or empty.");
            return false;
        }

        if (sectorName == null || sectorName.trim().isEmpty()) {
            System.out.println("Error: Sector name cannot be null or empty.");
            return false;
        }


        String sectorQuery = "SELECT SectorID FROM Sector WHERE SectorName = ?";
        String stockExistsQuery = "SELECT COUNT(*) FROM Stock WHERE StockSymbol = ?";
        String insertQuery = "INSERT INTO Stock (CompanyName, StockSymbol, SectorID) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement sectorStmt = conn.prepareStatement(sectorQuery);
             PreparedStatement stockExistsStmt = conn.prepareStatement(stockExistsQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Check if sector exists
            sectorStmt.setString(1, sectorName);
            ResultSet rsSector = sectorStmt.executeQuery();
            if (!rsSector.next()) {
                System.out.println("Sector does not exist.");
                return false;
            }
            int sectorId = rsSector.getInt("SectorID");

            // Check if stock exists
            stockExistsStmt.setString(1, stockSymbol);
            ResultSet rsStock = stockExistsStmt.executeQuery();
            if (rsStock.next() && rsStock.getInt(1) > 0) {
                System.out.println("Stock already exists.");
                return false;
            }

            // Insert new stock
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

    public boolean stockPrice(String stockSymbol, double perSharePrice) {
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
