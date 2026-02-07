package com.klanting.signclick.utils.statefullSQL.access;


import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;


public class OrderedList<T> implements AccessPoint<T>, List<T> {

    private final Class<T> type;
    private final String groupName;

    public void decrModCount(){
        modCount -= 1;
    }

    public int getModCount() {
        return modCount;
    }

    private int modCount = 0;

    public OrderedList(String name, Class<T> type) {
        this.type = type;
        this.groupName = name;
        DatabaseSingleton.getInstance().checkSetupTable(name, "OrderedList");
        DatabaseSingleton.getInstance().checkTable(type, new ArrayList<>());
        DatabaseSingleton.getInstance().registerAccessedClass("OrderedList_"+groupName, type);
    }

    public void storeInTable(UUID autoFlushId) throws SQLException{

        UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "OrderedList");

        //get next index
        String sql = """
                SELECT COUNT(*)
                FROM StatefullSQL"""+"OrderedList"+"""
                WHERE id = ?
            """;

        PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
        ps.setObject(1, id);

        ResultSet rs3 = ps.executeQuery();

        int count = 0;
        if (rs3.next()) {
            count = rs3.getInt(1);
        }

        String colNames = "id, index, autoflushid";
        String placeholders = "?, ?, ?";
        String insertSql = "INSERT INTO StatefullSQL"+"OrderedList"+" (" + colNames + ") VALUES (" + placeholders + ")";
        PreparedStatement insertStmt = DatabaseSingleton.getInstance().getConnection().prepareStatement(insertSql);
        insertStmt.setObject(1, id);
        insertStmt.setInt(2, count);
        insertStmt.setObject(3, autoFlushId);
        insertStmt.executeUpdate();
    }


    public T createRow(T entity) {
       return DatabaseSingleton.getInstance().createRow(groupName, "OrderedList", (u) -> storeInTable(u), entity);
    }

    @Override
    public int size() {
        Connection connection =  DatabaseSingleton.getInstance().getConnection();

        String tableName = type.getSimpleName().toLowerCase();

        String countSql = "SELECT COUNT(*) FROM " + tableName+ " t JOIN statefullSQL" + "OrderedList" +" o ON t.autoflushid = o.autoflushid JOIN StatefullSQL s ON o.id = s.id WHERE s.groupname = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(countSql);
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int rowCount = rs.getInt(1);
                return rowCount;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", type);
        return entities.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new AccessIterator<T>(groupName, type, this);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", type);
        Object[] arr = new Object[entities.size()];
        int i = 0;
        for (Object e : entities) {
            arr[i++] = e;
        }
        return arr;
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {

        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", type);

        int size = size(); // collection size

        if (a.length < size) {
            // create new array of same runtime type
            @SuppressWarnings("unchecked")
            T1[] newArray = (T1[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);

            a = newArray;
        }

        int i = 0;
        for (Object element : entities) {
            a[i++] = (T1) element;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean add(T t) {
        createRow(t);
        modCount++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", type);

        T matchingEntity = null;

        for (T entity: entities){
            if (entity.equals(o)){
                matchingEntity = entity;
                break;
            }
        }

        if (matchingEntity == null){
            return false;
        }

        UUID uuid;
        try {
            uuid = (UUID) matchingEntity.getClass()
                    .getDeclaredField("autoFlushId")
                    .get(matchingEntity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        /*
        * database
        * */
        UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "OrderedList");

        String sql = "DELETE FROM StatefullSQL"+"OrderedList"+" WHERE autoflushid = ? AND id = ? RETURNING index";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, uuid);
            ps.setObject(2, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int index = rs.getInt("index");

            /*
            * update the index
            * */
            String updateSql =
                    "UPDATE StatefullSQLOrderedList " +
                            "SET index = index - 1 " +
                            "WHERE id = ? AND index > ?";

            PreparedStatement updatePs = DatabaseSingleton.getInstance().getConnection().prepareStatement(updateSql);
            updatePs.setObject(1, id);
            updatePs.setInt(2, index);

            updatePs.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }

        DatabaseSingleton.getInstance().checkDelete(uuid, type);
        modCount++;

        return true;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object element : c) {
            if (!this.contains(element)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        for (T element : c) {
            this.add(element);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        List<T> list = new ArrayList<>(c);
        /*
        * reverse list to match normal list behaviour
        * */
        Collections.reverse(list);

        for (T item : list) {
            add(index, item);
        }
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        for (Object item : c) {
            this.remove(item);
        }
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", type);

        for (T entity: entities){
            boolean found = false;
            for (Object item: c){
                if (entity.equals(item)){
                    found = true;
                    break;
                }
            }

            if (!found){
                this.remove(entity);
            }
        }

        return false;
    }

    @Override
    public void clear() {
        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", type);
        for (T entity: entities){
            this.remove(entity);
        }
    }

    @Override
    public T get(int index) {
        String tableName = type.getSimpleName().toLowerCase();
        String sql = "SELECT t.* FROM " + tableName+ " t JOIN statefullSQL" + "OrderedList" +" o ON t.autoflushid = o.autoflushid JOIN StatefullSQL s ON o.id = s.id WHERE s.groupname = ? AND o.index = ?";

        try {
            PreparedStatement stmt = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            stmt.setString(1, groupName);
            stmt.setInt(2, index);

            ResultSet rs = stmt.executeQuery();

            /*
             * Go over each loaded row
             * */
            if (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = meta.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }

                T instance = DatabaseSingleton.getInstance().wrap(type, row, true);
                return instance;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public T set(int index, T element) {

        T entity = createRow(element);

        try {
            UUID uuid = (UUID) entity.getClass().getDeclaredField("autoFlushId").get(entity);

            UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "OrderedList");

            T toOverride = this.get(index);
            this.remove(toOverride);

            String updateSql =
                    "UPDATE StatefullSQLOrderedList " +
                            "SET index = "+index+" " +
                            "WHERE id = ? AND autoFlushId = ?";

            PreparedStatement updatePs = DatabaseSingleton.getInstance().getConnection().prepareStatement(updateSql);
            updatePs.setObject(1, id);
            updatePs.setObject(2, uuid);

            updatePs.executeUpdate();

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public void add(int index, T element) {
        T entity = createRow(element);
        try {
            UUID uuid = (UUID) entity.getClass().getDeclaredField("autoFlushId").get(entity);

            UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "OrderedList");

            String updateSql =
                    "UPDATE StatefullSQLOrderedList " +
                    "SET index = index + 1 " +
                    "WHERE id = ? AND index >= ? ";

            PreparedStatement updatePs = DatabaseSingleton.getInstance().getConnection().prepareStatement(updateSql);
            updatePs.setObject(1, id);
            updatePs.setInt(2, index);
            updatePs.executeUpdate();

            updateSql =
                    "UPDATE StatefullSQLOrderedList " +
                            "SET index = "+index+" " +
                            "WHERE id = ? AND autoFlushId = ?";

            updatePs = DatabaseSingleton.getInstance().getConnection().prepareStatement(updateSql);
            updatePs.setObject(1, id);
            updatePs.setObject(2, uuid);

            updatePs.executeUpdate();

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public T remove(int index) {
        T entity = this.get(index);
        this.remove(entity);
        return entity;
    }

    private List<T> getSortedList(){
        String tableName = type.getSimpleName().toLowerCase();
        String sql = "SELECT t.* FROM " + tableName+ " t JOIN statefullSQL" + "OrderedList" +" o ON t.autoflushid = o.autoflushid JOIN StatefullSQL s ON o.id = s.id WHERE s.groupname = ? ORDER BY o.index ASC";

        List<T> sortedEntities = new ArrayList<>();
        try {
            PreparedStatement stmt = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
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

                T instance = DatabaseSingleton.getInstance().wrap(type, row);
                sortedEntities.add(instance);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sortedEntities;
    }

    @Override
    public int indexOf(Object o) {
        List<T> sortedEntities = getSortedList();
        return sortedEntities.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        List<T> sortedEntities = getSortedList();
        return sortedEntities.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        //TODO probably points to dirty list
        List<T> sortedEntities = getSortedList();
        return sortedEntities.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        //TODO probably points to dirty list
        List<T> sortedEntities = getSortedList();
        return sortedEntities.listIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        List<T> sortedEntities = getSortedList();
        return sortedEntities.subList(fromIndex, toIndex);
    }
}
