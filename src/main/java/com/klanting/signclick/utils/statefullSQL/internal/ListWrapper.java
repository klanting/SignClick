package com.klanting.signclick.utils.statefullSQL.internal;

import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ListWrapper<T> implements List<T> {

    private final String listTableName;
    private final String itemTableName;
    private final UUID parentAutoFlushId;
    private final String variable;

    private final Class<?> clazz;

    public ListWrapper(String listTableName, String itemTableName, UUID parentAutoFlushId, Class<?> clazz, String variable){
        this.listTableName = listTableName;
        this.itemTableName = itemTableName;
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
        return size() != 0;
    }

    @Override
    public boolean contains(Object o) {

        String sql = """
                SELECT autoFlushId2
                FROM """+listTableName+"""
                WHERE autoFlushId1 = ?
            """;

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
        return null;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public boolean add(Object o) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection c) {
        return false;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection c) {
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
    public Object set(int index, Object element) {
        return null;
    }

    @Override
    public void add(int index, Object element) {

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
    public ListIterator listIterator() {
        return null;
    }

    @NotNull
    @Override
    public ListIterator listIterator(int index) {
        return null;
    }

    @NotNull
    @Override
    public List subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public boolean retainAll(@NotNull Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection c) {
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection c) {
        return false;
    }

    @NotNull
    @Override
    public Object[] toArray(@NotNull Object[] a) {
        return new Object[0];
    }
}
