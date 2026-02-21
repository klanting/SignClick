package com.klanting.signclick.utils.statefulSQL.internal;

import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ListWrapper<T> implements List<T> {

    private final String listTableName;
    private final UUID parentAutoFlushId;
    private final String variable;

    private final Class<?> clazz;

    public ListWrapper(String listTableName, UUID parentAutoFlushId, Class<?> clazz, String variable){
        this.listTableName = listTableName;
        this.parentAutoFlushId = parentAutoFlushId;
        this.clazz = clazz;
        this.variable = variable;
    }

    @Override
    public int size() {

        String sql = "SELECT COUNT(*) FROM "+listTableName+" WHERE autoFlushId1 = ? ";
        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);

            ResultSet rs3 = ps.executeQuery();

            int count = 0;
            if (rs3.next()) {
                count = rs3.getInt(1);
            }

            return count;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {

        String sql = "SELECT autoFlushId2 FROM "+listTableName+" WHERE autoFlushId1 = ?";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);

            ResultSet rs3 = ps.executeQuery();

            while (rs3.next()){
                UUID autoFlushId2 = (UUID) rs3.getObject("autoFlushId2");
                T obj = DatabaseSingleton.getInstance().getObjectByKey(autoFlushId2, clazz);
                if(obj.equals(o)){
                    return true;
                }
            }

            return false;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public Iterator iterator() {
        return new InternalIterator<T>(listTableName, clazz, parentAutoFlushId);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size()];
        int i = 0;

        for(int j=0; j<size(); j++){
            arr[i++] = get(j);
        }

        return arr;
    }

    @Override
    public boolean add(Object o) {
        UUID newUUID = DatabaseSingleton.getInstance().store(o, (u) -> {}, false);

        String insertListSql = "INSERT INTO "+listTableName+" (variable, autoFlushId1, autoFlushId2, index) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement insertStmt = DatabaseSingleton.getInstance().getConnection().prepareStatement(insertListSql);
            insertStmt.setString(1, variable);
            insertStmt.setObject(2, parentAutoFlushId);
            insertStmt.setObject(3, newUUID);
            insertStmt.setObject(4, size());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index != -1){
            return false;
        }

        remove(index);
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
    public boolean addAll(int index, @NotNull Collection c) {
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
    public void clear() {
        String sql = "DELETE FROM "+listTableName+" WHERE variable = ? AND autoflushid1 = ? RETURNING autoflushid2";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setString(1, variable);
            ps.setObject(2, parentAutoFlushId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                UUID autoFlushId2 = (UUID) rs.getObject("autoflushid2");
                DatabaseSingleton.getInstance().checkDelete(autoFlushId2, clazz);
            }


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get(int index) {
        String sql = "SELECT autoFlushId2 FROM "+listTableName+" WHERE autoFlushId1 = ? AND index = ?";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);
            ps.setInt(2, index);

            ResultSet rs3 = ps.executeQuery();

            if (rs3.next()){
                UUID autoFlushId2 = (UUID) rs3.getObject("autoFlushId2");
                Class<?> realClass = DatabaseSingleton.getInstance().getRealClass(autoFlushId2);
                return DatabaseSingleton.getInstance().getObjectByKey(autoFlushId2, realClass, true);
            }

            return null;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object set(int index, Object element) {
        remove(index);
        add(index, element);
        return get(index);
    }

    @Override
    public void add(int index, Object element) {

        /*
        * update index with higher values
        * */
        try {
            /*
             * update index with higher values
             * */
            String updateSql =
                    "UPDATE "+listTableName+" " +
                            "SET index = index + 1 " +
                            "WHERE autoflushid1 = ? AND variable = ? AND index > ?";

            PreparedStatement updatePs = DatabaseSingleton.getInstance().getConnection().prepareStatement(updateSql);

            updatePs.setObject(1, parentAutoFlushId);
            updatePs.setString(2, variable);
            updatePs.setInt(3, index);

            updatePs.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*
        * add new item
        * */
        UUID newUUID = DatabaseSingleton.getInstance().store(element, (u) -> {}, false);

        String insertListSql = "INSERT INTO "+listTableName+" (variable, autoFlushId1, autoFlushId2, index) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement insertStmt = DatabaseSingleton.getInstance().getConnection().prepareStatement(insertListSql);
            insertStmt.setString(1, variable);
            insertStmt.setObject(2, parentAutoFlushId);
            insertStmt.setObject(3, newUUID);
            insertStmt.setObject(4, index);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T remove(int index) {
        T entity = get(index);

        String sql = "DELETE FROM "+listTableName+" WHERE index = ? AND variable = ? AND autoflushid1 = ? RETURNING autoflushid2";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setInt(1, index);
            ps.setString(2, variable);
            ps.setObject(3, parentAutoFlushId);

            ResultSet rs = ps.executeQuery();

            rs.next();
            UUID autoFlushId2 = (UUID) rs.getObject("autoflushid2");
            DatabaseSingleton.getInstance().checkDelete(autoFlushId2, clazz);

            /*
             * update index with higher values
             * */
            String updateSql =
                    "UPDATE "+listTableName+" " +
                            "SET index = index - 1 " +
                            "WHERE autoflushid1 = ? AND variable = ? AND index > ?";

            PreparedStatement updatePs = DatabaseSingleton.getInstance().getConnection().prepareStatement(updateSql);

            updatePs.setObject(1, parentAutoFlushId);
            updatePs.setString(2, variable);
            updatePs.setInt(3, index);

            updatePs.executeUpdate();

            //TODO later do checkDelete toa void dangling ptrs, but then we need SQLLookupTable to keep ref count

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return entity;
    }

    @Override
    public int indexOf(Object o) {

        String sql = "SELECT autoFlushId2 FROM "+listTableName+" WHERE autoFlushId1 = ? ORDER BY INDEX ASC";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);

            ResultSet rs3 = ps.executeQuery();

            int index = 0;
            while (rs3.next()){
                UUID autoFlushId2 = (UUID) rs3.getObject("autoFlushId2");
                T obj = DatabaseSingleton.getInstance().getObjectByKey(autoFlushId2, clazz);
                if(obj.equals(o)){
                    return index;
                }
                index += 1;
            }

            return -1;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        String sql = "SELECT autoFlushId2 FROM "+listTableName+" WHERE autoFlushId1 = ? ORDER BY INDEX ASC";

        int lastIndex = -1;
        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);

            ResultSet rs3 = ps.executeQuery();

            int index = 0;
            while (rs3.next()){
                UUID autoFlushId2 = (UUID) rs3.getObject("autoFlushId2");
                T obj = DatabaseSingleton.getInstance().getObjectByKey(autoFlushId2, clazz);
                if(obj.equals(o)){
                    lastIndex = index;
                }
                index += 1;
            }

            return lastIndex;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public ListIterator listIterator() {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @NotNull
    @Override
    public ListIterator listIterator(int index) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @NotNull
    @Override
    public List subList(int fromIndex, int toIndex) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public boolean retainAll(@NotNull Collection c) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public boolean removeAll(@NotNull Collection c) {
        for (Object item : c) {
            this.remove(item);
        }
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

    @NotNull
    @Override
    public Object[] toArray(@NotNull Object[] a) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }
}
