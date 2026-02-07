package com.statefulSQL;

import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.OrderedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import java.util.HashMap;
import java.util.Map;

import static org.gradle.internal.impldep.org.testng.AssertJUnit.assertEquals;
import static org.gradle.internal.impldep.org.testng.AssertJUnit.assertNotNull;


@ClassFlush
class DummyParent{
    public int getValp() {
        return valp;
    }

    public void setValp(int valp) {
        this.valp = valp;
    }

    private int valp;

    public DummyParent(){
        valp = 1;
    }
}

@ClassFlush
class DummyChild extends DummyParent{
    public int getValc() {
        return valc;
    }

    public void setValc(int valc) {
        this.valc = valc;
    }

    private int valc;
    public DummyChild(){
        super();
        valc = 2;

    }

}

@ClassFlush
class DummyPtr extends DummyParent{
    public DummyParent getValPtr() {
        return valPtr;
    }

    public void setValPtr(DummyParent valPtr) {
        this.valPtr = valPtr;
    }

    private DummyParent valPtr;

    public DummyPtr(){
        valPtr = new DummyChild();
    }

}

@ClassFlush
class DummyPtrMap extends DummyParent{

    public Map<String, DummyParent> getValPtr() {
        return valPtr;
    }

    private Map<String, DummyParent> valPtr = new HashMap<>();

    public DummyPtrMap(){
        valPtr.put("A", new DummyChild());
        valPtr.put("B", new DummyChild());
    }

}

public class InheritanceTests {
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
    void simpleDerivedTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        OrderedList<DummyParent> dummies = new OrderedList<>("a",DummyParent.class);
        dummies.add(new DummyChild());

        DummyChild dc = (DummyChild) dummies.get(0);
        assertNotNull(dc);
        assertEquals(2, dc.getValc());
        assertEquals(1, dc.getValp());

        dc.setValp(15);
        assertEquals(15, dc.getValp());

        dc.setValc(9);
        assertEquals(9, dc.getValc());
    }

    @Test
    void simpleDerivedPtrTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        OrderedList<DummyPtr> dummies = new OrderedList<>("a",DummyPtr.class);
        dummies.add(new DummyPtr());

        DummyPtr ptr = dummies.get(0);

        assertEquals(1, ptr.getValPtr().getValp());
    }

    @Test
    void simpleDerivedPtrMapTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        OrderedList<DummyPtrMap> dummies = new OrderedList<>("a",DummyPtrMap.class);
        dummies.add(new DummyPtrMap());

        DummyPtrMap ptr = dummies.get(0);

        assertEquals(1, ptr.getValPtr().get("A").getValp());
        DummyChild child = (DummyChild) ptr.getValPtr().get("A");
        assertEquals(2, child.getValc());

    }

}
