import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public interface InvestmentFirmInterface {

    boolean defineSector(String sectorName);

    boolean defineStock(String companyName, String stockSymbol, String sector);

    boolean setStockPrice(String stockSymbol, double perSharePrice);

    boolean defineProfile(String profileName, Map<String, Integer> sectorHoldings);

    int addAdvisor(String advisorName);

    int addClient(String clientName);

    int createAccount(int clientId, int financialAdvisor, String accountName, String profileType, boolean reinvest) throws SQLException;

    boolean tradeShares(int account, String stockSymbol, int sharesExchanged);

    boolean changeAdvisor(int accountId, int newAdvisorId);

    double accountValue(int accountId);

    double advisorPortfolioValue(int advisorId);

    Map<Integer, Double> investorProfit(int clientId);

    Map<String, Integer> profileSectorWeights(int accountId);

    Set<Integer> divergentAccounts(int tolerance);

    int disburseDividend(String stockSymbol, double dividendPerShare) throws SQLException;

    Map<String, Boolean> stockRecommendations(int accountId, int maxRecommendations, int numComparators);

    Set<Set<Integer>> advisorGroups(double tolerance, int maxGroups);
}
