package com.klanting.signclick.utils.autoFlush.access;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.gradle.internal.impldep.org.objenesis.Objenesis;
import org.gradle.internal.impldep.org.objenesis.ObjenesisStd;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class InterceptorWrap<T> {

    private final Object target;

    public InterceptorWrap(Object target) {
        this.target = target;
    }

    @RuntimeType
    public Object intercept(@Origin Method method,
                            @AllArguments Object[] args) throws Exception {

        Objenesis objenesis = new ObjenesisStd();
        Class<?> clazz = target.getClass();
        T instance = (T) objenesis.newInstance(clazz);

        Map<String, Object> values = new HashMap<>();
        values.put("val", 1);

        // Step 3: Populate fields manually
        for (var entry : values.entrySet()) {
            try {
                var field = clazz.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(instance, entry.getValue());
            } catch (NoSuchFieldException e) {
                // ignore missing fields
            }
        }

        System.out.println("Before " + method.getName());

        // call original method safely
        method.setAccessible(true);
        Object result = method.invoke(instance, args);

        System.out.println("After " + method.getName());

        return result; // or modify result if you want
    }
}