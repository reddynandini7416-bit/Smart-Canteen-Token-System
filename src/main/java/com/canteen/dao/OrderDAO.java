package com.canteen.dao;

import com.canteen.DatabaseConnection;
import com.canteen.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class OrderDAO {

    public int createOrder(Order order) {

        String sql = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, order.getUserId());
            statement.setDouble(2, order.getTotalAmount());
            statement.setString(3, order.getStatus());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                return keys.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
    public List<Order> getAllOrders() {

    List<Order> orders = new ArrayList<>();

    String sql = "SELECT * FROM orders ORDER BY order_id DESC";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
           Order order = new Order(
        resultSet.getInt("order_id"),
        resultSet.getInt("user_id"),
        resultSet.getDouble("total_amount"),
        resultSet.getString("status")
);

            orders.add(order);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return orders;
}  
public boolean updateOrderStatus(int orderId, String status) {

    String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, status);
        statement.setInt(2, orderId);

        int rows = statement.executeUpdate();

        return rows > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
public List<String[]> getQueueOrders() {

    List<String[]> queue = new ArrayList<>();

    String sql = "SELECT o.order_id, t.token_number, o.status " +
                 "FROM orders o " +
                 "LEFT JOIN tokens t ON o.order_id = t.order_id " +
                 "ORDER BY o.order_id DESC";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {

            String orderId = String.valueOf(
                    resultSet.getInt("order_id"));

            String token = resultSet.getString("token_number");

            String status = resultSet.getString("status");

            queue.add(new String[]{
                    token,
                    orderId,
                    status
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return queue;
}

    /** Fetch a single order by its order_id. Returns null if not found. */
    public Order getOrderById(int orderId) {

        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, orderId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new Order(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getDouble("total_amount"),
                    rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Look up the order_id for a given token number.
     * Returns -1 if not found.
     */
    public int getOrderIdByTokenNumber(int tokenNumber) {

        String sql = "SELECT order_id FROM tokens WHERE token_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, tokenNumber);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("order_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    }

