package com.klanting.signclick.utils.autoFlush;

import com.klanting.signclick.logicLayer.companyLogic.Company;
import com.klanting.signclick.logicLayer.companyLogic.producible.LicenseSingleton;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSingleton {

    private static final String URL = "jdbc:postgresql://localhost:5432/signclick";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    // Singleton pattern
    private static DatabaseSingleton instance;
    private Connection connection;

    private DatabaseSingleton() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to PostgreSQL successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseSingleton getInstance() {
        if (instance == null) {
            instance = new DatabaseSingleton();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private static String mapJavaTypeToSQL(Class<?> type) {
        if (type == int.class) return "INTEGER";
        if (type == long.class) return "BIGINT";
        if (type == short.class) return "SMALLINT";
        if (type == byte.class) return "SMALLINT";
        if (type == boolean.class) return "BOOLEAN";
        if (type == float.class) return "REAL";
        if (type == double.class) return "DOUBLE PRECISION";
        if (type == char.class) return "CHAR(1)";
        if (type  == String.class) return "VARCHAR";
        return null; // skip non-primitives
    }

    private void checkTable(Class<?> clazz, List<Class<?>> blackList){
        blackList.add(clazz);
        List<String> columns = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields(); // all fields declared in the class
        for (Field field : fields) {
            Class<?> type = field.getType();
            String columnName = field.getName();
            String sqlType = mapJavaTypeToSQL(type);

            if (sqlType != null) { // only primitives
                columns.add(columnName + " " + sqlType);
            }else if (!blackList.contains(type) && type.isAnnotationPresent(ClassFlush.class)){
                checkTable(type, blackList);
            }
        }

        String columnDefs = String.join(",\n    ", columns);
        String tableName = clazz.getSimpleName().toLowerCase(); // optional: lowercase table name

        String tableCreation = String.format("""
                CREATE TABLE %s (
                    %s
                );
                """, tableName, columnDefs);



        try {
            PreparedStatement stmt = connection.prepareStatement(tableCreation);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void checkTables(){
        Class<?> clazz = Company.class;
        checkTable(clazz, new ArrayList<>());


    }
}
