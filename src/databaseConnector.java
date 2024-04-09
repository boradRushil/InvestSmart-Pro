import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.util.Properties;

public class databaseConnector {

    public static void main(String[] args) {

        // Get my identity information

        Properties identity = new Properties();
        String username = "";
        String password = "";

        String propertyFilename = "src/sample.prop";

        try {
            InputStream stream = new FileInputStream( propertyFilename );

            identity.load(stream);

            username = identity.getProperty("username");
            password = identity.getProperty("password");
        } catch (Exception e) {
            return;
        }

        // Do the actual database work now

        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", username, password );
            statement = connect.createStatement();
            statement.execute("use test;");
            resultSet = statement.executeQuery("SELECT * from Sector;");

            while (resultSet.next()) {
                System.out.println("Sector name: " + resultSet.getString("SectorName"));
            }

            resultSet.close();
            statement.close();
            connect.close();
        } catch (Exception e) {
            System.out.println("Connection failed");
            System.out.println(e.getMessage());
        }
    }
}
