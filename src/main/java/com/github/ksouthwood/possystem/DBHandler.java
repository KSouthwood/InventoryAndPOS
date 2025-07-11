package com.github.ksouthwood.possystem;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHandler {
    private static final String JDBC_PREFIX = "jdbc:sqlite:";
    private final String dbName;
    private final String url;

    public DBHandler(final String dbName) {
        this.dbName = dbName;
        this.url = JDBC_PREFIX + dbName;
        if (!new File(dbName).exists()) {
            System.err.println("Database file does not exist: " + dbName);
            createDB();
        }
    }
    
    private void createDB() {
        try {
            if (!new File(dbName).createNewFile()) {
                System.err.println("Error creating database file: " + dbName + " already exists");
            }
        } catch (Exception e) {
            System.err.println("Error creating database file: " + e.getMessage());
            return;
        }

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS supplier_orders (" +
                             "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                             "supplier_name TEXT NOT NULL," +
                             "wine_type TEXT NOT NULL," +
                             "amount_purchased INTEGER NOT NULL," +
                             "price_paid REAL NOT NULL," +
                             "is_paid BOOLEAN NOT NULL)");
            }
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
        }
    }
    
    void addSupplierOrder(final String supplierName, final String wineType, final int amountPurchased,
                                 final double pricePaid, final boolean isPaid) {
        String sql = "INSERT INTO supplier_orders (supplier_name, wine_type, amount_purchased, price_paid, is_paid) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, supplierName);
            preparedStatement.setString(2, wineType);
            preparedStatement.setInt(3, amountPurchased);
            preparedStatement.setDouble(4, pricePaid);
            preparedStatement.setBoolean(5, isPaid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding supplier order: " + e.getMessage());
        }
    }
}
