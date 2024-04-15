import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Advisor {
    // Constructor
    public Advisor() {
    }
    // Save the advisor to the database if not already exists and return the advisor ID
    public int getAdvisorID(String advisorName) {
        // First, check if the advisor already exists
        Integer existingAdvisorId = DatabaseHelper.getIdByName("FinancialAdvisor", "AdvisorID", "Name", advisorName);
        if(existingAdvisorId ==null){
            return -1;
        }
        if (existingAdvisorId != -1) {
            System.out.println("Advisor already exists in the database : " + existingAdvisorId);
            // Advisor already exists, return their ID
            return existingAdvisorId;
        }

        // Advisor does not exist, insert them into the database
        final String insertQuery = "INSERT INTO FinancialAdvisor (Name) VALUES (?)";
        try (Connection conn = DatabaseConnector.getConnection();

             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1,advisorName );
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 to indicate failure
    }

}
