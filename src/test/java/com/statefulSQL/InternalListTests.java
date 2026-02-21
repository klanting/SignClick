package com.statefulSQL;

import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.MapDict;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import java.util.ArrayList;
import java.util.List;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;
import static org.gradle.internal.impldep.org.junit.Assert.assertTrue;

@ClassFlush
class InternalListDummy5 {
    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    private int val = 1;

}

@ClassFlush
class InternalListDummy4 {

    public int val2 = 3;

    public List<InternalListDummy5> getDummies5() {
        return dummies5;
    }

    private final List<InternalListDummy5> dummies5 = new ArrayList<>();
    public InternalListDummy4(){
        dummies5.add(new InternalListDummy5());
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

        MapDict<String, InternalListDummy4> dummies = new MapDict<>("a",String.class, InternalListDummy4.class);
        InternalListDummy4 dum = dummies.createRow("S", new InternalListDummy4());

        assertEquals(1, dum.getDummies5().size());
        assertTrue(dum.getDummies5().contains(dum.getDummies5().get(0)));

        /*
        * load system again
        * */
        dummies = new MapDict<>("a",String.class, InternalListDummy4.class);
        assertEquals(1, dummies.size());
        assertEquals(1, dummies.get("S").getDummies5().size());

    }

    @Test
    void otherReference(){
        /*
        * case where 2 different objects reference the same InternalListDummy5 with an internal list, but 1 removes it, ensure other doesn't remove it
        * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, InternalListDummy4> dummies = new MapDict<>("a",String.class, InternalListDummy4.class);
        InternalListDummy4 dum = dummies.createRow("S", new InternalListDummy4());
        InternalListDummy4 dum2 = dummies.createRow("S", new InternalListDummy4());

        /*
        * add ref to other list
        * */
        dum2.getDummies5().add(dum.getDummies5().get(0));

        assertEquals(1, dum.getDummies5().size());
        assertEquals(2, dum2.getDummies5().size());
    }

}
