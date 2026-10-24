package com.canteen.dao;

import com.canteen.DatabaseConnection;
import com.canteen.Token;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TokenDAO {

    public int generateToken(int orderId) {

        String sql = "SELECT COALESCE(MAX(token_number), 0) + 1 AS next_token FROM tokens";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {

                int tokenNumber = resultSet.getInt("next_token");

                String insertSql =
                        "INSERT INTO tokens (order_id, token_number, token_status) " +
                        "VALUES (?, ?, ?)";

                try (PreparedStatement insertStatement =
                             connection.prepareStatement(insertSql)) {

                    insertStatement.setInt(1, orderId);
                    insertStatement.setInt(2, tokenNumber);
                    insertStatement.setString(3, "Waiting");

                    insertStatement.executeUpdate();

                    return tokenNumber;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
    public int getTokenByOrderId(int orderId) {

    String sql = "SELECT token_number FROM tokens WHERE order_id = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setInt(1, orderId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("token_number");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return -1;
}
}