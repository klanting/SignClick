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


        Class<?> clazz = self.getClass();

        System.out.println("LOL "+clazz);
        Field field2 = clazz.getDeclaredField("uuid");
        UUID uuid = (UUID) field2.get(self);
        Map<String, Object> values = DatabaseSingleton.getInstance().getDataByKey(uuid,clazz);

        System.out.println("X "+clazz.getSuperclass());
        T instance = DatabaseSingleton.getInstance().wrap(clazz.getSuperclass(), values);
        System.out.println("D "+instance);

        System.out.println("M "+method.getName());
        if (method.getName().equals("equals") && args.length == 1) {
            System.out.println("M2 "+method.getName());
            Object other = args[0];

            if (other == null) return false;
            System.out.println("M3 "+method.getName());
            System.out.println(clazz.getSuperclass().isInstance(other));
            System.out.println("V2"+ other.toString());

            try {
                Field uuidField = clazz.getDeclaredField("uuid");
                Field uui2dField = other.getClass().getDeclaredField("uuid");
                uuidField.setAccessible(true);
                UUID thisUuid = (UUID) uuidField.get(self);
                UUID otherUuid = (UUID) uui2dField.get(other);

                System.out.println("M4 "+thisUuid+" "+otherUuid);
                return thisUuid.equals(otherUuid);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
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