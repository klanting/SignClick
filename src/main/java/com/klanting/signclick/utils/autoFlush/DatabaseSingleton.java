package com.klanting.signclick.utils.autoFlush;

import com.klanting.signclick.logicLayer.companyLogic.Company;
import com.klanting.signclick.utils.DataBase;
import com.klanting.signclick.utils.autoFlush.access.InterceptorWrap;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class DatabaseSingleton {


    private final Connection connection;


    // Singleton pattern
    private static DatabaseSingleton instance;

    private DatabaseSingleton() {

        //TODO REMOVE HARDCODED CREDENTIALS
        //DISCLAIMER: NOT UNSAFE BECAUSE ARE DUMMY CREDENTIALS
        String URL = "jdbc:postgresql://localhost:5432/signclick";
        String USER = "postgres";
        String PASSWORD = "postgres";

        DataBase db = new DataBase(URL, USER, PASSWORD);
        this.connection = db.getConnection();
    }

    private DatabaseSingleton(Connection connection) {
        this.connection = connection;
    }

    public static DatabaseSingleton getInstance() {
        if (instance == null) {
            instance = new DatabaseSingleton();
        }
        return instance;
    }

    public static DatabaseSingleton getInstance(Connection connection) {
        if (instance == null) {
            instance = new DatabaseSingleton(connection);
        }
        return instance;
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

    public Map<String, Object> getDataByKey(UUID key, Class<?> clazz){
        String tableName = clazz.getSuperclass().getSimpleName().toLowerCase();
        String sql = "SELECT * FROM " + tableName + " WHERE autoFlushId = ?::uuid";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, key);

            System.out.println(stmt.toString());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Map<String, Object> row = new HashMap<>();

                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = meta.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }

                return row;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    private void checkTable(Class<?> clazz, List<Class<?>> blackList){
        //START REAL CODE

        blackList.add(clazz);
        List<String> columns = new ArrayList<>();
        List<String> foreignKeys = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields(); // all fields declared in the class
        for (Field field : fields) {
            Class<?> type = field.getType();
            String columnName = field.getName();
            String sqlType = mapJavaTypeToSQL(type);

            //Convert list to additional relation entity
            if (type == List.class){

                if (!(field.getGenericType() instanceof ParameterizedType parameterizedType)) {
                    continue;
                }

                Type[] typeArgs = parameterizedType.getActualTypeArguments();

                Type elementType = typeArgs[0];
                if (!(elementType instanceof Class<?> clazz2)) {
                    continue;
                }

                System.out.println(clazz2.getSimpleName());
                System.out.println("D"+ elementType+" "+elementType);
                if (!blackList.contains(clazz2) && type.isAnnotationPresent(ClassFlush.class)){
                    checkTable(clazz2, blackList);
                }


                String tableCreation = String.format("""
                        CREATE TABLE %s (
                        autoFlushId1 UUID NOT NULL,
                        autoFlushId2 UUID NOT NULL,
                        index NOT NULL
                        
                        PRIMARY KEY (autoFlushId1, autoFlushId2, index)
                        );
                        """, clazz.getSimpleName()+"_"+clazz2.getSimpleName());
                //TODO add foreign keys, but only after main table creation, so alter TABLE, with recursion, when main loop is done only

                try {
                    PreparedStatement stmt = connection.prepareStatement(tableCreation);

                    stmt.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                continue;
            }

            if (sqlType != null) { // only primitives
                columns.add(columnName + " " + sqlType);
            }else if (!blackList.contains(type) && type.isAnnotationPresent(ClassFlush.class)){
                checkTable(type, blackList);
                columns.add(columnName+ " UUID");
                foreignKeys.add(String.format("""
                        CONSTRAINT fk_%s
                                FOREIGN KEY (%s) REFERENCES %s
                                ON DELETE SET NULL
                        """, columnName, columnName, type.getSimpleName()+"(autoFlushId)"));
            }
        }
        System.out.println("W "+foreignKeys.toString());
        columns.addAll(foreignKeys);

        String columnDefs = String.join(",\n    ", columns);
        System.out.println("Q "+clazz);
        String tableName = clazz.getSimpleName().toLowerCase(); // optional: lowercase table name

        String tableCreation = String.format("""
                CREATE TABLE %s (
                autoFlushId UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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
        //DEBUG ONLY

        //TODO remove this alter development only
        String d = String.format("""
                DROP SCHEMA public CASCADE;
                CREATE SCHEMA public;
                """);
        try {
            PreparedStatement stmt = connection.prepareStatement(d);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Class<?> clazz = Company.class;
        checkTable(clazz, new ArrayList<>());


    }

    public <T> UUID store(T entity) {
        try {
            Class<T> clazz = (Class<T>)  entity.getClass();

            DatabaseMetaData metaData = connection.getMetaData();

            List<String> columns = new ArrayList<>();
            ResultSet rs = metaData.getColumns(null, "public", clazz.getSimpleName().toLowerCase(), null);

            boolean tableExists = rs.next();
            if (!tableExists){
                checkTable(clazz, new ArrayList<>());
                rs = metaData.getColumns(null, "public", clazz.getSimpleName().toLowerCase(), null);
                tableExists = rs.next();
            }

            while (tableExists) {
                String columnName = rs.getString("COLUMN_NAME");

                if (columnName.equalsIgnoreCase("autoFlushId")){
                    tableExists = rs.next();
                    continue;
                }

                columns.add(columnName);

                tableExists = rs.next();
            }

            //TODO recursively store all storable to which we have a ptr.

            List<Object> values = new ArrayList<>();

            for (String column: columns){
                Field field = clazz.getDeclaredField(column);
                field.setAccessible(true);

                Class<?> type = field.getType();

                Object data = field.get(entity);

                if (type.isAnnotationPresent(ClassFlush.class) && data != null){
                    UUID uuid = store(data);
                    values.add(uuid);
                    continue;
                }

                values.add(data);

            }

            String colNames = String.join(", ", columns);
            String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));

            String insertSql = "INSERT INTO "+clazz.getSimpleName().toLowerCase()+" (" + colNames + ") VALUES (" + placeholders + ") RETURNING autoFlushId";
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);

            for (int i = 0; i < values.size(); i++) {
                insertStmt.setObject(i + 1, values.get(i)); // JDBC is 1-indexed
            }

            ResultSet result = insertStmt.executeQuery();
            boolean suc6 = result.next();
            assert suc6;
            return (UUID) result.getObject(1);


        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public <T> void update(UUID key, T entity) {
        try {
            Class<T> clazz = (Class<T>)  entity.getClass();

            DatabaseMetaData metaData = connection.getMetaData();

            List<String> columns = new ArrayList<>();
            System.out.println("XX "+ clazz.getSimpleName().toLowerCase());
            ResultSet rs = metaData.getColumns(null, "public", clazz.getSimpleName().toLowerCase(), null);

            boolean tableExists = rs.next();
            if (!tableExists){
                checkTable(clazz, new ArrayList<>());
                rs = metaData.getColumns(null, "public", clazz.getSimpleName().toLowerCase(), null);
                tableExists = rs.next();
            }

            while (tableExists) {
                String columnName = rs.getString("COLUMN_NAME");

                if (columnName.equalsIgnoreCase("autoFlushId")){
                    tableExists = rs.next();
                    continue;
                }

                Field field = clazz.getDeclaredField(columnName);
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (type.isAnnotationPresent(ClassFlush.class) && field.get(entity) != null){
                    tableExists = rs.next();
                    continue;
                }

                columns.add(columnName);

                tableExists = rs.next();
            }

            //TODO check references, if UUID same -> all fine, if not or nonexistent update.

            String setClause = columns.stream()
                    .map(c -> c + " = ?")
                    .collect(Collectors.joining(", "));

            String updateSql =
                    "UPDATE " + clazz.getSimpleName().toLowerCase() +
                            " SET " + setClause +
                            " WHERE autoFlushId = ?";

            PreparedStatement insertStmt = connection.prepareStatement(updateSql);

            //TODO recursively store all storable to which we have a ptr.

            List<Object> values = new ArrayList<>();

            for (String column: columns){
                Field field = clazz.getDeclaredField(column);
                field.setAccessible(true);

                Object data = field.get(entity);
                values.add(data);
            }

            for (int i = 0; i < values.size(); i++) {
                insertStmt.setObject(i + 1, values.get(i)); // JDBC is 1-indexed
            }
            insertStmt.setObject(values.size()+1, key);

            insertStmt.executeUpdate();


        }catch (SQLException e){
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public static void clear(){
        instance = null;
    }

    public <T> T getModifiedObject(T entity){
        /*
         * flush to disk here
         * */

        Class<T> clazz = (Class<T>) entity.getClass();

        assert clazz.isAnnotationPresent(ClassFlush.class);

        try {

            /*
             * Store class in SQL
             * */
            UUID id = DatabaseSingleton.getInstance().store(entity);

            /*
             * override class so it contains a UUID
             * */
            Class<? extends T> dynamicType = new ByteBuddy()
                    .subclass(clazz)
                    .defineField("uuid", UUID.class, Modifier.PUBLIC)
                    .method(
                            not(named("clone"))
                    )
                    .intercept(MethodDelegation.to(new InterceptorWrap<T>()))
                    .make()
                    .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            T obj = dynamicType.getDeclaredConstructor().newInstance();
            dynamicType.getField("uuid").set(obj, id);
            return obj;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
