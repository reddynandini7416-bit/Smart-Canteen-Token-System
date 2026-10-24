package com.canteen.dao;

import com.canteen.DatabaseConnection;
import com.canteen.FoodItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {

    public List<FoodItem> getAvailableFood() {

        List<FoodItem> foodList = new ArrayList<>();

        String sql = "SELECT * FROM food_items WHERE availability = TRUE";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                FoodItem food = new FoodItem(
                        resultSet.getInt("food_id"),
                        resultSet.getString("food_name"),
                        resultSet.getDouble("price"),
                        resultSet.getBoolean("availability")
                );

                foodList.add(food);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return foodList;
    }
}