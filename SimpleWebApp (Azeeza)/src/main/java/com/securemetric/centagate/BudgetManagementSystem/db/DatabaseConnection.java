package com.securemetric.centagate.BudgetManagementSystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget_management_system", "root", "foo123");
            System.out.println("Database Connection Successfully Established!");
            return connection;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database.getConnection() Error -->" + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close(Connection con) {
        try {
            con.close();
        }
        catch(Exception e) {
        }
    }
}

