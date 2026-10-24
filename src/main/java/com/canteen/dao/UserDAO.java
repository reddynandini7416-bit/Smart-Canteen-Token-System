package com.canteen.dao;

import com.canteen.DatabaseConnection;
import com.canteen.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Returns the user's full name for a given user_id, or "Unknown" if not found. */
    public String getNameByUserId(int userId) {

        String sql = "SELECT name FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    /**
     * Registers a new user and returns the saved User (with the generated user_id),
     * or null if the username already exists or the insert fails.
     */
    public User registerUser(String name, String username, String password) {

        // 1. Check for duplicate username
        String checkSql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement check = connection.prepareStatement(checkSql)) {

            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                return null;   // username already taken
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // 2. Insert new user and return the saved User object
        String insertSql =
            "INSERT INTO users (name, username, password) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                return new User(newId, name, username, password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
