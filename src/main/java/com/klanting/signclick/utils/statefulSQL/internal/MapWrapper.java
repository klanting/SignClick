package com.klanting.signclick.utils.statefulSQL.internal;

import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MapWrapper<S, T> implements Map<S, T> {

    private final String mapTableName;
    private final UUID parentAutoFlushId;
    private final String variable;

    private final Class<?> clazz;
    private final Class<?> keyClazz;


    public MapWrapper(String mapTableName, UUID parentAutoFlushId, Class<?> keyClazz, Class<?> clazz, String variable){
        this.mapTableName = mapTableName;
        this.parentAutoFlushId = parentAutoFlushId;

        this.keyClazz = keyClazz;
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
        UUID keyId = getAutoFlushIdByKey(key);
        if (keyId == null){
            return null;
        }

        Class<?> realClass = DatabaseSingleton.getInstance().getRealClass(keyId);

        return DatabaseSingleton.getInstance().getObjectByKey(keyId, realClass, true);
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
        for (Entry<? extends S, ? extends T> elements: m.entrySet()){
            put(elements.getKey(), elements.getValue());
        }
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM "+mapTableName+" WHERE autoFlushId1 = ? RETURNING autoFlushId2";
        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                UUID autoFlushId = (UUID) rs.getObject("autoflushid");
                DatabaseSingleton.getInstance().checkDelete(autoFlushId, clazz);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @NotNull
    @Override
    public Set<S> keySet() {
        Set<S> keys = new HashSet<>();

        String sql = "SELECT key FROM "+mapTableName+" WHERE autofushid1 = ?";
        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String key = rs.getString("key");
                if (keyClazz == String.class){
                    keys.add((S) key);
                }else{
                    keys.add(DatabaseSingleton.getInstance().deserialize(keyClazz, key));
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return keys;
    }

    @NotNull
    @Override
    public Collection<T> values() {
        List<T> values = new ArrayList<>();
        for (S key: keySet()){
            values.add(get(key));
        }

        return values;
    }

    @NotNull
    @Override
    public Set<Entry<S, T>> entrySet() {
        Set<S> keys = keySet();

        Set<Entry<S, T>> entries = new HashSet<>();
        for (S key: keys){
            T value = get(key);
            entries.add(new AbstractMap.SimpleEntry<>(key, value));
        }

        return entries;
    }
}
