package com.klanting.signclick.utils.statefullSQL.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapWrapper implements Map {
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
    public Object get(Object key) {
        return null;
    }

    @Nullable
    @Override
    public Object put(Object key, Object value) {
        return null;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(@NotNull Map m) {

    }

    @Override
    public void clear() {

    }

    @NotNull
    @Override
    public Set keySet() {
        return null;
    }

    @NotNull
    @Override
    public Collection values() {
        return null;
    }

    @NotNull
    @Override
    public Set<Entry> entrySet() {
        return null;
    }
}
