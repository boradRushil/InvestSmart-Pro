package DatabaseAccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableCreator {

    // SQL queries for creating tables
    private static final String[] CREATE_TABLE_QUERIES = {
            "CREATE TABLE IF NOT EXISTS Sector (" +
                    "SectorID INT PRIMARY KEY AUTO_INCREMENT," +
                    "SectorName VARCHAR(255) NOT NULL UNIQUE" +
                    ")",

            "CREATE TABLE IF NOT EXISTS Stock (" +
                    "StockSymbol VARCHAR(10) PRIMARY KEY," +
                    "CompanyName VARCHAR(255) NOT NULL," +
                    "SectorID INT," +
                    "CurrentPrice DECIMAL(10, 2)," +
                    "FOREIGN KEY (SectorID) REFERENCES Sector(SectorID)" +
                    ")",

            "CREATE TABLE IF NOT EXISTS Client (" +
                    "ClientID INT PRIMARY KEY AUTO_INCREMENT," +
                    "Name VARCHAR(255) NOT NULL" +
                    ")",

            "CREATE TABLE IF NOT EXISTS FinancialAdvisor (" +
                    "AdvisorID INT PRIMARY KEY AUTO_INCREMENT," +
                    "Name VARCHAR(255) NOT NULL" +
                    ")",

            "CREATE TABLE IF NOT EXISTS InvestmentProfile (" +
                    "ProfileID INT PRIMARY KEY AUTO_INCREMENT," +
                    "ProfileName VARCHAR(255) NOT NULL" +
                    ")",

            "CREATE TABLE IF NOT EXISTS ProfileSector (" +
                    "ProfileID INT," +
                    "SectorID INT," +
                    "Percentage INT," +
                    "PRIMARY KEY (ProfileID, SectorID)," +
                    "FOREIGN KEY (ProfileID) REFERENCES InvestmentProfile(ProfileID)," +
                    "FOREIGN KEY (SectorID) REFERENCES Sector(SectorID)" +
                    ")",

            "CREATE TABLE IF NOT EXISTS InvestmentAccount (" +
                    "AccountID INT PRIMARY KEY AUTO_INCREMENT," +
                    "ClientID INT," +
                    "AdvisorID INT," +
                    "ProfileID INT," +
                    "AccountName VARCHAR(255) NOT NULL," +
                    "ReinvestDividends BOOLEAN," +
                    "CashBalance DECIMAL(15, 2) NOT NULL DEFAULT 0.00," +
                    "FOREIGN KEY (ClientID) REFERENCES Client(ClientID)," +
                    "FOREIGN KEY (AdvisorID) REFERENCES FinancialAdvisor(AdvisorID)," +
                    "FOREIGN KEY (ProfileID) REFERENCES InvestmentProfile(ProfileID)" +
                    ")",

            "CREATE TABLE IF NOT EXISTS AccountStocks (" +
                    "AccountID INT," +
                    "StockSymbol VARCHAR(10)," +
                    "SharesOwned DECIMAL(15, 4)," +
                    "ACB DECIMAL(15, 2)," +
                    "PRIMARY KEY (AccountID, StockSymbol)," +
                    "FOREIGN KEY (AccountID) REFERENCES InvestmentAccount(AccountID)," +
                    "FOREIGN KEY (StockSymbol) REFERENCES Stock(StockSymbol)" +
                    ")",

            "CREATE TABLE IF NOT EXISTS InvestmentFirmShares (" +
                    "StockSymbol VARCHAR(10) NOT NULL," +
                    "FractionalShares DECIMAL(10, 4) NOT NULL," +
                    "PRIMARY KEY (StockSymbol)" +
                    ")"
    };

    // Method to create tables in the database
    public static void createTables() {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Execute each create table query
            for (String query : CREATE_TABLE_QUERIES) {
                stmt.executeUpdate(query);
            }
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
