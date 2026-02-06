package com.klanting.signclick.utils.statefullSQL.access;

import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MapDict<S, T> implements AccessPoint<T>, Map<S, T> {

    private final Class<S> keyClazz;
    private final Class<T> clazz;
    private final String groupName;
    public MapDict(String name, Class<S> keyClazz, Class<T> clazz){
        this.keyClazz = keyClazz;
        this.clazz = clazz;
        this.groupName = name;
        DatabaseSingleton.getInstance().checkSetupTable(name, "MapDict");
        DatabaseSingleton.getInstance().checkTable(clazz, new ArrayList<>());
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

        String keyVal;
        if (keyClazz == String.class){
            keyVal = key.toString();
        }else{
            keyVal = DatabaseSingleton.getInstance().serialize(keyClazz, key);
        }

        ps.setObject(1, id);
        ps.setObject(2, keyVal);

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

            DatabaseSingleton.getInstance().checkDelete(oldAutoFlushId, clazz);
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
        return DatabaseSingleton.getInstance().getAll(groupName, "MapDict", clazz).size();
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
        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "MapDict", clazz);
        return entities.contains(value);
    }

    private UUID getAutoFlushIdByKey(Object key){
        UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "MapDict");
        String sql = "SELECT * FROM StatefullSQL"+"MapDict"+" WHERE id = ? AND key = ?";

        try {
            PreparedStatement  ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);

            ps.setObject(1, id);
            ps.setObject(2, key.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                return null;
            }
            return UUID.fromString(rs.getString("autoflushid"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteByAutoFlushId(UUID autoFlushId){
        UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "MapDict");
        String sql = "DELETE FROM StatefullSQL"+"MapDict"+" WHERE autoflushid = ? AND id = ?";
        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, autoFlushId);
            ps.setObject(2, id);
            ps.executeUpdate();
            DatabaseSingleton.getInstance().checkDelete(autoFlushId, clazz);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public T get(Object key) {
        UUID keyId = getAutoFlushIdByKey(key);
        if (keyId == null){
            return null;
        }

        return DatabaseSingleton.getInstance().getObjectByKey(keyId, clazz, true);
    }

    @Nullable
    @Override
    public T put(S key, T value) {
        UUID keyId = getAutoFlushIdByKey(key);
        if (keyId != null){
            deleteByAutoFlushId(keyId);
        }

        return createRow(key, value);
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
        UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "MapDict");
        String sql = "DELETE FROM StatefullSQL"+"MapDict"+" WHERE id = ? RETURNING autoFlushId";
        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, id);
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

        UUID id = DatabaseSingleton.getInstance().getIdByGroup(groupName, "MapDict");
        String sql = "SELECT key FROM StatefullSQL"+"MapDict"+" WHERE id = ?";
        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, id);
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
        return DatabaseSingleton.getInstance().getAll(groupName, "MapDict", clazz);
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
