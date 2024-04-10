import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Advisor {
    private int advisorId;
    private String name;

    // Constructor
    public Advisor(String name) {
        this.name = name;
    }

    // Save the advisor to the database
    public int save() {
        final String insertQuery = "INSERT INTO FinancialAdvisor (Name) VALUES (?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, this.name);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.advisorId = generatedKeys.getInt(1);
                        return this.advisorId;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 to indicate failure
    }

    // Getters and Setters...
}
