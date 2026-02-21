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
class InheritanceDummyParent {
    public int getValp() {
        return valp;
    }

    public void setValp(int valp) {
        this.valp = valp;
    }

    private int valp;

    public InheritanceDummyParent(){
        valp = 1;
    }
}

@ClassFlush
class InheritanceDummyChild extends InheritanceDummyParent {
    public int getValc() {
        return valc;
    }

    public void setValc(int valc) {
        this.valc = valc;
    }

    private int valc;
    public InheritanceDummyChild(){
        super();
        valc = 2;

    }

}

@ClassFlush
class InheritanceDummyPtr extends InheritanceDummyParent {
    public InheritanceDummyParent getValPtr() {
        return valPtr;
    }

    public void setValPtr(InheritanceDummyParent valPtr) {
        this.valPtr = valPtr;
    }

    private InheritanceDummyParent valPtr;

    public InheritanceDummyPtr(){
        valPtr = new InheritanceDummyChild();
    }

}

@ClassFlush
class InheritanceDummyPtrMap extends InheritanceDummyParent {

    public Map<String, InheritanceDummyParent> getValPtr() {
        return valPtr;
    }

    private Map<String, InheritanceDummyParent> valPtr = new HashMap<>();

    public InheritanceDummyPtrMap(){
        valPtr.put("A", new InheritanceDummyChild());
        valPtr.put("B", new InheritanceDummyChild());
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

        OrderedList<InheritanceDummyParent> dummies = new OrderedList<>("a", InheritanceDummyParent.class);
        dummies.add(new InheritanceDummyChild());

        InheritanceDummyChild dc = (InheritanceDummyChild) dummies.get(0);
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

        OrderedList<InheritanceDummyPtr> dummies = new OrderedList<>("a", InheritanceDummyPtr.class);
        dummies.add(new InheritanceDummyPtr());

        InheritanceDummyPtr ptr = dummies.get(0);

        assertEquals(1, ptr.getValPtr().getValp());
    }

    @Test
    void simpleDerivedPtrMapTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        OrderedList<InheritanceDummyPtrMap> dummies = new OrderedList<>("a", InheritanceDummyPtrMap.class);
        dummies.add(new InheritanceDummyPtrMap());

        InheritanceDummyPtrMap ptr = dummies.get(0);

        assertEquals(1, ptr.getValPtr().get("A").getValp());
        InheritanceDummyChild child = (InheritanceDummyChild) ptr.getValPtr().get("A");
        assertEquals(2, child.getValc());

    }

}
