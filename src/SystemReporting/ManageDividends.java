package SystemReporting;

import DatabaseAccess.DatabaseConnector;
import DatabaseFunctions.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ManageDividends {
    public int disburseDividendForAccounts(String stockSymbol, double dividendPerShare) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            Map<Integer, Double> accountShares = getAccountShares(conn, stockSymbol);
            double purchasePrice = DatabaseHelper.getCurrentStockPrice(conn, stockSymbol);
            double initialShares = getCurrentFirmShares(conn, stockSymbol);
            double totalDividends = 0;
            double totalPurchasedShares = 0;

            for (Map.Entry<Integer, Double> entry : accountShares.entrySet()) {
                int accountId = entry.getKey();
                double shares = entry.getValue();
                double dividendForAccount = shares * dividendPerShare;
                totalDividends += distributeDividend(conn, accountId, dividendForAccount, purchasePrice, stockSymbol);
                totalPurchasedShares += dividendForAccount;
            }

            double finalShares = initialShares - totalPurchasedShares;
            updateFractionalShares(conn, stockSymbol, finalShares);

            return (int) Math.round(finalShares - initialShares);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private double distributeDividend(Connection conn, int accountId, double dividendForAccount, double purchasePrice, String stockSymbol) throws SQLException {
        boolean reinvest = isReinvestmentAccount(conn, accountId);
        if (!reinvest) {
            DatabaseHelper.updateCashBalance(conn, accountId, dividendForAccount);
        } else {
            double purchasedShares = Math.ceil(dividendForAccount / purchasePrice);
            updateStockHoldings(conn, accountId, stockSymbol, purchasedShares, purchasePrice);
        }
        return dividendForAccount;
    }

    private static Map<Integer, Double> getAccountShares(Connection conn, String stockSymbol) throws SQLException {
        Map<Integer, Double> accountShares = new HashMap<>();
        String query = "SELECT AccountID, SharesOwned FROM AccountStocks WHERE StockSymbol = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int accountId = rs.getInt("AccountID");
                double sharesOwned = rs.getDouble("SharesOwned");
                accountShares.put(accountId, sharesOwned);
            }
        }
        return accountShares;
    }

    private static boolean isReinvestmentAccount(Connection conn, int accountId) throws SQLException {
        String query = "SELECT Reinvest FROM InvestmentAccount WHERE AccountID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("Reinvest");
            }
        }
        return false;
    }
    private double getCurrentFirmShares(Connection conn, String stockSymbol) throws SQLException {
        String query = "SELECT FractionalShares FROM InvestmentFirmShares WHERE StockSymbol = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("FractionalShares");
            }
        }
        return 0; // Return 0 if no shares held by the firm or error occurred
    }

    private static void updateStockHoldings(Connection conn, int accountId, String stockSymbol, double purchasedShares, double purchasePrice) throws SQLException {
        // Update the account's stock holdings with the purchased shares and update the ACB
        String query = "INSERT INTO AccountStocks (AccountID, StockSymbol, SharesOwned, ACB) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE SharesOwned = SharesOwned + ?, ACB = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Get the current ACB for the stock in the account
            double currentACB = DatabaseHelper.getAverageCostBase(conn,accountId, stockSymbol);

            // Calculate the total cost of the purchased shares
            double totalCost = purchasedShares * purchasePrice;

            // Calculate the new total number of shares owned and the new ACB
            double totalShares = getTotalSharesOwned(conn, accountId,stockSymbol) + purchasedShares;
            double newACB = (currentACB * (totalShares - purchasedShares) + totalCost) / totalShares;

            stmt.setInt(1, accountId);
            stmt.setString(2, stockSymbol);
            stmt.setDouble(3, purchasedShares);
            stmt.setDouble(4, newACB);
            stmt.executeUpdate();
        }
    }
    private static double getTotalSharesOwned(Connection conn, int accountId, String stockSymbol) throws SQLException {
        String query = "SELECT SUM(SharesOwned) AS TotalSharesOwned FROM AccountStocks WHERE AccountID = ? AND StockSymbol = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("TotalSharesOwned");
            }
        }
        return 0; // Return 0 if no shares owned or account not found
    }


    private static double updateFractionalShares(Connection conn, String stockSymbol, double purchasedShares) throws SQLException {
        String query = "INSERT INTO InvestmentFirmShares (StockSymbol, FractionalShares) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE FractionalShares = FractionalShares + ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stockSymbol);
            stmt.setDouble(2, purchasedShares - Math.floor(purchasedShares)); // Calculate fractional shares
            stmt.setDouble(3, purchasedShares - Math.floor(purchasedShares)); // Add fractional shares
            stmt.executeUpdate();
        }
        return purchasedShares - Math.floor(purchasedShares); // Return fractional shares
    }
}
