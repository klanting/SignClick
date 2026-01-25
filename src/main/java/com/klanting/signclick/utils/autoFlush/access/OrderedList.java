package com.klanting.signclick.utils.autoFlush.access;

import com.klanting.signclick.utils.autoFlush.ClassFlush;
import com.klanting.signclick.utils.autoFlush.DatabaseSingleton;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.sql.DatabaseMetaData;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.*;


public class OrderedList<T> implements AccessPoint{


    public T createRow(T entity) {

        /*
        * flush to disk here
        * */

        Class<T> clazz = (Class<T>)  entity.getClass();

        assert clazz.isAnnotationPresent(ClassFlush.class);

        try {

            /*
            * Store class in SQL
            * */
            UUID id = DatabaseSingleton.getInstance().store(entity);


            /*
            * override class so it contains a UUID
            * */
            Class<? extends T> dynamicType = new ByteBuddy()
                    .subclass(clazz)
                    .defineField("uuid", UUID.class, Modifier.PUBLIC)
                    .method(
                            not(named("clone"))
                    )
                    .intercept(MethodDelegation.to(new InterceptorWrap<T>(entity)))
                    .make()
                    .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            T obj = dynamicType.getDeclaredConstructor().newInstance();
            dynamicType.getField("uuid").set(obj, id);
            return obj;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
