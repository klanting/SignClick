package com.klanting.signclick.utils.statefullSQL.internal;

import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.UUID;

public class InternalIterator<T> implements Iterator<T> {

    private final Class<?> clazz;
    private final String listTableName;

    private final UUID parentAutoFlushId;

    private int current = 0;

    public InternalIterator(String listTableName, Class<?> clazz, UUID parentAutoFlushId){
        this.listTableName = listTableName;
        this.clazz = clazz;
        this.parentAutoFlushId = parentAutoFlushId;
    }

    private int size(){
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
    public boolean hasNext() {
        return current < size();
    }

    @Override
    public T next() {
        String sql = "SELECT autoFlushId2 FROM "+listTableName+" WHERE autoFlushId1 = ? AND index = ?";

        try {
            PreparedStatement ps = DatabaseSingleton.getInstance().getConnection().prepareStatement(sql);
            ps.setObject(1, parentAutoFlushId);
            ps.setInt(2, current);

            ResultSet rs3 = ps.executeQuery();

            if (rs3.next()){
                UUID autoFlushId2 = (UUID) rs3.getObject("autoFlushId2");
                current += 1;
                return DatabaseSingleton.getInstance().getObjectByKey(autoFlushId2, clazz);
            }

            return null;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }
}
