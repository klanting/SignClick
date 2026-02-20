package com.klanting.signclick.utils.statefulSQL.access.needed;

import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.OrderedList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class OrderedSubList<T> implements List<T> {
    //TODO class not finished yet

    private final int fromIndex;
    private int toIndex;

    private final Class<T> type;

    protected String getGroupName() {
        return groupName;
    }

    private final String groupName;


    private final OrderedList<T> ref;

    public OrderedSubList(String name, Class<T> type, int fromIndex, int toIndex, OrderedList<T> ref) {
        this.groupName = name;
        this.type = type;
        this.ref = ref;

        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public int size() {
        return toIndex-fromIndex;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean add(T t) {
        ref.add(toIndex, t);
        toIndex += 1;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        List<T> entities = DatabaseSingleton.getInstance().getAll(getGroupName(), "OrderedList", type);
        return entities.subList(fromIndex, toIndex).contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public T get(int index) {
        return ref.get(index+fromIndex);
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
        return ref.set(index+fromIndex, element);
    }

    @Override
    public void add(int index, T element) {
        ref.add(index+fromIndex, element);
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
