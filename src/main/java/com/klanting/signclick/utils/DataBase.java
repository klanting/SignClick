package com.klanting.signclick.utils;

import io.ebeaninternal.server.util.Str;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private Connection connection;

    public DataBase(String url, String user, String password){
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to PostgreSQL successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Connection getConnection() {
        return connection;
    }
}
