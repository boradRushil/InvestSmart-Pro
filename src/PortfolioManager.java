import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PortfolioManager {

    public static double getAccountValue(int accountId) {
        if (accountId <= 0|| !DatabaseHelper.entityExistsById("InvestmentAccount", "AccountID", accountId)) {
            System.out.println("Invalid account ID.");
            return -1;
        }

        try {
            Connection conn = DatabaseConnector.getConnection();

            // Get the market value of stocks in the account
            double stockValue = getStockValue(conn, accountId);

            // Get the cash balance of the account
            double cashBalance = getCashBalance(conn, accountId);

            conn.close();

            // Calculate the total account value
            return stockValue + cashBalance;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static double getAdvisorPortfolioValue(int advisorId) {
        if (advisorId <= 0 || !DatabaseHelper.entityExistsById("InvestmentAccount", "AccountID", advisorId)) {
            System.out.println("Invalid advisor ID.");
            return -1;
        }

        try {
            Connection conn = DatabaseConnector.getConnection();

            // Get the market value of accounts managed by the advisor
            double portfolioValue = getPortfolioValue(conn, advisorId);

            conn.close();

            return portfolioValue;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static double getStockValue(Connection conn, int accountId) throws SQLException {
        String query = "SELECT SUM(AS.SharesOwned * S.CurrentPrice) AS StockValue " +
                "FROM AccountStocks AS AS " +
                "JOIN Stock AS S ON AS.StockSymbol = S.StockSymbol " +
                "WHERE AS.AccountID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("StockValue");
            }
        }
        return 0; // Return 0 if no stocks found or account not found
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

    private static double getPortfolioValue(Connection conn, int advisorId) throws SQLException {
        String query = "SELECT SUM(IA.CashBalance + COALESCE(StockValue, 0)) AS PortfolioValue " +
                "FROM InvestmentAccount AS IA " +
                "LEFT JOIN (SELECT AccountID, SUM(AS.SharesOwned * S.CurrentPrice) AS StockValue " +
                "           FROM AccountStocks AS AS " +
                "           JOIN Stock AS S ON AS.StockSymbol = S.StockSymbol " +
                "           GROUP BY AccountID) AS AV ON IA.AccountID = AV.AccountID " +
                "WHERE IA.AdvisorID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, advisorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("PortfolioValue");
            }
        }
        return 0; // Return 0 if no accounts managed by the advisor or advisor not found
    }
}
