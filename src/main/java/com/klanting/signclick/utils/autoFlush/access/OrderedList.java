package com.klanting.signclick.utils.autoFlush.access;

import com.klanting.signclick.utils.autoFlush.ClassFlush;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.*;


public class OrderedList<T> implements AccessPoint{


    public T createRow(T entity) {

        /*
        * flush to disk here
        * */

        Class<T> clazz = (Class<T>)  entity.getClass();

        assert clazz.isAnnotationPresent(ClassFlush.class);

        try {

            return new ByteBuddy()
                    .subclass(clazz)
                    .method(
                            ElementMatchers.not(named("clone"))
                    )
                    .intercept(MethodDelegation.to(new InterceptorWrap<T>(entity)))
                    .make()
                    .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
