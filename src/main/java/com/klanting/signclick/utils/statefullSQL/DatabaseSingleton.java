package com.klanting.signclick.utils.statefullSQL;

import com.klanting.signclick.utils.DataBase;
import com.klanting.signclick.utils.statefullSQL.access.InterceptorWrap;
import com.klanting.signclick.utils.statefullSQL.access.UuidFunction;
import com.klanting.signclick.utils.statefullSQL.internal.ListWrapper;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import org.gradle.internal.impldep.org.objenesis.Objenesis;
import org.gradle.internal.impldep.org.objenesis.ObjenesisStd;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

record ListInsertEntry(String variable, UUID autoFlushId2, int index, String listTableName) {}

public class DatabaseSingleton {

    private final List<SQLSerializer> serializers = new ArrayList<>();

    public void registerSerializer(SQLSerializer serializer){
        serializers.add(serializer);
    }

    public <S> String serialize(Class<?> type, S value){
        for (SQLSerializer s: serializers){
            if (s.getType().equals(type)){
                return s.serialize(value);
            }
        }
        return null;
    }

    public <S> boolean hasSerializer(Class<S> type){
        for (SQLSerializer s: serializers){
            if (s.getType().equals(type)){
                return true;
            }
        }
        return false;
    }

    public <S> S deserialize(Class<S> type, String value){
        for (SQLSerializer s: serializers){
            if (s.getType().equals(type)){
                return (S) s.deserialize(value);
            }
        }
        return null;
    }


    private final Connection connection;


    // Singleton pattern
    private static DatabaseSingleton instance;

    public Connection getConnection() {
        return connection;
    }

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
        if (type == Integer.class) return "INTEGER";
        if (type == long.class) return "BIGINT";
        if (type == short.class) return "SMALLINT";
        if (type == byte.class) return "SMALLINT";
        if (type == boolean.class) return "BOOLEAN";
        if (type == float.class) return "REAL";
        if (type == double.class) return "DOUBLE PRECISION";
        if (type == char.class) return "CHAR(1)";
        if (type == String.class) return "VARCHAR";
        return null;
    }

    public <T> T wrap(Class<?> clazz, @NotNull Map<String, Object> values){
        /**
        * gets class and list of values, and initialize object with its values and autoFlushId
        * */
        assert !(ByteBuddyEnhanced.class.isAssignableFrom(clazz));

        /*
        * initialize object without constructor
        * */
        Objenesis objenesis = new ObjenesisStd();

        /*
        * override base object with bytebuddy
        * this doesn't intercept, but embeds autoFlushId as field to instance
        * */
        Class<?> dynamicType = new ByteBuddy()
                .subclass(clazz)
                .implement(ByteBuddyEnhanced.class)
                .defineField("autoFlushId", UUID.class, Modifier.PUBLIC)
                .method(
                        named("equals")
                )
                .intercept(MethodDelegation.to(new InterceptorWrap<T>()))
                .make()
                .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        assert (ByteBuddyEnhanced.class.isAssignableFrom(dynamicType));

        T instance = (T) objenesis.newInstance(dynamicType);

        /*
        * Set each field manually
        * */
        for (var entry : values.entrySet()) {

            if (entry.getKey().equals("autoflushid")){
                try {
                    var field = dynamicType.getDeclaredField("autoFlushId");
                    field.setAccessible(true);
                    field.set(instance, entry.getValue());
                }catch (Exception e){
                    throw new RuntimeException("AutoFlushId is not a valid field");
                }

                continue;
            }

            try {
                /*
                * get target field from base clazz
                * */
                var field = clazz.getDeclaredField(entry.getKey());

                Class<?> type = field.getType();

                /*
                * When ptr to other class
                * */
                if (type.isAnnotationPresent(ClassFlush.class) && entry.getValue() != null){

                    //other than above this one intercepts
                    Class<?> dynamicType2 = new ByteBuddy()
                            .subclass(type)
                            .implement(ByteBuddyEnhanced.class)
                            .defineField("autoFlushId", UUID.class, Modifier.PUBLIC)
                            .method(
                                    not(named("clone"))
                            )
                            .intercept(MethodDelegation.to(new InterceptorWrap<T>()))
                            .make()
                            .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                            .getLoaded();

                    Object obj = dynamicType2.getDeclaredConstructor().newInstance();
                    dynamicType2.getField("autoFlushId").set(obj, entry.getValue());

                    field.setAccessible(true);
                    field.set(instance, obj);

                    continue;
                }

                /*
                * map field
                * */
                field.setAccessible(true);
                if(mapJavaTypeToSQL(type) != null) {
                    field.set(instance, entry.getValue());
                }else if(type == List.class){
                    field.set(instance, entry.getValue());
                }else{
                    field.set(instance, deserialize(type, (String) entry.getValue()));
                }

            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        assert instance instanceof ByteBuddyEnhanced;

        return instance;
    }

    public <T> List<T> getAll(String groupName, String accessMethodType, Class<T> clazz) {
        /**
        * get all elements for a given, groupName, accessMethodType and Clazz
        * Clazz: plain class
        * */
        assert !(ByteBuddyEnhanced.class.isAssignableFrom(clazz));

        String tableName = getTableName(clazz);
        String sql = "SELECT t.* FROM " + tableName+ " t JOIN statefullSQL" + accessMethodType +" o ON t.autoflushid = o.autoflushid JOIN StatefullSQL s ON o.id = s.id WHERE s.groupname = ?";

        List<T> entities = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, groupName);

            ResultSet rs = stmt.executeQuery();

            /*
            * Go over each loaded row
            * */
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = meta.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }

                T instance = wrap(clazz, row);
                entities.add(instance);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (T entity: entities){
            assert entity instanceof ByteBuddyEnhanced;
        }

        return entities;
    }

    public <T> T getObjectByKey(UUID key, Class<?> clazz){

        assert !(ByteBuddyEnhanced.class.isAssignableFrom(clazz));

        Map<String, Object> values = DatabaseSingleton.getInstance().getDataByKey(key, clazz);
        T instance = DatabaseSingleton.getInstance().wrap(clazz, values);

        assert instance instanceof ByteBuddyEnhanced;

        return instance;
    }

    public static String getTableName(Class<?> clazz){
        return clazz.getSimpleName().toLowerCase();
    }

    private Map<String, Object> getDataByKey(UUID key, Class<?> clazz){
        /*
        * Provide autoFlushId and get the corresponding values
        * Clazz: plain class
        * */
        assert !(ByteBuddyEnhanced.class.isAssignableFrom(clazz));

        String tableName = getTableName(clazz);
        String sql = "SELECT * FROM " + tableName + " WHERE autoFlushId = ?::uuid";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, key);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Map<String, Object> row = new HashMap<>();

                ResultSetMetaData meta = rs.getMetaData();
                /*
                * load simple attributes
                * */
                int columnCount = meta.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = meta.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }

                /*
                * load internal lists
                * */
                for(Field field: clazz.getDeclaredFields()){
                    if(field.getType() != List.class){
                        continue;
                    }

                    if (!(field.getGenericType() instanceof ParameterizedType parameterizedType)) {
                        continue;
                    }
                    Type[] typeArgs = parameterizedType.getActualTypeArguments();
                    Type elementType = typeArgs[0];
                    if (!(elementType instanceof Class<?> clazz2)) {
                        continue;
                    }
                    if(!clazz2.isAnnotationPresent(ClassFlush.class)){
                        continue;
                    }
                    String listTableName = getTableName(clazz)+"_list_"+getTableName(clazz2);
                    String name = field.getName();

                    List<Object> internalList = new ListWrapper<>(listTableName, key, clazz2, name);
                    row.put(name, internalList);

                }

                return row;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public UUID getIdByGroup(String groupName, String type){
        String sql = "SELECT * FROM StatefullSQL WHERE groupName = ? AND type = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, groupName);
            stmt.setObject(2, type);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return (UUID) rs.getObject(1);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public boolean tableExists(String tableName){

        try {
            DatabaseMetaData meta = connection.getMetaData();

            ResultSet rs = meta.getTables(
                    null,          // catalog
                    "public",      // schema (case-sensitive in some DBs)
                    tableName.toLowerCase(),       // table name
                    new String[]{"TABLE"});

            boolean exists = rs.next();
            return exists;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void checkSetupTable(String name, String type){

        if (!tableExists("StatefullSQL")){
            String tableCreation = String.format("""
                        CREATE TABLE %s (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        type VARCHAR NOT NULL,
                        groupName VARCHAR NOT NULL
                        );
                        """, "StatefullSQL");

            try {
                PreparedStatement stmt = connection.prepareStatement(tableCreation);

                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        if (!tableExists("StatefullSQLOrderedList")){
            String tableCreation = String.format("""
                        CREATE TABLE %s (
                        id UUID NOT NULL,
                        index INT NOT NULL,
                        autoFlushId UUID NOT NULL,
                        PRIMARY KEY(id, index) DEFERRABLE INITIALLY DEFERRED
                        );
                        """, "StatefullSQLOrderedList");

            try {
                PreparedStatement stmt = connection.prepareStatement(tableCreation);

                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        if (!tableExists("StatefullSQLMapDict")){
            /*
            * key can be UUID (of real key), INT or STRING (but always serialize to string)
            * */
            String tableCreation = String.format("""
                        CREATE TABLE %s (
                        id UUID NOT NULL,
                        key VARCHAR NOT NULL,
                        autoFlushId UUID NOT NULL,
                        PRIMARY KEY(id, key) DEFERRABLE INITIALLY DEFERRED
                        );
                        """, "StatefullSQLMapDict");

            try {
                PreparedStatement stmt = connection.prepareStatement(tableCreation);

                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


        String sql = "SELECT * FROM StatefullSQL WHERE groupName = ? AND type = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, name);
            stmt.setObject(2, type);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                String insertSql = "INSERT INTO StatefullSQL (type, groupName) VALUES (?, ?) RETURNING id";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setString(1, type);
                insertStmt.setString(2, name);
                ResultSet rs2 = insertStmt.executeQuery();

                rs2.next();
                UUID id = (UUID) rs2.getObject(1);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private void checkListAttributeTable(Class<?> parent, Field field, List<Class<?>> blackList){

        if (!(field.getGenericType() instanceof ParameterizedType parameterizedType)) {
            return;
        }

        Type[] typeArgs = parameterizedType.getActualTypeArguments();

        Type elementType = typeArgs[0];
        if (!(elementType instanceof Class<?> clazz2)) {
            return;
        }

        if (!blackList.contains(clazz2) && clazz2.isAnnotationPresent(ClassFlush.class)){
            checkTable(clazz2, blackList);
        }

        if(!clazz2.isAnnotationPresent(ClassFlush.class)){
            /*
            * This case, we will serialize entire list
            * */
            return;
        }

        String tableCreation = String.format("""
                        CREATE TABLE %s (
                        variable VARCHAR NOT NULL,
                        autoFlushId1 UUID NOT NULL,
                        autoFlushId2 UUID NOT NULL,
                        index INT NOT NULL,
                        
                        PRIMARY KEY (variable, autoFlushId1, index) DEFERRABLE INITIALLY DEFERRED
                        );
                        """, getTableName(parent)+"_list_"+getTableName(clazz2));
        //TODO add foreign keys, but only after main table creation, so alter TABLE, with recursion, when main loop is done only (to later)

        try {
            PreparedStatement stmt = connection.prepareStatement(tableCreation);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void checkTable(Class<?> clazz, List<Class<?>> blackList){
        blackList.add(clazz);

        if (tableExists(getTableName(clazz))){
            return;
        }

        List<String> columns = new ArrayList<>();
        List<String> foreignKeys = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields(); // all fields declared in the class
        for (Field field : fields) {
            Class<?> type = field.getType();
            String columnName = field.getName();
            String sqlType = mapJavaTypeToSQL(type);

            //Convert list to additional relation entity
            if (type == List.class){
                checkListAttributeTable(clazz, field, blackList);
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
            }else{
                if (hasSerializer(type)){
                    columns.add(columnName+ " VARCHAR");
                }
            }
        }
        columns.addAll(foreignKeys);

        String columnDefs = String.join(",\n    ", columns);
        String tableName = getTableName(clazz);

        String comma = (columnDefs.length() > 0) ? ",":"";
        String tableCreation = String.format("""
                CREATE TABLE %s (
                autoFlushId UUID PRIMARY KEY DEFAULT gen_random_uuid()"""+comma+"""
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

    public <T> UUID store(String groupName, String type, UuidFunction storeTableFunc, T entity) {
        return store(groupName, type, entity, storeTableFunc, true);
    }

    public <T> UUID store(String groupName, String type, T entity, UuidFunction storeTableFunc, boolean storeInTable) {
        if (storeInTable){
            checkSetupTable(groupName, type);
        }

        try {
            Class<T> clazz = (Class<T>)  entity.getClass();

            DatabaseMetaData metaData = connection.getMetaData();

            List<String> columns = new ArrayList<>();
            ResultSet rs = metaData.getColumns(null, "public", getTableName(clazz), null);

            boolean tableExists = rs.next();
            if (!tableExists){
                checkTable(clazz, new ArrayList<>());
                rs = metaData.getColumns(null, "public", getTableName(clazz), null);
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

            List<ListInsertEntry> listInsertEntries = new ArrayList<>();

            List<Object> values = new ArrayList<>();

            for (Field field: clazz.getDeclaredFields()){
                field.setAccessible(true);

                Class<?> type2 = field.getType();

                Object data = field.get(entity);

                if (type2.isAnnotationPresent(ClassFlush.class) && data != null){
                    UUID uuid = store(groupName, type, data, storeTableFunc, false);
                    values.add(uuid);
                    continue;
                }

                if (mapJavaTypeToSQL(type2) != null) {
                    values.add(data);
                }else if (type2 == List.class){

                    if (!(field.getGenericType() instanceof ParameterizedType parameterizedType)) {
                        continue;
                    }
                    Type[] typeArgs = parameterizedType.getActualTypeArguments();
                    Type elementType = typeArgs[0];
                    if (!(elementType instanceof Class<?> clazz2)) {
                        continue;
                    }
                    if(!clazz2.isAnnotationPresent(ClassFlush.class)){
                        continue;
                    }
                    String listTableName = getTableName(clazz)+"_list_"+getTableName(clazz2);
                    String variable = field.getName();

                    int index = 0;
                    for (Object targetObj: (List<Object>) field.get(entity)){
                        UUID autoFlushId2 = store(groupName, type, targetObj, storeTableFunc, false);

                        listInsertEntries.add(new ListInsertEntry(variable, autoFlushId2, index, listTableName));
                        index += 1;
                    }


                }else{
                    values.add(serialize(type2, data));
                }


            }

            String colNames = String.join(", ", columns);
            String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));

            String insertSql = "INSERT INTO "+getTableName(clazz)+" (" + colNames + ") VALUES (" + placeholders + ") RETURNING autoFlushId";
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);

            for (int i = 0; i < values.size(); i++) {
                insertStmt.setObject(i + 1, values.get(i)); // JDBC is 1-indexed
            }

            ResultSet result = insertStmt.executeQuery();
            boolean suc6 = result.next();
            assert suc6;

            UUID autoFlushId = (UUID) result.getObject(1);

            if (storeInTable){
                storeTableFunc.apply(autoFlushId);
            }

            /*
            * make the internal list connections
            * */
            for (ListInsertEntry lie: listInsertEntries){
                String listTableName = lie.listTableName();
                String insertListSql = "INSERT INTO "+listTableName+" (variable, autoFlushId1, autoFlushId2, index) VALUES (?, ?, ?, ?)";

                insertStmt = DatabaseSingleton.getInstance().getConnection().prepareStatement(insertListSql);
                insertStmt.setString(1, lie.variable());
                insertStmt.setObject(2, autoFlushId);
                insertStmt.setObject(3, lie.autoFlushId2());
                insertStmt.setObject(4, lie.index());
                insertStmt.executeUpdate();
            }

            return autoFlushId;


        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public <T> void checkDelete(UUID key, Class<?> clazz){
        try {
            //TODO add support mapDict
            String sql = "SELECT COUNT(*) FROM StatefullSQL"+"OrderedList"+" WHERE autoflushid = ?";


            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, key);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count != 0){
                return;
            }

            String tableName = getTableName(clazz);

            sql = "DELETE FROM "+tableName+" WHERE autoflushid = ?";

            ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, key);
            ps.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public <T> void update(UUID key, T entity) {
        try {
            Class<T> clazz = (Class<T>) entity.getClass().getSuperclass();

            DatabaseMetaData metaData = connection.getMetaData();

            List<String> columns = new ArrayList<>();
            ResultSet rs = metaData.getColumns(null, "public", getTableName(clazz), null);

            boolean tableExists = rs.next();
            if (!tableExists){
                checkTable(clazz, new ArrayList<>());
                rs = metaData.getColumns(null, "public", getTableName(clazz), null);
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
                    "UPDATE " + getTableName(clazz) +
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
                if (mapJavaTypeToSQL(values.get(i).getClass()) != null){
                    insertStmt.setObject(i + 1, values.get(i)); // JDBC is 1-indexed
                }else{
                    for (SQLSerializer s: serializers){
                        if(values.get(i).getClass().equals(s.getType())){
                            insertStmt.setString(i + 1, s.serialize(values.get(i)));
                            break;
                        }
                    }

                }
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

    public <T> T createRow(String groupName, String type, UuidFunction storeTableFunc, T entity){
        /*
         * flush to disk here
         * */

        Class<T> clazz = (Class<T>) entity.getClass();

        assert clazz.isAnnotationPresent(ClassFlush.class);

        try {

            /*
             * Store class in SQL
             * */
            UUID id = DatabaseSingleton.getInstance().store(groupName, type, storeTableFunc, entity);

            /*
             * override class so it contains a UUID
             * */
            Class<? extends T> dynamicType = new ByteBuddy()
                    .subclass(clazz)
                    .implement(ByteBuddyEnhanced.class)
                    .defineField("autoFlushId", UUID.class, Modifier.PUBLIC)
                    .method(
                            not(named("clone"))
                    )
                    .intercept(MethodDelegation.to(new InterceptorWrap<T>()))
                    .make()
                    .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            T obj = dynamicType.getDeclaredConstructor().newInstance();
            dynamicType.getField("autoFlushId").set(obj, id);
            return obj;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
