package queuesmart.Database;

import queuesmart.DatabaseConnection;
import queuesmart.Models.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public List<Service> getActiveServices() {

        List<Service> services = new ArrayList<>();

        String sql = "SELECT service_id, service_name, description, is_active "
                + "FROM services WHERE is_active = TRUE ORDER BY service_name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                services.add(mapRow(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving services.");
            e.printStackTrace();
        }

        return services;
    }

    private Service mapRow(ResultSet result) throws SQLException {

        return new Service(
                result.getInt("service_id"),
                result.getString("service_name"),
                result.getString("description"),
                result.getBoolean("is_active")
        );
    }
}
