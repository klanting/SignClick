package com.klanting.signclick.utils.statefullSQL.access;


import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;


public class OrderedList<T> implements AccessPoint<T>, List<T> {

    private final Class<T> type;
    private final String groupName;

    public OrderedList(String name, Class<T> type) {
        this.type = type;
        this.groupName = name;
        DatabaseSingleton.getInstance().checkSetupTable(name, "OrderedList");
    }

    @Override
    public T createRow(T entity) {
       return DatabaseSingleton.getInstance().getModifiedObject(groupName, "OrderedList", entity);
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
        return new AccessIterator<T>(groupName, type);
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

        return false;
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
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

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
                return instance;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public T set(int index, T element) {
        return null;
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
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return null;
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return null;
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }
}
