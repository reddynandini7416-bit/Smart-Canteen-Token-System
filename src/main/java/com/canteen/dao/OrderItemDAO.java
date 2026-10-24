package com.canteen.dao;

import com.canteen.DatabaseConnection;
import com.canteen.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    public boolean addOrderItem(OrderItem item) {

        String sql = "INSERT INTO order_items " +
                "(order_id, food_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, item.getOrderId());
            statement.setInt(2, item.getFoodId());
            statement.setInt(3, item.getQuantity());
            statement.setDouble(4, item.getPrice());

            statement.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns all order items for a given order_id, joined with food_items
     * so we also get the food name.
     * Each String[] contains: { food_name, quantity, unit_price, line_total }
     */
    public List<String[]> getItemsByOrderId(int orderId) {

        List<String[]> items = new ArrayList<>();

        String sql = "SELECT f.food_name, oi.quantity, oi.price, " +
                     "       (oi.quantity * oi.price) AS line_total " +
                     "FROM order_items oi " +
                     "JOIN food_items f ON oi.food_id = f.food_id " +
                     "WHERE oi.order_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, orderId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                items.add(new String[]{
                    rs.getString("food_name"),
                    String.valueOf(rs.getInt("quantity")),
                    String.format("%.2f", rs.getDouble("price")),
                    String.format("%.2f", rs.getDouble("line_total"))
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}
