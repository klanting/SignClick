package com.statefullSQL;

import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.access.MapDict;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import java.util.ArrayList;
import java.util.List;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;
import static org.gradle.internal.impldep.org.junit.Assert.assertTrue;

@ClassFlush
class Dummy5{
    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    private int val = 1;

}

@ClassFlush
class Dummy4{

    public int val2 = 3;

    public List<Dummy5> getDummies5() {
        return dummies5;
    }

    private final List<Dummy5> dummies5 = new ArrayList<>();
    public Dummy4(){
        dummies5.add(new Dummy5());
    }
}

public class InternalListTests {

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

        MapDict<String, Dummy4> dummies = new MapDict<>("a",String.class, Dummy4.class);
        Dummy4 dum = dummies.createRow("S", new Dummy4());

        assertEquals(1, dum.getDummies5().size());
        assertTrue(dum.getDummies5().contains(dum.getDummies5().get(0)));

        /*
        * load system again
        * */
        dummies = new MapDict<>("a",String.class, Dummy4.class);
        assertEquals(1, dummies.size());
        assertEquals(1, dummies.get("S").getDummies5().size());




    }

}
