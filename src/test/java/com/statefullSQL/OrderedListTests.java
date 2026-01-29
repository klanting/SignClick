package com.statefullSQL;


import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.SQLSerializer;
import com.klanting.signclick.utils.statefullSQL.access.OrderedList;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import static org.gradle.internal.impldep.org.junit.Assert.*;


class WEIRDOBJECT{

    public String val = "1";

}

class WEIRDOBJECTSERIALIZER extends SQLSerializer<WEIRDOBJECT>{

    public WEIRDOBJECTSERIALIZER(Class type) {
        super(type);
    }

    @Override
    public String serialize(WEIRDOBJECT value) {
        System.out.println("A555");
        return value.val;
    }

    @Override
    public WEIRDOBJECT deserialize(String value) {
        System.out.println("B555");
        WEIRDOBJECT o = new WEIRDOBJECT();
        o.val = value;
        return o;
    }
}

@ClassFlush
class Dummy{

    private int val = 1;

    public WEIRDOBJECT getW() {
        return w;
    }

    public WEIRDOBJECT w = new WEIRDOBJECT();

    public int hello(){
        return val;
    }

    public void inc(){
        val += 1;
    }

}

@ClassFlush
class Dummy2{

    private int val = 1;

    public Dummy3 getDummy3() {
        return dummy3;
    }

    private final Dummy3 dummy3 = new Dummy3();

    public int hello(){
        return val;
    }

    public void inc(){
        val += 1;
    }

}


@ClassFlush
class Dummy3{

    private int val = 1;

    public int hello(){
        return val;
    }

    public void inc(){
        val += 1;
    }

}

public class OrderedListTests {

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
    void createRow(){

        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        DatabaseSingleton.getInstance().registerSerializer(new WEIRDOBJECTSERIALIZER(WEIRDOBJECT.class));

        OrderedList<Dummy> dummies = new OrderedList<>("a",Dummy.class);
        Dummy predum = new Dummy();
        predum.w.val = "30";
        Dummy dum = dummies.createRow(predum);
        assertEquals(1, dum.hello());
        dum.inc();
        assertEquals(2, dum.hello());
        assertEquals(1, dummies.size());
        assertTrue(dummies.contains(dum));
        assertEquals("30", dum.getW().val);
    }

    @Test
    void createRowChained(){
        /*
        * Dummy2 has a reference to Dummy 3
        * */

        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<Dummy2> dummies = new OrderedList<>("a",Dummy2.class);
        Dummy2 dum = dummies.createRow(new Dummy2());
        assertEquals(1, dum.hello());
        dum.inc();
        assertEquals(2, dum.hello());

        assertEquals(1, dum.getDummy3().hello());
        dum.getDummy3().inc();
        assertEquals(2, dum.getDummy3().hello());
        assertEquals(1, dummies.size());

    }

    @Test
    void accessMethods(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<Dummy2> dummies = new OrderedList<>("a",Dummy2.class);
        assertTrue(dummies.isEmpty());
        /*
        * Add item to list
        * */
        assertTrue(dummies.add(new Dummy2()));

        assertEquals(1, dummies.size());
        assertFalse(dummies.isEmpty());

        Dummy2 preDum2 = new Dummy2();
        preDum2.inc();
        assertEquals(2, preDum2.hello());
        Dummy2 dum2 = dummies.createRow(preDum2);

        assertEquals(2, dummies.size());

        assertEquals(1, dummies.indexOf(dum2));
        assertEquals(2, dummies.get(1).hello());


    }
}
