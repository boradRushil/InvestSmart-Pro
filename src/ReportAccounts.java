import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReportAccounts {
    public static Set<Integer> divergentAccounts(int tolerance) {
        Set<Integer> divergentAccountIds = new HashSet<>();

        // Query to get all investment account IDs
        String query = "SELECT AccountID FROM InvestmentAccount";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int accountId = rs.getInt("AccountID");

                // Get sector weights for the account
                Map<String, Integer> sectorWeights = SectorWeights.profileSectorWeights(accountId);

                // Get the target sector percentages from the investment profile
                Map<String, Integer> targetSectorPercentages = getTargetSectorPercentages(accountId);

                // Check if any sector diverges beyond the tolerance level
                boolean divergent = false;
                for (String sector : sectorWeights.keySet()) {
                    int actualPercentage = sectorWeights.get(sector);
                    int targetPercentage = targetSectorPercentages.getOrDefault(sector, 0);
                    int upperLimit = targetPercentage + tolerance;
                    int lowerLimit = targetPercentage - tolerance;

                    if (actualPercentage > upperLimit || actualPercentage < lowerLimit) {
                        divergent = true;
                        break;
                    }
                }

                // If any sector diverges beyond the tolerance level, add the account to divergentAccountIds
                if (divergent) {
                    divergentAccountIds.add(accountId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return divergentAccountIds;
    }
    public static Map<String, Integer> getTargetSectorPercentages(int accountId) {
        Map<String, Integer> targetSectorPercentages = new HashMap<>();

        // Query to get target sector percentages from the investment profile
        String query = "SELECT PS.Percentage, S.SectorName " +
                "FROM ProfileSector PS " +
                "JOIN Sector S ON PS.SectorID = S.SectorID " +
                "JOIN InvestmentAccount IA ON PS.ProfileID = IA.ProfileID " +
                "WHERE IA.AccountID = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sectorName = rs.getString("SectorName");
                int percentage = rs.getInt("Percentage");
                targetSectorPercentages.put(sectorName, percentage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return targetSectorPercentages;
    }
}
