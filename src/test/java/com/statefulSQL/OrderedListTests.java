package com.statefulSQL;


import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;
import com.klanting.signclick.utils.statefulSQL.access.OrderedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import static org.gradle.internal.impldep.org.junit.Assert.*;


class OrderedListWeirdObject {

    public String val = "1";

}

class OrderedListWeirdObjectSerializer extends SQLSerializer<OrderedListWeirdObject>{

    public OrderedListWeirdObjectSerializer(Class type) {
        super(type);
    }

    @Override
    public String serialize(OrderedListWeirdObject value) {
        return value.val;
    }

    @Override
    public OrderedListWeirdObject deserialize(String value) {
        OrderedListWeirdObject o = new OrderedListWeirdObject();
        o.val = value;
        return o;
    }
}

@ClassFlush
class OrderedListDummy {

    private int val = 1;

    public OrderedListWeirdObject getW() {
        return w;
    }

    public OrderedListWeirdObject w = new OrderedListWeirdObject();

    public int hello(){
        return val;
    }

    public void inc(){
        val += 1;
    }

}

@ClassFlush
class OrderedListDummy2 {

    private int val = 1;

    public OrderedListDummy3 getDummy3() {
        return dummy3;
    }

    private final OrderedListDummy3 dummy3 = new OrderedListDummy3();

    public int hello(){
        return val;
    }

    public void inc(){
        val += 1;
    }

}


@ClassFlush
class OrderedListDummy3 {

    private int val = 1;

    public int hello(){
        return val;
    }

    public void inc(){
        val += 1;
    }

}

@ClassFlush
class OrderedListDummyEquals {

    private int val = 1;

    public OrderedListDummy3 getDummy3() {
        return dummy3;
    }

    private final OrderedListDummy3 dummy3 = new OrderedListDummy3();

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
        if (!(obj instanceof OrderedListDummyEquals d)) return false;

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
        DatabaseSingleton.getInstance().registerSerializer(new OrderedListWeirdObjectSerializer(OrderedListWeirdObject.class));

        OrderedList<OrderedListDummy> dummies = new OrderedList<>("a", OrderedListDummy.class);
        OrderedListDummy predum = new OrderedListDummy();
        predum.w.val = "30";
        OrderedListDummy dum = dummies.createRow(predum);
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
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);
        OrderedListDummy2 dum = dummies.createRow(new OrderedListDummy2());
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
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);
        assertTrue(dummies.isEmpty());
        /*
        * Add item to list
        * */
        assertTrue(dummies.add(new OrderedListDummy2()));

        assertEquals(1, dummies.size());
        assertFalse(dummies.isEmpty());

        OrderedListDummy2 preDum2 = new OrderedListDummy2();
        preDum2.inc();
        assertEquals(2, preDum2.hello());
        OrderedListDummy2 dum2 = dummies.createRow(preDum2);

        assertEquals(2, dummies.size());

        assertEquals(1, dummies.indexOf(dum2));
        assertEquals(2, dummies.get(1).hello());

        dummies.clear();
        assertTrue(dummies.isEmpty());
    }

    @Test
    void iterator(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);
        OrderedListDummy2 predum1 = new OrderedListDummy2();
        OrderedListDummy2 predum2 = new OrderedListDummy2();
        predum2.inc();

        assertEquals(1, predum1.hello());
        assertEquals(2, predum2.hello());

        dummies.add(predum1);
        dummies.add(predum2);

        assertEquals(1, dummies.get(0).hello());
        assertEquals(2, dummies.get(1).hello());

        Iterator<OrderedListDummy2> it = dummies.iterator();

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
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);

        assertEquals(0, dummies.size());
        assertTrue(dummies.isEmpty());

        /*
        * add element
        * */
        dummies.add(new OrderedListDummy2());
        assertEquals(1, dummies.size());
        assertFalse(dummies.isEmpty());

        /*
        * add 2nd element
        * */
        dummies.add(new OrderedListDummy2());
        assertEquals(2, dummies.size());
        assertFalse(dummies.isEmpty());

        /*
        * add 3th element tp other list linking to same group
        * */
        OrderedList<OrderedListDummy2> dummies2 = new OrderedList<>("a", OrderedListDummy2.class);

        dummies2.add(new OrderedListDummy2());
        assertEquals(3, dummies.size());
        assertFalse(dummies.isEmpty());
        assertEquals(3, dummies2.size());
        assertFalse(dummies2.isEmpty());

        /*
        * add item to new ordered list group, so that group 'a' -> 3, 'b' -> 1
        * */
        OrderedList<OrderedListDummy2> dummies3 = new OrderedList<>("b", OrderedListDummy2.class);
        dummies3.add(new OrderedListDummy2());

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
        OrderedList<OrderedListDummyEquals> dummies = new OrderedList<>("a", OrderedListDummyEquals.class);
        OrderedListDummyEquals preDum = new OrderedListDummyEquals();

        assertFalse(dummies.contains(preDum));

        /*
        * add element
        * */
        dummies.add(preDum);
        assertTrue(dummies.contains(preDum));
        OrderedListDummyEquals postDum = dummies.get(0);
        assertTrue(dummies.contains(postDum));
        assertEquals(preDum, postDum);

        /*
        * check if no false positive
        * */
        OrderedListDummyEquals preDum2 = new OrderedListDummyEquals();
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

        OrderedList<OrderedListDummyEquals> dummies2 = new OrderedList<>("b", OrderedListDummyEquals.class);
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
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);

        /*
        * Add 2 dummy elements
        * */
        dummies.add(new OrderedListDummy2());
        dummies.add(new OrderedListDummy2());

        assertEquals(2, dummies.size());
        dummies.get(1).inc();

        /*
        * loop over each, and ensure first value 1, second value 2
        * */
        int counter = 1;
        for(OrderedListDummy2 dummy2: dummies){
            assertEquals(counter, dummy2.hello());
            counter += 1;
        }

        /*
        * check if correctly throws iterator concurrency error
        * */
        List<OrderedListDummy2> d = new OrderedList<>("b", OrderedListDummy2.class);
        d.add(new OrderedListDummy2());
        d.add(new OrderedListDummy2());
        d.add(new OrderedListDummy2());

        boolean throwsError = false;
        try {
            for (OrderedListDummy2 dummy2 : d) {
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
        d = new OrderedList<>("c", OrderedListDummy2.class);
        d.add(new OrderedListDummy2());
        d.add(new OrderedListDummy2());
        d.add(new OrderedListDummy2());
        /*
        * Check save delete still working
        * */
        Iterator<OrderedListDummy2> it = d.iterator();

        while (it.hasNext()) {
            OrderedListDummy2 element = it.next();
            it.remove();
        }
        assertEquals(0, d.size());

    }

    @Test
    void toArrayTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<OrderedListDummyEquals> dummies = new OrderedList<>("a", OrderedListDummyEquals.class);

        /*
         * Add 2 dummy elements
         * */
        dummies.add(new OrderedListDummyEquals());
        dummies.add(new OrderedListDummyEquals());

        assertEquals(2, dummies.size());
        dummies.get(1).inc();

        /*
        * Convert to Array
        * */
        Object[] dummiesArray = dummies.toArray();

        assertEquals(2, dummiesArray.length);
        assertTrue(dummiesArray[0].equals(new OrderedListDummyEquals()));
        assertFalse(dummiesArray[1].equals(new OrderedListDummyEquals()));

        /*
        * convert to Dummy2 Array (other group)
        * */
        OrderedList<OrderedListDummy2> dummies2 = new OrderedList<>("b", OrderedListDummy2.class);
        dummies2.add(new OrderedListDummy2());
        dummies2.add(new OrderedListDummy2());

        assertEquals(2, dummies2.size());
        dummies2.get(1).inc();

        OrderedListDummy2[] dummiesArray2 = dummies2.toArray(new OrderedListDummy2[2]);
        assertEquals(2, dummiesArray2.length);
        assertEquals(1, dummiesArray2[0].hello());
        assertEquals(2, dummiesArray2[1].hello());

        /*
         * convert to Dummy2 Array (same group but other class) -> illegal action
         * */
        boolean throwsError = false;
        try {
            dummies2 = new OrderedList<>("a", OrderedListDummy2.class);
        }catch (RuntimeException e){
            throwsError = true;
        }
        assertTrue(throwsError);

    }

    @Test
    void addAllTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);

        OrderedListDummy2 preDum = new OrderedListDummy2();
        OrderedListDummy2 preDum2 = new OrderedListDummy2();
        preDum2.inc();
        OrderedListDummy2 preDum3 = new OrderedListDummy2();
        preDum3.inc();
        preDum3.inc();
        List<OrderedListDummy2> preDumList = List.of(preDum, preDum2, preDum3);

        dummies.addAll(preDumList);

        assertEquals(3, dummies.size());
        assertEquals(1, preDumList.get(0).hello());
        assertEquals(2, preDumList.get(1).hello());
        assertEquals(3, preDumList.get(2).hello());

    }

    @Test
    void retainAllTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);

        OrderedListDummy2 preDum = new OrderedListDummy2();
        OrderedListDummy2 preDum2 = new OrderedListDummy2();
        preDum2.inc();
        OrderedListDummy2 preDum3 = new OrderedListDummy2();
        preDum3.inc();
        preDum3.inc();
        OrderedListDummy2 preDum4 = new OrderedListDummy2();
        preDum4.inc();
        preDum4.inc();
        preDum4.inc();

        dummies.add(preDum);
        dummies.add(preDum2);
        dummies.add(preDum3);
        dummies.add(preDum4);

        List<OrderedListDummy2> retainList = List.of(dummies.get(1), dummies.get(2), new OrderedListDummy2());

        dummies.retainAll(retainList);
        assertEquals(2, dummies.size());
        assertEquals(2, dummies.get(0).hello());
        assertEquals(3, dummies.get(1).hello());
    }

    @Test
    void clearList(){
        /*
        * clear list and check all correctly removed
        * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);

        dummies.add(new OrderedListDummy2());
        dummies.add(new OrderedListDummy2());
        dummies.add(new OrderedListDummy2());
        assertEquals(3, dummies.size());

        dummies.clear();
        assertEquals(0, dummies.size());
    }

    @Test
    void tempRemovedFromList(){
        /*
        * Have an item from a list (store in sql), keep temp pointer and remove it from access list, add it to new access list
        * */

        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);
        OrderedList<OrderedListDummy2> dummies2 = new OrderedList<>("b", OrderedListDummy2.class);

        OrderedListDummy2 dum = dummies.createRow(new OrderedListDummy2());
        assertEquals(1, dummies.size());

        /*
        * remove from original list
        * */
        dummies.remove(dum);

        /*
        * check if intermediate state still actively update
        * */
        assertEquals(1, dum.hello());
        dum.inc();
        assertEquals(2, dum.hello());

        /*
        * add to new list
        * */
        dummies2.add(dum);

        assertEquals(0, dummies.size());
        assertEquals(1, dummies2.size());

        /*
        * see updated value in dummies2
        * */
        assertEquals(2, dummies2.get(0).hello());
    }

    @Test
    void addIndex(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);

        OrderedListDummy2 dum = new OrderedListDummy2();
        OrderedListDummy2 dum2 = new OrderedListDummy2();
        dum2.inc();

        dummies.add(dum);
        /*
        * set Dummy 2 (with value 2 on first index)
        * */
        dummies.add(0, dum2);

        assertEquals(2, dummies.size());
        assertEquals(2, dummies.get(0).hello());
        assertEquals(1, dummies.get(1).hello());
    }

    @Test
    void setIndex(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);

        OrderedListDummy2 dum = new OrderedListDummy2();
        OrderedListDummy2 dum2 = new OrderedListDummy2();
        dum2.inc();
        OrderedListDummy2 dum3 = new OrderedListDummy2();
        dum3.inc();
        dum3.inc();

        dummies.add(dum);
        dummies.add(dum2);

        assertEquals(2, dummies.size());
        assertEquals(1, dummies.get(0).hello());
        assertEquals(2, dummies.get(1).hello());

        /*
         * set Dummy2 on index 0
         * */
        dummies.set(0, dum3);

        assertEquals(2, dummies.size());
        assertEquals(3, dummies.get(0).hello());
        assertEquals(2, dummies.get(1).hello());
    }

    @Test
    void addAll(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        /*
        * prepare original collection
        * */
        List<OrderedListDummy2> preDummies = new ArrayList<>();
        OrderedListDummy2 dum = new OrderedListDummy2();
        OrderedListDummy2 dum2 = new OrderedListDummy2();
        dum2.inc();
        OrderedListDummy2 dum3 = new OrderedListDummy2();
        dum3.inc();
        dum3.inc();

        preDummies.add(dum);
        preDummies.add(dum2);
        preDummies.add(dum3);

        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);
        dummies.addAll(preDummies);

        assertEquals(3, dummies.size());
        assertEquals(1, dummies.get(0).hello());
        assertEquals(2, dummies.get(1).hello());
        assertEquals(3, dummies.get(2).hello());
    }

    @Test
    void subList(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        OrderedListDummy2 dum = new OrderedListDummy2();
        OrderedListDummy2 dum2 = new OrderedListDummy2();
        dum2.inc();
        OrderedListDummy2 dum3 = new OrderedListDummy2();
        dum3.inc();
        dum3.inc();
        OrderedListDummy2 dum4 = new OrderedListDummy2();
        dum4.inc();
        dum4.inc();
        dum4.inc();

        OrderedList<OrderedListDummy2> dummies = new OrderedList<>("a", OrderedListDummy2.class);
        dummies.add(dum);
        dummies.add(dum2);
        dummies.add(dum3);
        assertEquals(3, dummies.size());

        List<OrderedListDummy2> subList = dummies.subList(1, 2);
        assertEquals(1, subList.size());
        assertEquals(2, subList.get(0).hello());

        subList.set(0, dum4);

        assertEquals(3, dummies.size());
        assertEquals(4, dummies.get(1).hello());

    }

}
