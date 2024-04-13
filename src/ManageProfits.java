import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageProfits {

    public static Map<Integer, Double> getInvestorProfit(int clientId) {
        Map<Integer, Double> profitMap = new HashMap<>();

        try (Connection conn = DatabaseConnector.getConnection()) {
            List<Integer> accountIds = DatabaseHelper.getInvestmentAccountIdsByClient(clientId);
            for (Integer accountId : accountIds) {
                double accountProfit = calculateAccountProfit(conn, accountId);
                profitMap.put(accountId, accountProfit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profitMap;
    }

    private static double calculateAccountProfit(Connection conn, int accountId) throws SQLException {
        double totalProfit = 0.0;
        List<String> stocks = getStocksByAccountId(conn, accountId);
        for (String stockSymbol : stocks) {
            double stockProfit = calculateProfit(accountId, stockSymbol);
            totalProfit += stockProfit;
        }
        return totalProfit;
    }

    private static List<String> getStocksByAccountId(Connection conn, int accountId) throws SQLException {
        List<String> stocks = new ArrayList<>();
        String query = "SELECT StockSymbol FROM AccountStocks WHERE AccountID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stocks.add(rs.getString("StockSymbol"));
            }
        }
        return stocks;
    }

    public static double calculateProfit(int accountId, String stockSymbol) {
        double currentPrice = (double) DatabaseHelper.getColumnValue("Stock", "CurrentPrice", "StockSymbol", stockSymbol);
        double sharesOwned = (double) DatabaseHelper.getColumnValue("AccountStocks", "SharesOwned", "StockSymbol", stockSymbol);
        double acb = (double) DatabaseHelper.getColumnValue("AccountStocks", "ACB", "StockSymbol", stockSymbol);

        double marketValue = currentPrice * sharesOwned;
        double profit = marketValue - acb;

        return profit;
    }

}
