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
        String query = "SELECT SUM(Shares * LastTradeValue) AS StockValue " +
                "FROM Portfolio P JOIN Stock S ON P.StockSymbol = S.StockSymbol " +
                "WHERE AccountID = ?";
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
        String query = "SELECT SUM(StockValue + CashBalance) AS PortfolioValue " +
                "FROM InvestmentAccount WHERE AdvisorID = ?";
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
