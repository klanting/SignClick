package com.statefulSQL;


import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;
import com.klanting.signclick.utils.statefulSQL.access.OrderedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

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
        return value.val;
    }

    @Override
    public WEIRDOBJECT deserialize(String value) {
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

@ClassFlush
class DummyEquals{

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;           // Same reference
        if (obj == null) return false;          // Null check
        if (!(obj instanceof DummyEquals d)) return false;

        return val == d.val;
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

        dummies.clear();
        assertTrue(dummies.isEmpty());
    }

    @Test
    void iterator(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<Dummy2> dummies = new OrderedList<>("a",Dummy2.class);
        Dummy2 predum1 = new Dummy2();
        Dummy2 predum2 = new Dummy2();
        predum2.inc();

        assertEquals(1, predum1.hello());
        assertEquals(2, predum2.hello());

        dummies.add(predum1);
        dummies.add(predum2);

        assertEquals(1, dummies.get(0).hello());
        assertEquals(2, dummies.get(1).hello());

        Iterator<Dummy2> it = dummies.iterator();

        assertTrue(it.hasNext());
        assertEquals(1, it.next().hello());
        assertTrue(it.hasNext());
        assertEquals(2, it.next().hello());
        assertFalse(it.hasNext());
    }

    @Test
    void sizeTest(){
        /*
        * check that size works correctly
        * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<Dummy2> dummies = new OrderedList<>("a",Dummy2.class);

        assertEquals(0, dummies.size());
        assertTrue(dummies.isEmpty());

        /*
        * add element
        * */
        dummies.add(new Dummy2());
        assertEquals(1, dummies.size());
        assertFalse(dummies.isEmpty());

        /*
        * add 2nd element
        * */
        dummies.add(new Dummy2());
        assertEquals(2, dummies.size());
        assertFalse(dummies.isEmpty());

        /*
        * add 3th element tp other list linking to same group
        * */
        OrderedList<Dummy2> dummies2 = new OrderedList<>("a",Dummy2.class);

        dummies2.add(new Dummy2());
        assertEquals(3, dummies.size());
        assertFalse(dummies.isEmpty());
        assertEquals(3, dummies2.size());
        assertFalse(dummies2.isEmpty());

        /*
        * add item to new ordered list group, so that group 'a' -> 3, 'b' -> 1
        * */
        OrderedList<Dummy2> dummies3 = new OrderedList<>("b",Dummy2.class);
        dummies3.add(new Dummy2());

        assertEquals(3, dummies.size());
        assertFalse(dummies.isEmpty());
        assertEquals(3, dummies2.size());
        assertFalse(dummies2.isEmpty());
        assertEquals(1, dummies3.size());
        assertFalse(dummies3.isEmpty());

    }

    @Test
    void containsTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<DummyEquals> dummies = new OrderedList<>("a",DummyEquals.class);
        DummyEquals preDum = new DummyEquals();

        assertFalse(dummies.contains(preDum));

        /*
        * add element
        * */
        dummies.add(preDum);
        assertTrue(dummies.contains(preDum));
        DummyEquals postDum = dummies.get(0);
        assertTrue(dummies.contains(postDum));
        assertEquals(preDum, postDum);

        /*
        * check if no false positive
        * */
        DummyEquals preDum2 = new DummyEquals();
        preDum2.inc();
        assertFalse(preDum.equals(preDum2));
        assertFalse(dummies.contains(preDum2));

        /*
        * delete item
        * */
        dummies.remove(postDum);
        assertFalse(dummies.contains(preDum));
        assertFalse(dummies.contains(preDum2));
        assertFalse(dummies.contains(postDum));

        OrderedList<DummyEquals> dummies2 = new OrderedList<>("b",DummyEquals.class);
        dummies2.add(preDum);

        assertFalse(dummies.contains(preDum));
        assertFalse(dummies.contains(preDum2));
        assertFalse(dummies.contains(postDum));

        assertTrue(dummies2.contains(preDum));

    }

    @Test
    void iterationTest(){
        /*
        * check that the iterator works correctly
        * */

        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<Dummy2> dummies = new OrderedList<>("a",Dummy2.class);

        /*
        * Add 2 dummy elements
        * */
        dummies.add(new Dummy2());
        dummies.add(new Dummy2());

        assertEquals(2, dummies.size());
        dummies.get(1).inc();

        /*
        * loop over each, and ensure first value 1, second value 2
        * */
        int counter = 1;
        for(Dummy2 dummy2: dummies){
            assertEquals(counter, dummy2.hello());
            counter += 1;
        }

        /*
        * check if correctly throws iterator concurrency error
        * */
        List<Dummy2> d = new OrderedList<>("b",Dummy2.class);
        d.add(new Dummy2());
        d.add(new Dummy2());
        d.add(new Dummy2());

        boolean throwsError = false;
        try {
            for (Dummy2 dummy2 : d) {
                d.remove(dummy2);
            }
        }catch (ConcurrentModificationException e){
            throwsError = true;
        }
        assertTrue(throwsError);
        assertFalse(d.isEmpty());

        /*
        * reinitialize
        * */
        d = new OrderedList<>("c",Dummy2.class);
        d.add(new Dummy2());
        d.add(new Dummy2());
        d.add(new Dummy2());
        /*
        * Check save delete still working
        * */
        Iterator<Dummy2> it = d.iterator();

        while (it.hasNext()) {
            Dummy2 element = it.next();
            it.remove();
        }
        assertEquals(0, d.size());

    }

    @Test
    void toArrayTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<DummyEquals> dummies = new OrderedList<>("a",DummyEquals.class);

        /*
         * Add 2 dummy elements
         * */
        dummies.add(new DummyEquals());
        dummies.add(new DummyEquals());

        assertEquals(2, dummies.size());
        dummies.get(1).inc();

        /*
        * Convert to Array
        * */
        Object[] dummiesArray = dummies.toArray();

        assertEquals(2, dummiesArray.length);
        assertTrue(dummiesArray[0].equals(new DummyEquals()));
        assertFalse(dummiesArray[1].equals(new DummyEquals()));

        /*
        * convert to Dummy2 Array (other group)
        * */
        OrderedList<Dummy2> dummies2 = new OrderedList<>("b",Dummy2.class);
        dummies2.add(new Dummy2());
        dummies2.add(new Dummy2());

        assertEquals(2, dummies2.size());
        dummies2.get(1).inc();

        Dummy2[] dummiesArray2 = dummies2.toArray(new Dummy2[2]);
        assertEquals(2, dummiesArray2.length);
        assertEquals(1, dummiesArray2[0].hello());
        assertEquals(2, dummiesArray2[1].hello());

        /*
         * convert to Dummy2 Array (same group but other class) -> illegal action
         * */
        boolean throwsError = false;
        try {
            dummies2 = new OrderedList<>("a",Dummy2.class);
        }catch (RuntimeException e){
            throwsError = true;
        }
        assertTrue(throwsError);

    }

    @Test
    void addAllTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<Dummy2> dummies = new OrderedList<>("a",Dummy2.class);

        Dummy2 preDum = new Dummy2();
        Dummy2 preDum2 = new Dummy2();
        preDum2.inc();
        Dummy2 preDum3 = new Dummy2();
        preDum3.inc();
        preDum3.inc();
        List<Dummy2> preDumList = List.of(preDum, preDum2, preDum3);

        dummies.addAll(preDumList);

        assertEquals(3, dummies.size());
        assertEquals(1, preDumList.get(0).hello());
        assertEquals(2, preDumList.get(1).hello());
        assertEquals(3, preDumList.get(2).hello());

    }

    @Test
    void retainAllTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<Dummy2> dummies = new OrderedList<>("a",Dummy2.class);

        Dummy2 preDum = new Dummy2();
        Dummy2 preDum2 = new Dummy2();
        preDum2.inc();
        Dummy2 preDum3 = new Dummy2();
        preDum3.inc();
        preDum3.inc();
        Dummy2 preDum4 = new Dummy2();
        preDum4.inc();
        preDum4.inc();
        preDum4.inc();

        dummies.add(preDum);
        dummies.add(preDum2);
        dummies.add(preDum3);
        dummies.add(preDum4);

        List<Dummy2> retainList = List.of(dummies.get(1), dummies.get(2), new Dummy2());

        dummies.retainAll(retainList);
        assertEquals(2, dummies.size());
        assertEquals(2, dummies.get(0).hello());
        assertEquals(3, dummies.get(1).hello());
    }

}
