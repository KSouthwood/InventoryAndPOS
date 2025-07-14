package com.github.ksouthwood.possystem;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {
    private static final String JDBC_PREFIX = "jdbc:sqlite:";
    private static final String TABLE_NAME = "supplier_orders";

    static final String SUPPLIER = "supplier_name";
    static final String WINE = "wine_type";
    static final String AMOUNT = "amount_purchased";
    static final String PRICE = "price_paid";
    static final String PAID = "is_paid";

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
                stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                             "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                             SUPPLIER + " TEXT NOT NULL," +
                             WINE + " TEXT NOT NULL," +
                             AMOUNT + " INTEGER NOT NULL," +
                             PRICE + " REAL NOT NULL," +
                             PAID + " BOOLEAN NOT NULL)");
            }
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
        }
    }
    
    void addSupplierOrder(final String supplierName, final String wineType, final int amountPurchased,
                                 final double pricePaid, final boolean isPaid) {
        String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                                   TABLE_NAME, SUPPLIER, WINE, AMOUNT, PRICE, PAID);

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

    List<Object[]> getAllRows() {
        List<Object[]> rows = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url)) {
            var resultSet = connection.createStatement().executeQuery("SELECT * FROM " + TABLE_NAME);
            while (resultSet.next()) {
                rows.add(new Object[] {
                        resultSet.getString(SUPPLIER),
                        resultSet.getString(WINE),
                        resultSet.getInt(AMOUNT),
                        resultSet.getDouble(PRICE),
                        resultSet.getBoolean(PAID)
                });
            }
        } catch (SQLException e) {
            System.err.println("Error reading from database: " + e.getMessage());
        }
        return rows;
    }
}
