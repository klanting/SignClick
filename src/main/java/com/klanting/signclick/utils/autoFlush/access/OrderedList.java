package com.klanting.signclick.utils.autoFlush.access;


import com.klanting.signclick.utils.autoFlush.DatabaseSingleton;
import io.ebeaninternal.server.util.Str;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
       return DatabaseSingleton.getInstance().getModifiedObject(entity);
    }

    @Override
    public int size() {
        Connection connection =  DatabaseSingleton.getInstance().getConnection();

        String tableName = type.getSimpleName().toLowerCase();

        String countSql = "SELECT COUNT(*) FROM " + tableName;

        try (PreparedStatement stmt = connection.prepareStatement(countSql);
             ResultSet rs = stmt.executeQuery()) {

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
        List<T> entities = DatabaseSingleton.getInstance().getAll(type);
        return entities.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new AccessIterator<T>(type);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        List<T> entities = DatabaseSingleton.getInstance().getAll(type);
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

        List<T> entities = DatabaseSingleton.getInstance().getAll(type);

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
        List<T> entities = DatabaseSingleton.getInstance().getAll(type);

        /*
        * database
        * */

        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return false;
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
        return null;
    }

    @Override
    public T set(int index, T element) {
        return null;
    }

    @Override
    public void add(int index, T element) {

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
