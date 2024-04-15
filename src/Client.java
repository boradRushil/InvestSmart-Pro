import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Client {

    // Constructor
    public Client() {
    }

    // Save the client to the database if not already present, otherwise return existing client ID
    public int getClientID(String clientName) {
        // Check if the client already exists in the database
        Integer clientId = DatabaseHelper.getIdByName("Client", "ClientID", "Name", clientName);
        if (clientId == null) {
            System.out.println("Client already exists in the database: " + clientId);
            return -1; // Return -1 if the client already exists
        }

        // Insert the client into the database if not already present
        final String insertQuery = "INSERT INTO Client (Name) VALUES (?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, clientName);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated client ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        clientId = generatedKeys.getInt(1);
                        return clientId; // Return the generated client ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 to indicate failure
    }
}
