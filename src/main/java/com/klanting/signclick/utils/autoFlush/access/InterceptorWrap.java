package com.klanting.signclick.utils.autoFlush.access;

import com.klanting.signclick.utils.autoFlush.ClassFlush;
import com.klanting.signclick.utils.autoFlush.DatabaseSingleton;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import org.gradle.internal.impldep.org.objenesis.Objenesis;
import org.gradle.internal.impldep.org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class InterceptorWrap<T> {


    public InterceptorWrap() {
    }

    @RuntimeType
    public Object intercept(
            @This Object self,
            @Origin Method method,
            @AllArguments Object[] args) throws Exception {

        Objenesis objenesis = new ObjenesisStd();
        Class<?> clazz = self.getClass();

        T instance = (T) objenesis.newInstance(clazz.getSuperclass());
        System.out.println("LOL "+clazz);
        Field field2 = clazz.getDeclaredField("uuid");
        UUID uuid = (UUID) field2.get(self);
        Map<String, Object> values = DatabaseSingleton.getInstance().getDataByKey(uuid,clazz);

        // Step 3: Populate fields manually
        for (var entry : values.entrySet()) {
            try {
                var field = clazz.getSuperclass().getDeclaredField(entry.getKey());

                Class<?> type = field.getType();
                if (type.isAnnotationPresent(ClassFlush.class) && entry.getValue() != null){

                    Class<?> dynamicType = new ByteBuddy()
                            .subclass(type)
                            .defineField("uuid", UUID.class, Modifier.PUBLIC)
                            .method(
                                    not(named("clone"))
                            )
                            .intercept(MethodDelegation.to(new InterceptorWrap<T>()))
                            .make()
                            .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                            .getLoaded();

                    Object obj = dynamicType.getDeclaredConstructor().newInstance();
                    dynamicType.getField("uuid").set(obj, entry.getValue());

                    field.setAccessible(true);
                    field.set(instance, obj);

                    continue;
                }

                field.setAccessible(true);
                field.set(instance, entry.getValue());
            } catch (NoSuchFieldException e) {
                // ignore missing fields
            }
        }

        // call original method safely
        method.setAccessible(true);
        Object result = method.invoke(instance, args);

        //Flush changes made to object
        DatabaseSingleton.getInstance().update(uuid, instance);

        return result; // or modify result if you want
    }
}