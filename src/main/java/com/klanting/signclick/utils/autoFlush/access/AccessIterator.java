package com.klanting.signclick.utils.autoFlush.access;


import com.klanting.signclick.utils.autoFlush.DatabaseSingleton;

import java.util.Iterator;
import java.util.List;

public class AccessIterator<T> implements Iterator<T> {

    private final Class<T> type;

    private int current = 0;

    public AccessIterator(Class<T> type){
        this.type = type;
    }

    @Override
    public boolean hasNext() {
        List<T> entities = DatabaseSingleton.getInstance().getAll(type);
        return current < entities.size();
    }

    @Override
    public T next() {
        List<T> entities = DatabaseSingleton.getInstance().getAll(type);
        T entity = entities.get(current);
        current++;
        return entity;
    }
}