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
        String query = "SELECT SUM((s.CurrentPrice - AS.ACB) * AS.SharesOwned) AS TotalProfit " +
                "FROM AccountStocks AS AS " +
                "JOIN Stock AS s ON AS.StockSymbol = s.Symbol " +
                "WHERE AS.AccountID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalProfit = rs.getDouble("TotalProfit");
            }
        }
        return totalProfit;
    }

}
