package queuesmart.Database;

import queuesmart.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentDAO {

    public int getStudentIdByUserId(int userId) {

        String sql = "SELECT student_id FROM students WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt("student_id");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding student.");
            e.printStackTrace();
        }

        return 0;
    }

    public int insertStudent(int userId, String studentNumber) {

        String sql = "INSERT INTO students (user_id, student_number) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userId);
            statement.setString(2, studentNumber);

            int affected = statement.executeUpdate();

            if (affected == 0) {
                return 0;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error inserting student.");
            e.printStackTrace();
        }

        return 0;
    }
}