import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SectorWeights {

    public static Map<String, Integer> profileSectorWeights(int accountId) {
        Map<String, Integer> sectorWeights = new HashMap<>();

        // Get the total account value
        double accountValue = PortfolioManager.getAccountValue(accountId);
        if (accountValue == -1) {
            System.out.println("Failed to retrieve account value.");
            return sectorWeights;
        }

        // Query to get sector-wise value of stocks in the account
        String query = "SELECT S.SectorName, SUM(AS.SharesOwned * S.CurrentPrice) AS SectorValue " +
                "FROM AccountStocks AS AS " +
                "JOIN Stock AS S ON AS.StockSymbol = S.StockSymbol " +
                "WHERE AS.AccountID = ? " +
                "GROUP BY S.SectorName";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sectorName = rs.getString("SectorName");
                double sectorValue = rs.getDouble("SectorValue");

                // Calculate percentage of sector value in the account
                int sectorPercentage = (int) Math.round((sectorValue / accountValue) * 100);
                sectorWeights.put(sectorName, sectorPercentage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add cash sector with percentage
        sectorWeights.put("cash", 100 - sectorWeights.values().stream().mapToInt(Integer::intValue).sum());

        return sectorWeights;
    }
}
