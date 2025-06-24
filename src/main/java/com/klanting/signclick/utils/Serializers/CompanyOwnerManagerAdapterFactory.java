package com.klanting.signclick.utils.Serializers;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.klanting.signclick.economy.CompanyOwnerManager;

import java.io.IOException;

public class CompanyOwnerManagerAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!CompanyOwnerManager.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                T obj = delegate.read(in);

                if (obj instanceof CompanyOwnerManager manager) {
                    manager.fixBoard();
                }
                return obj;
            }
        };
    }
}
