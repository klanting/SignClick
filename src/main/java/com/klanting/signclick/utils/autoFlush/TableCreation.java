package com.klanting.signclick.utils.autoFlush;

import com.klanting.signclick.logicLayer.companyLogic.Company;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableCreation {
    /*
    * Code to dynamically create/update tables
    * */

    // FIX LATER
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void checkTables(){
        Class<?> clazz = Company.class;

        Field[] fields = clazz.getDeclaredFields(); // all fields declared in the class
        for (Field field : fields) {
            System.out.println("Field name: " + field.getName() + ", type: " + field.getType());
        }

        String tableCreation = String.format("""
            CREATE TABLE %s (
            column1 datatype,
            column2 datatype,
            column3 datatype
            );
            """, clazz.getName());

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(tableCreation)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
