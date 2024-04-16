package DataEntry;

import DatabaseAccess.DatabaseConnector;
import DatabaseFunctions.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Stock {

    public boolean addStock(String companyName, String stockSymbol, String sectorName) {
        if (validateStock(companyName, stockSymbol, sectorName)) return false;

        Integer sectorId = DatabaseHelper.getIdByName("Sector","SectorID","SectorName", sectorName); // DataEntry.Sector ID
        if (sectorId == null) {
            System.out.println("Error: Failed to retrieve sector ID.");
            return false;
        }

        String insertQuery = "INSERT INTO Stock (CompanyName, StockSymbol, SectorID, CurrentPrice) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            insertStmt.setString(1, companyName);
            insertStmt.setString(2, stockSymbol);
            insertStmt.setInt(3, sectorId);
            insertStmt.setInt(4, 1);
            insertStmt.executeUpdate();
            System.out.println("Stock defined successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean validateStock(String companyName, String stockSymbol, String sectorName) {
        if (companyName == null || companyName.trim().isEmpty() ||
                stockSymbol == null || stockSymbol.trim().isEmpty() ||
                sectorName == null || sectorName.trim().isEmpty()) {
            System.out.println("Error: Company name, stock symbol, and sector name cannot be null or empty.");
            return true;
        }

        if (!DatabaseHelper.entityExistsByName("DataEntry.Sector", "SectorName", sectorName)) {
            System.out.println("Error: DataEntry.Sector does not exist.");
            return true;
        }

        if (DatabaseHelper.entityExistsByName("DataEntry.Stock", "StockSymbol", stockSymbol)) {
            System.out.println("DataEntry.Stock already exists.");
            return true;
        }
        return false;
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
