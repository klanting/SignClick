package com.klanting.signclick.utils.statefulSQL.access;


import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class AccessIterator<T> implements Iterator<T> {

    private final Class<T> clazz;
    private final String groupName;

    private final OrderedList<T> ref;

    private final int expectedModCount;

    private int current = 0;

    private int size;

    private void checkForModification() {

        int actualModCount = ref.getModCount();

        if (actualModCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }


    public AccessIterator(String groupName, Class<T> clazz, OrderedList<T> ref){
        this.groupName = groupName;
        this.clazz = clazz;
        this.ref = ref;
        this.expectedModCount = ref.getModCount();

        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", clazz);

        this.size = entities.size();
    }

    @Override
    public boolean hasNext() {
        return current < size;
    }

    @Override
    public T next() {
        checkForModification();

        List<T> entities = DatabaseSingleton.getInstance().getAll(groupName, "OrderedList", clazz);
        T entity = entities.get(current);
        current++;
        return entity;
    }

    @Override
    public void remove() {
        /*
        * special support for ptr delete
        * */
        ref.remove(current-1);
        ref.decrModCount();

        /*
        * list will auto fix the current, so this is needed as correction for this auto fix
        * */
        current -= 1;
        size -= 1;
    }
}