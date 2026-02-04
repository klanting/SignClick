package com.klanting.signclick.utils.statefullSQL.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapWrapper<S, T> implements Map<S, T> {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
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
