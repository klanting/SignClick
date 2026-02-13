package com.statefulSQL;

import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.MapDict;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import java.util.HashMap;
import java.util.Map;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;
import static org.gradle.internal.impldep.org.junit.Assert.assertTrue;

@ClassFlush
class Dummy7{
    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    private int val = 1;

}

@ClassFlush
class Dummy6{

    public int val2 = 3;

    public Map<String, Dummy7> getDummies5() {
        return dummies5;
    }

    private final Map<String, Dummy7> dummies5 = new HashMap<>();
    public Dummy6(){
        dummies5.put("A", new Dummy7());
    }
}

@ClassFlush
class Dummy9{
    /*
    * circular mapping Dummy9 has Map for Dummy8, and Dummy8 has Map for Dummy 9
    * */

    public int getVal2() {
        return val2;
    }

    public int val2 = 3;

    public Map<String, Dummy8> getDummies5() {
        return dummies8;
    }

    private final Map<String, Dummy8> dummies8 = new HashMap<>();
    public Dummy9(){
        dummies8.put("A", new Dummy8());
    }
}

@ClassFlush
class Dummy8{
    /*
     * circular mapping Dummy9 has Map for Dummy8, and Dummy8 has Map for Dummy 9
     * */

    public int getVal2() {
        return val2;
    }

    public int val2 = 4;

    public Map<String, Dummy9> getDummies5() {
        return dummies9;
    }

    private final Map<String, Dummy9> dummies9 = new HashMap<>();
    public Dummy8(){
    }
}

public class InternalMapTests {

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
    void simpleListAttribute(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, Dummy6> dummies = new MapDict<>("a",String.class, Dummy6.class);
        Dummy6 dum = dummies.createRow("S", new Dummy6());

        assertTrue(dum.getDummies5().containsKey("A"));
        assertEquals(1, dum.getDummies5().get("A").getVal());

        dum.getDummies5().get("A").setVal(2);
        assertEquals(2, dum.getDummies5().get("A").getVal());

    }

    @Test
    void circularMappingAttribute(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, Dummy9> dummies = new MapDict<>("a",String.class, Dummy9.class);
        Dummy9 dum = dummies.createRow("A", new Dummy9());
        Dummy8 dum8 = dum.getDummies5().get("A");

        /*
        * make the circular dependency circular
        * */
        dum8.getDummies5().put("B", dum);

        assertEquals(3, dum.getVal2());
        assertEquals(4, dum8.getVal2());

    }

}
