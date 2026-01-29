package com.klanting.signclick.utils.statefullSQL.access;

import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MapDict<S, T> implements AccessPoint<T>, Map<S, T> {

    private final Class<T> type;
    private final String groupName;
    public MapDict(String name, Class<T> type){
        this.type = type;
        this.groupName = name;
        DatabaseSingleton.getInstance().checkSetupTable(name, "MapDict");
        DatabaseSingleton.getInstance().checkTable(type, new ArrayList<>());
    }

    public T createRow(S key, T entity) {
        return DatabaseSingleton.getInstance().createRow(groupName, "MapDict", (u) -> storeInTable(key, u), entity);
    }

    public void storeInTable(S key, UUID autoFlushId) throws SQLException{
        UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "MapDict");

        //get next index
        String sql = """
                SELECT autoFlushId
                FROM StatefullSQL"""+"MapDict"+"""
                WHERE id = ? AND key = ?
            """;

        PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
        ps.setObject(1, id);
        ps.setObject(2, key.toString());

        ResultSet rs3 = ps.executeQuery();


        if (rs3.next()) {
            UUID oldAutoFlushId = (UUID) rs3.getObject(1);

            sql = "DELETE FROM StatefullSQL"+"MapDict"+" WHERE autoflushid = ? AND id = ?";

            try {
                ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
                ps.setObject(1, oldAutoFlushId);
                ps.setObject(2, id);
                ps.executeUpdate();

            }catch (Exception e){
                e.printStackTrace();
            }

            DatabaseSingleton.getInstance().checkDelete(oldAutoFlushId, type);
        }


        String colNames = "id, key, autoflushid";
        String placeholders = "?, ?, ?";
        String insertSql = "INSERT INTO StatefullSQL"+"MapDict"+" (" + colNames + ") VALUES (" + placeholders + ")";
        PreparedStatement insertStmt = DatabaseSingleton.getInstance().getConnection().prepareStatement(insertSql);
        insertStmt.setObject(1, id);
        insertStmt.setObject(2, key.toString());
        insertStmt.setObject(3, autoFlushId);
        insertStmt.executeUpdate();
    }


    @Override
    public int size() {
        return DatabaseSingleton.getInstance().getAll(groupName, "MapDict", type).size();
    }

    @Override
    public boolean isEmpty() {
        return size() != 0;
    }

    @Override
    public boolean containsKey(Object key) {

        UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "MapDict");
        String sql = "SELECT * FROM StatefullSQL"+"MapDict"+" WHERE id = ? AND key = ?";

        try {
            PreparedStatement  ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);

            ps.setObject(1, id);
            ps.setObject(2, key.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public T get(Object key) {
        return null;
    }

    @Nullable
    @Override
    public T put(S key, T value) {
        return null;
    }

    @Override
    public T remove(Object key) {
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends S, ? extends T> m) {

    }

    @Override
    public void clear() {

    }

    @NotNull
    @Override
    public Set<S> keySet() {
        return null;
    }

    @NotNull
    @Override
    public Collection<T> values() {
        return null;
    }

    @NotNull
    @Override
    public Set<Entry<S, T>> entrySet() {
        return null;
    }


}
