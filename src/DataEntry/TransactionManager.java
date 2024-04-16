package DataEntry;

import DatabaseAccess.DatabaseConnector;
import DatabaseFunctions.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionManager {

    public boolean tradeShares(int accountId, String stockSymbol, int sharesExchanged) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn == null) {
                throw new IllegalStateException("Database connection failed.");
            }

            if (validateTransaction(accountId, stockSymbol, sharesExchanged)) {
                return false;
            }

            // Get the last recorded trade value for the stock
            double sharePrice = DatabaseHelper.getCurrentStockPrice(conn, stockSymbol);
            if (sharePrice == -1) {
                System.out.println("Failed to retrieve share price.");
                return false;
            }

            // Calculate transaction value
            double transactionValue = sharePrice * sharesExchanged;
            double currentACB = DatabaseHelper.getAverageCostBase(conn,accountId, stockSymbol);
            if (updateAccountStatementByTransactionType(accountId, stockSymbol, sharesExchanged, conn, transactionValue, currentACB)) {
                return false;
            }

            System.out.println("Shares traded successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static boolean validateTransaction(int accountId, String stockSymbol, int sharesExchanged) {
        if ( stockSymbol == null || stockSymbol.isEmpty()|| stockSymbol.isBlank()){
            System.out.println("Stock symbol cannot be null or empty.");
            return true;
        }

        if (sharesExchanged == 0) {
            System.out.println("Shares exchanged cannot be zero.");
            return true;
        }

        if (accountId <= 0 ||!DatabaseHelper.entityExistsById("InvestmentAccount", "AccountID", accountId)) {
            System.out.println("Invalid account ID.");
            return true;
        }
        return false;
    }

    private static boolean hasSufficientShares(Connection conn, int accountId, String stockSymbol, int sharesExchanged) throws SQLException {
        String query = "SELECT SharesOwned FROM AccountStocks WHERE AccountID = ? AND StockSymbol = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int sharesOwned = rs.getInt("SharesOwned");
                return sharesOwned >= Math.abs(sharesExchanged); // Check if the account owns enough shares to sell
            }
        }
        return false; // Return false if shares owned not found or insufficient shares
    }

    private static boolean updateAccountStatementByTransactionType(int accountId, String stockSymbol, int sharesExchanged, Connection conn, double transactionValue, double currentACB) throws SQLException {
        if (stockSymbol.equalsIgnoreCase("cash")) {
            // Special case: Transfer cash into or out of the account
            if (!DatabaseHelper.updateCashBalance(conn, accountId, -sharesExchanged)) {
                System.out.println("Failed to update cash balance.");
                return true;
            }
        } else if (sharesExchanged < 0){
            // Check if the account owns the specified number of shares to be sold
            if ( !hasSufficientShares(conn, accountId, stockSymbol, sharesExchanged)) {
                System.out.println("Insufficient shares owned for selling.");
                return true;
            }
            // Sell shares: Update shares owned
            // Buy shares: Update shares owned and calculate ACB
            double newACB = calculateNewACB(currentACB, sharesExchanged, getTotalShares(conn, accountId, stockSymbol));
            updateStockHoldings(conn, accountId, stockSymbol, sharesExchanged, newACB);
            DatabaseHelper.updateCashBalance(conn, accountId, transactionValue); // Deduct transaction value from cash balance
        }
        else {
            double cashBalance = DatabaseHelper.getCashBalance(conn, accountId);
            if (cashBalance < transactionValue) {
                System.out.println("Insufficient cash balance in the account.");
                return true;
            }

            // Buy shares: Update shares owned and calculate ACB
            double newACB = calculateNewACB(currentACB, sharesExchanged, getTotalShares(conn, accountId, stockSymbol));
            updateStockHoldings(conn, accountId, stockSymbol, sharesExchanged, newACB);
            DatabaseHelper.updateCashBalance(conn, accountId, -transactionValue); // Deduct transaction value from cash balance
        }
        return false;
    }

    private static void updateStockHoldings(Connection conn, int accountId, String stockSymbol, int sharesExchanged, double newACB) throws SQLException {
        String query = "INSERT INTO AccountStocks (AccountID, StockSymbol, SharesOwned, ACB) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE SharesOwned = SharesOwned + ?, ACB = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, stockSymbol);
            stmt.setInt(3, sharesExchanged);
            stmt.setDouble(4, newACB);
            stmt.setInt(5, sharesExchanged);
            stmt.setDouble(6, newACB);
            stmt.executeUpdate();
        }
    }

    private static double calculateNewACB(double currentACB, int sharesSold, Integer totalShares) {
        // Calculate the proportion of shares sold
        double proportionSold = (double) sharesSold / totalShares;
        // Calculate the new ACB after selling shares
        double newACB = currentACB * (1 - proportionSold);
        return newACB;
    }

    private static int getTotalShares(Connection conn, int accountId, String stockSymbol) throws SQLException {
        String query = "SELECT SUM(SharesOwned) AS TotalShares FROM AccountStocks WHERE AccountID = ? AND StockSymbol = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("TotalShares");
            }
        }
        return 0; // Return 0 if no shares found or error occurred
    }

}
