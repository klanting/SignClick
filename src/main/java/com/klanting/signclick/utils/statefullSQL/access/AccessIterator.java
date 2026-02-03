package com.klanting.signclick.utils.statefullSQL.access;


import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;

import java.util.Iterator;
import java.util.List;

public class AccessIterator<T> implements Iterator<T> {

    private final Class<T> clazz;
    private final String groupName;

    private int current = 0;

    public AccessIterator(String groupName, Class<T> clazz){
        this.groupName = groupName;
        this.clazz = clazz;
    }

    @Override
    public boolean hasNext() {
        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", clazz);
        return current < entities.size();
    }

    @Override
    public T next() {
        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", clazz);
        T entity = entities.get(current);
        current++;
        return entity;
    }
}