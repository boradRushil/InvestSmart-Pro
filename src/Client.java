import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Client {


    // Constructor
    public Client() {
    }

    // Save the client to the database
    public int save(String clientName) {
        int clientId = 0;
        final String insertQuery = "INSERT INTO Client (Name) VALUES (?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, clientName);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        clientId = generatedKeys.getInt(1);
                        return clientId;
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
