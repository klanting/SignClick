package com.klanting.signclick.utils.statefulSQL.access.needed;

import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.OrderedList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;

public class OrderedSubList<T> extends OrderedList<T> {
    //TODO class not finished yet

    private final int fromIndex;
    private int toIndex;

    public OrderedSubList(String name, Class<T> type, int fromIndex, int toIndex) {
        super(name, type);

        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public int size() {
        return toIndex-fromIndex;
    }

    @Override
    public boolean add(T t) {
        super.add(toIndex, t);
        toIndex += 1;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        List<T> entities = DatabaseSingleton.getInstance().getAll(getGroupName(), "OrderedList", getType());
        return entities.subList(fromIndex, toIndex).contains(o);
    }

    @Override
    public T get(int index) {
        return super.get(index+fromIndex);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void clear() {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public T set(int index, T element) {
        return super.set(index+fromIndex, element);
    }

    @Override
    public void add(int index, T element) {
        super.add(index+fromIndex, element);
    }

    @Override
    public T remove(int index) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public int indexOf(Object o) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }


}
