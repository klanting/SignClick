package com.statefulSQL;


import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.MapDict;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import static org.gradle.internal.impldep.org.testng.AssertJUnit.assertEquals;

@ClassFlush
class ArrayDummy1{
    private int[] array1 = {1, 2, -1, 4};
    private String[] array2 = {"", "a", null};

    private String[] array3 = {null, "", "a"};

    public int[] getArray1() {
        return array1;
    }

    public String[] getArray2() {
        return array2;
    }

    public String[] getArray3() {
        return array3;
    }
}

public class ArrayTests {
    /*
    * Test suite containing objects with arrays
    * */

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
    void arraySerializer(){
        /*
        * check that the array serializer works properly for array fields.
        * */

        //DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, ArrayDummy1> dummies = new MapDict<>("a",String.class, ArrayDummy1.class);
        ArrayDummy1 a = dummies.createRow("A", new ArrayDummy1());

        /*
        * verify arrays correct init
        * */
        assertEquals(4, a.getArray1().length);
        assertEquals(1, a.getArray1()[0]);
        assertEquals(2, a.getArray1()[1]);
        assertEquals(-1, a.getArray1()[2]);
        assertEquals(4, a.getArray1()[3]);
    }
}
