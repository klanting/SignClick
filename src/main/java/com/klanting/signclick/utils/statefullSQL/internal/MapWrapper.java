package com.klanting.signclick.utils.statefullSQL.internal;

import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MapWrapper<S, T> implements Map<S, T> {

    private final String mapTableName;
    private final UUID parentAutoFlushId;
    private final String variable;

    private final Class<?> clazz;


    public MapWrapper(String mapTableName, UUID parentAutoFlushId, Class<?> clazz, String variable){
        this.mapTableName = mapTableName;
        this.parentAutoFlushId = parentAutoFlushId;
        this.clazz = clazz;
        this.variable = variable;
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM "+mapTableName+" WHERE autoFlushId1 = ? ";
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
    public boolean containsKey(Object key) {
        System.out.println("A");
        String sql = "SELECT autoFlushId2 FROM "+mapTableName+" WHERE autoFlushId1 = ? AND key = ?";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);
            ps.setObject(2, key.toString());

            ResultSet rs3 = ps.executeQuery();

            return rs3.next();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        String sql = "SELECT autoFlushId2 FROM "+mapTableName+" WHERE autoFlushId1 = ?";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);

            ResultSet rs3 = ps.executeQuery();

            while (rs3.next()){
                UUID autoFlushId2 = (UUID) rs3.getObject("autoFlushId2");
                T obj = DatabaseSingleton.getInstance().getObjectByKey(autoFlushId2, clazz);
                if(obj.equals(value)){
                    return true;
                }
            }

            return false;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private UUID getAutoFlushIdByKey(Object key){
        String sql = "SELECT * FROM "+mapTableName+" WHERE autoFlushId1 = ? AND key = ?";

        try {
            PreparedStatement  ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);

            ps.setObject(1, parentAutoFlushId);
            ps.setObject(2, key.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                return null;
            }
            return UUID.fromString(rs.getString("autoflushid2"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get(Object key) {
        System.out.println("GET");
        UUID keyId = getAutoFlushIdByKey(key);
        if (keyId == null){
            return null;
        }

        System.out.println("KEYID "+keyId);
        return DatabaseSingleton.getInstance().getObjectByKey(keyId, clazz, true);
    }

    private void deleteByAutoFlushId(UUID autoFlushId){
        String sql = "DELETE FROM "+mapTableName+" WHERE autoflushid1 = ? AND autoflushid2 = ?";
        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);
            ps.setObject(2, autoFlushId);
            ps.executeUpdate();
            DatabaseSingleton.getInstance().checkDelete(autoFlushId, clazz);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public T put(S key, T value) {
        UUID keyId = getAutoFlushIdByKey(key);
        if (keyId != null){
            deleteByAutoFlushId(keyId);
        }

        UUID newUUID = DatabaseSingleton.getInstance().store("", "", value, (u) -> {}, false);

        String insertListSql = "INSERT INTO "+mapTableName+" (variable, autoFlushId1, autoFlushId2, key) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement insertStmt = DatabaseSingleton.getInstance().getConnection().prepareStatement(insertListSql);
            insertStmt.setString(1, variable);
            insertStmt.setObject(2, parentAutoFlushId);
            insertStmt.setObject(3, newUUID);
            insertStmt.setObject(4, key.toString());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return get(key);
    }

    @Override
    public T remove(Object key) {
        UUID keyId = getAutoFlushIdByKey(key);
        if (keyId == null){
            return null;
        }
        T entity = get(key);

        deleteByAutoFlushId(keyId);

        return entity;
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
