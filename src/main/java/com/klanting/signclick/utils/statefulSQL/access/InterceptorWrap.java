package com.klanting.signclick.utils.statefulSQL.access;

import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class InterceptorWrap<T> {


    public InterceptorWrap() {
    }

    @RuntimeType
    public Object intercept(
            @This Object self,
            @Origin Method method,
            @AllArguments Object[] args) throws Exception {

        Class<?> clazz = self.getClass();

        Field field2 = clazz.getDeclaredField("autoFlushId");
        UUID uuid = (UUID) field2.get(self);

        T instance;
        if (!DatabaseSingleton.getInstance().getRemoveCache().containsKey(uuid)){
            /*
            * when item in sql database
            * */
            Class<?> clazzOriginal = DatabaseSingleton.getInstance().getRealClass(uuid);
            instance = DatabaseSingleton.getInstance().getObjectByKey(uuid, clazzOriginal);
        }else{
            instance = (T) DatabaseSingleton.getInstance().getRemoveCache().get(uuid);
        }


        if (method.getName().equals("equals") && args.length == 1) {
            Object other = args[0];

            if (other == null) return false;

            try {
                Field uuidField = clazz.getDeclaredField("autoFlushId");
                Field uui2dField = other.getClass().getDeclaredField("autoFlushId");
                uuidField.setAccessible(true);
                UUID thisUuid = (UUID) uuidField.get(self);
                UUID otherUuid = (UUID) uui2dField.get(other);

                return thisUuid.equals(otherUuid);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                //TODO the direction should depend on which option is missing the field
                return other.equals(instance);
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