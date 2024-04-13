import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionManager {

    public static boolean tradeShares(int accountId, String stockSymbol, int sharesExchanged) {
        if ( stockSymbol == null || stockSymbol.isEmpty()|| stockSymbol.isBlank()){
            System.out.println("Stock symbol cannot be null or empty.");
            return false;
        }

        if (sharesExchanged == 0) {
            System.out.println("Shares exchanged cannot be zero.");
            return false;
        }

        if (accountId <= 0 ||!DatabaseHelper.entityExistsById("InvestmentAccount", "AccountID", accountId)) {
            System.out.println("Invalid account ID.");
            return false;
        }

        try {
            Connection conn = DatabaseConnector.getConnection();

            // Get the last recorded trade value for the stock
            double sharePrice = getLastTradeValue(conn, stockSymbol);
            if (sharePrice == -1) {
                System.out.println("Failed to retrieve share price. Using default share price of $1.");
                sharePrice = 1.0; // Use default share price if no trade has occurred
            }

            // Calculate transaction value
            double transactionValue = sharePrice * sharesExchanged;

            // Check if the account has sufficient cash balance for buying shares
            if (sharesExchanged > 0) {
                double cashBalance = getCashBalance(conn, accountId);
                if (cashBalance < transactionValue) {
                    System.out.println("Insufficient cash balance in the account.");
                    return false;
                }
            }

            // Update cash balance and stock holdings based on the transaction type
            if (stockSymbol.equalsIgnoreCase("cash")) {
                // Transfer cash into or out of the account
                updateCashBalance(conn, accountId, transactionValue);
            } else {
                // Buy or sell shares
                updateStockHoldingsOnTransaction(conn, accountId, stockSymbol, sharesExchanged);
                updateCashBalance(conn, accountId, -transactionValue); // Deduct transaction value from cash balance
            }

            conn.close();
            System.out.println("Shares traded successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double getLastTradeValue(Connection conn, String stockSymbol) throws SQLException {
        String query = "SELECT CurrentPrice FROM Stock WHERE StockSymbol = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stockSymbol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("LastTradeValue");
            }
        }
        return -1; // Return -1 if no trade value found
    }

    private static double getCashBalance(Connection conn, int accountId) throws SQLException {
        String query = "SELECT CashBalance FROM InvestmentAccount WHERE AccountID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("CashBalance");
            }
        }
        return 0; // Return 0 if account not found or cash balance not available
    }

    private static void updateCashBalance(Connection conn, int accountId, double amount) throws SQLException {
        String query = "UPDATE InvestmentAccount SET CashBalance = CashBalance + ? WHERE AccountID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
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

    private static void updateStockHoldingsOnTransaction(Connection conn, int accountId, String stockSymbol, int sharesExchanged) throws SQLException {
        try {
            double sharePrice = getLastTradeValue(conn, stockSymbol);
            double transactionValue = sharePrice * sharesExchanged;

            // Update stock holdings based on transaction type
            if (sharesExchanged > 0) {
                // Buy shares: Update shares owned and calculate ACB
                double currentACB = DatabaseHelper.getAverageCostBase(accountId, stockSymbol);
                double newACB = calculateNewACB(currentACB, transactionValue, sharesExchanged);
                updateStockHoldings(conn, accountId, stockSymbol, sharesExchanged, newACB);
            } else if (sharesExchanged < 0) {
                // Sell shares: Update shares owned
                updateStockHoldings(conn, accountId, stockSymbol, sharesExchanged, 0); // Set shares owned to 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static double calculateNewACB(double currentACB, double transactionValue, int sharesExchanged) {
        // Calculate the cost of new shares purchased
        double newSharesCost = transactionValue;
        // Add the cost of new shares to the current ACB
        double newACB = currentACB + newSharesCost;
        // Return the updated ACB
        return newACB;
    }


}
