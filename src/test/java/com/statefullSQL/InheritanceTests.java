package com.statefullSQL;

import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.access.OrderedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;
import tools.DataBaseTest;

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
    }

}
