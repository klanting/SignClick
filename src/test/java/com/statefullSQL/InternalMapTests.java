package com.statefullSQL;

import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.access.MapDict;
import io.ebeaninternal.server.util.Str;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

}
