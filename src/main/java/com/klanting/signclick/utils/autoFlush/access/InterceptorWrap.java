package com.klanting.signclick.utils.autoFlush.access;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class InterceptorWrap<T> {

    private final Object target;

    public InterceptorWrap(Object target) {
        this.target = target;
    }

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> superCall,
                            @Origin Method method,
                            @AllArguments Object[] args) throws Exception {

        System.out.println("Before " + method.getName());

        // call original method safely
        Object result = superCall.call();

        System.out.println("After " + method.getName());

        return result; // or modify result if you want
    }
}