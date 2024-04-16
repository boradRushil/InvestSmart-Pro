import DataEntry.*;
import DatabaseAccess.TableCreator;
import SystemAnalysis.Groups;
import SystemAnalysis.Recommendations;
import SystemReporting.*;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;


public class InvestmentFirm implements InvestmentFirmInterface{
    Stock stock = new Stock();
    // Constructor
    public InvestmentFirm() {
        // Create tables in the database
        TableCreator.createTables();
    }

    // Define a sector
    public boolean defineSector(String sectorName) {
        Sector sector = new Sector();
        return sector.addSector(sectorName);
    }

    // Define a stock
    public boolean defineStock(String companyName, String stockSymbol, String sector) {

        return stock.addStock(companyName, stockSymbol, sector);
    }

    // Set stock price
    public boolean setStockPrice(String stockSymbol, double perSharePrice) {

        return stock.updateStockPrice(stockSymbol, perSharePrice);
    }

    // Define an investment profile
    public boolean defineProfile(String profileName, Map<String, Integer> sectorHoldings) {
        InvestmentProfile profile = new InvestmentProfile();

        boolean success = profile.updateInvestmentProfile(profileName, sectorHoldings);
        if (success) {
            System.out.println("Profile defined successfully.");
        } else {
            System.out.println("Failed to define profile.");
        }
        return success;
    }

    // Add a financial advisor
    public int addAdvisor(String advisorName) {
        // Implementation
        Advisor advisor = new Advisor();
        int advisorId = advisor.getAdvisorID(advisorName);
        if (advisorId > 0) {
            System.out.println("DataEntry.Advisor added successfully with ID: " + advisorId);
        } else {
            System.out.println("Failed to add advisor.");
        }
        return advisorId;
    }

    // Add a client
    public int addClient(String clientName) {
        Client client = new Client();
        int clientId = client.getClientID(clientName);
        if (clientId > 0) {
            System.out.println("DataEntry.Client added successfully with ID: " + clientId);
        } else {
            System.out.println("Failed to add client.");
        }
        return clientId;
    }

    // Create an account
    public int createAccount(int clientId, int financialAdvisor, String accountName, String profileType, boolean reinvest) throws SQLException {
        // Implementation
        AccountManager account = new AccountManager();
        int accountId = account.addAccount(clientId, financialAdvisor, accountName, profileType, reinvest);
        if (accountId > 0) {
            System.out.println("Account created successfully with ID: " + accountId);
        } else {
            System.out.println("Failed to create account.");
        }
        return account.addAccount(clientId,financialAdvisor,accountName,profileType,reinvest); // Placeholder return
    }

    // Trade shares
    public boolean tradeShares(int account, String stockSymbol, int sharesExchanged) {
        // Delegate transaction handling to TransactionManager class
        TransactionManager transactionManager = new TransactionManager();
        return transactionManager.tradeShares(account, stockSymbol, sharesExchanged);
    }

    // Change advisor
    public boolean changeAdvisor(int accountId, int newAdvisorId) {
        AccountManager account = new AccountManager();

        return account.changeAdvisor(accountId, newAdvisorId);
    }

    // Report account value
    public double accountValue(int accountId) {
        // Implementation
        return PortfolioManager.getAccountValue(accountId); // Placeholder return
    }

    // Report market value managed by an advisor
    public double advisorPortfolioValue(int advisorId) {
        // Implementation
        return PortfolioManager.getAdvisorPortfolioValue(advisorId); // Placeholder return
    }

    // Report profit for a client
    public Map<Integer, Double> investorProfit(int clientId) {

        ManageProfits ManageProfits = new ManageProfits();
        return ManageProfits.getInvestorProfit(clientId); // Placeholder return
    }

    // Compute the proportion of account value held in each sector
    public Map<String, Integer> profileSectorWeights(int accountId) {
        // Implementation
        return SectorWeights.profileSectorWeights(accountId); // Placeholder return
    }

    // Identify accounts diverging from their target investment profile
    public Set<Integer> divergentAccounts(int tolerance) {
        ReportAccounts reportAccounts = new ReportAccounts();
        return reportAccounts.divergentAccounts(tolerance); // Placeholder return
    }

    // Disburse dividend and buy shares as necessary
    public int disburseDividend(String stockSymbol, double dividendPerShare) throws SQLException {
        ManageDividends dividends = new ManageDividends();

        return dividends.disburseDividendForAccounts(stockSymbol,dividendPerShare); // Placeholder return
    }

    // Analyze system methods

    // Recommend stocks to buy or sell for an account
    public Map<String, Boolean> stockRecommendations(int accountId, int maxRecommendations, int numComparators) {
        // Implementation
        Recommendations recommendations = new Recommendations();
        return recommendations.stockRecommendations(accountId,maxRecommendations,numComparators); // Placeholder return
    }

    // Identify groups of advisors with similar investment preferences
    public Set<Set<Integer>> advisorGroups(double tolerance, int maxGroups) {
        Groups groups = new Groups();
        return groups.advisorGroups(tolerance,maxGroups); // Placeholder return
    }

}
