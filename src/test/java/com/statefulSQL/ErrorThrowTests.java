package com.statefulSQL;

import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.MapDict;
import io.ebeaninternal.server.util.Str;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;
import static org.junit.Assert.*;

import static org.gradle.internal.impldep.org.testng.AssertJUnit.assertEquals;

@ClassFlush
class ErrorThrowClass1{
    public int getVal() {
        return val;
    }

    private int val = 1;

    public void increase(boolean error) throws IllegalAccessException{
        val += 1;

        if(error){
            throw new IllegalAccessException("hello");
        }
        val += 1;

    }

    public String increaseWrapper(){
        /*
        * try and catches the error method
        * */
        try{
            increase(true);
        }catch (IllegalAccessException e){
            return e.getMessage();
        }
        return null;

    }
}

public class ErrorThrowTests {
    @BeforeEach
    public void setUp() throws Exception {
        DataBaseTest.initDb();
    }

    @AfterEach
    public void tearDown() throws Exception {
        DataBaseTest.shutdown();
        DatabaseSingleton.clear();
    }

    @Test
    void simpleErrorThrown(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, ErrorThrowClass1> dummies = new MapDict<>("a",String.class, ErrorThrowClass1.class);
        ErrorThrowClass1 dum = dummies.createRow("S", new ErrorThrowClass1());

        assertEquals(1, dum.getVal());
        assertThrows(IllegalAccessException.class, () -> dum.increase(true));
        assertEquals(2, dum.getVal());

        /*
        * check try catch keeps working
        * */
        assertEquals("hello", dum.increaseWrapper());
    }
}
