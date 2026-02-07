package com.klanting.signclick.utils.statefulSQL;


public abstract class SQLSerializer<T> {

    public Class<T> getType() {
        return type;
    }

    private final Class<T> type;

    public SQLSerializer(Class<T> type){
        this.type = type;
    }

    public abstract String serialize(T value);
    public abstract T deserialize(String value);
}
