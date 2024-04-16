package SystemAnalysis;

import DatabaseAccess.DatabaseConnector;
import DatabaseFunctions.DatabaseHelper;
import SystemReporting.SectorWeights;

import java.sql.*;
import java.util.*;

public class Recommendations {

    // Implementation of the stockRecommendations method
    public Map<String, Boolean> stockRecommendations(int accountId, int maxRecommendations, int numComparators) {
        Map<String, Boolean> recommendations = new HashMap<>();

        // Step 1: Get stock holdings for the given account
        Map<String, Integer> accountStockHoldings = SectorWeights.profileSectorWeights(accountId);

        // Step 2: Calculate cosine similarity measures for all accounts and select top comparators
        List<Integer> topComparators = calculateTopComparators(accountId, numComparators);

        // Step 3: Analyze stock holdings of selected accounts and determine recommendations
        for (Integer comparatorId : topComparators) {
            Map<String, Integer> comparatorStockHoldings = SectorWeights.profileSectorWeights(comparatorId);
            for (String stock : comparatorStockHoldings.keySet()) {
                boolean recommend = false;
                if (!accountStockHoldings.containsKey(stock)) {
                    // Recommend buying if the majority of comparators hold the stock
                    recommend = true;
                } else if (!comparatorStockHoldings.containsKey(stock)) {
                    // Recommend selling if the majority of comparators don't hold the stock
                    recommend = false;
                } else {
                    // If the stock is held by both, prioritize based on total investment
                    int accountInvestment = accountStockHoldings.get(stock);
                    int comparatorInvestment = comparatorStockHoldings.get(stock);
                    if (comparatorInvestment > accountInvestment) {
                        recommend = true;
                    } else if (comparatorInvestment < accountInvestment) {
                        recommend = false;
                    }
                }
                // Add recommendation to the map
                recommendations.put(stock, recommend);

                // Break loop if maxRecommendations is reached
                if (recommendations.size() >= maxRecommendations) {
                    break;
                }
            }
        }
        return recommendations;
    }
    // Implementation to calculate cosine similarity measures and select top comparators
    private List<Integer> calculateTopComparators(int accountId, int numComparators) {
        // Placeholder implementation
        List<Integer> topComparators = new ArrayList<>();
        // Get all account IDs in the system (excluding the given account)
        List<Integer> allAccounts = DatabaseHelper.getAllAccountIds();

        // Calculate cosine similarity measures
        Map<Integer, Double> similarityScores = new HashMap<>();
        for (Integer otherAccountId : allAccounts) {
            if (otherAccountId != accountId) {
                double similarity = calculateCosineSimilarity(accountId, otherAccountId);
                similarityScores.put(otherAccountId, similarity);
            }
        }
        // Sort the accounts based on similarity scores
        List<Map.Entry<Integer, Double>> sortedScores = new ArrayList<>(similarityScores.entrySet());
        sortedScores.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Select top comparators
        for (int i = 0; i < Math.min(numComparators, sortedScores.size()); i++) {
            topComparators.add(sortedScores.get(i).getKey());
        }

        return topComparators;
    }

    // Placeholder method to calculate cosine similarity between two accounts
    private double calculateCosineSimilarity(int accountId, int otherAccountId) {
        // Implementation to calculate cosine similarity between the stock holdings of two accounts
        // Retrieve stock holdings of the two accounts
        Map<String, Integer> accountStockHoldings = SectorWeights.profileSectorWeights(accountId);
        Map<String, Integer> otherAccountStockHoldings = SectorWeights.profileSectorWeights(otherAccountId);

        // Calculate dot product of the two vectors
        double dotProduct = calculateDotProduct(accountStockHoldings, otherAccountStockHoldings);

        // Calculate magnitude of the vectors
        double magnitudeAccount = calculateMagnitude(accountStockHoldings);
        double magnitudeOtherAccount = calculateMagnitude(otherAccountStockHoldings);

        // Calculate cosine similarity
        double cosineSimilarity = dotProduct / (magnitudeAccount * magnitudeOtherAccount);

        return cosineSimilarity;
    }
    // Method to calculate the dot product of two vectors representing stock holdings
    private double calculateDotProduct(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        double dotProduct = 0.0;

        for (String stockSymbol : vector1.keySet()) {
            if (vector2.containsKey(stockSymbol)) {
                dotProduct += vector1.get(stockSymbol) * vector2.get(stockSymbol);
            }
        }

        return dotProduct;
    }

    // Method to calculate the magnitude of a vector representing stock holdings
    private double calculateMagnitude(Map<String, Integer> vector) {
        double sumOfSquares = 0.0;

        for (double value : vector.values()) {
            sumOfSquares += Math.pow(value, 2);
        }

        return Math.sqrt(sumOfSquares);
    }


}
