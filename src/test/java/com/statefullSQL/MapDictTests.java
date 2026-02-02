package com.statefullSQL;


import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.SQLSerializer;
import com.klanting.signclick.utils.statefullSQL.access.MapDict;
import com.klanting.signclick.utils.statefullSQL.access.OrderedList;
import io.ebeaninternal.server.util.Str;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import static org.gradle.internal.impldep.org.junit.Assert.*;



@ClassFlush
class MapDummy{

    private int val = 1;

    public int hello(){
        return val;
    }

    public void inc(){
        val += 1;
    }


}

public class MapDictTests {

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
    void simpleOperations(){

        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, MapDummy> dummies = new MapDict<>("a",String.class, MapDummy.class);
        MapDummy predum = new MapDummy();

        MapDummy dum = dummies.createRow("S", predum);

        assertEquals(1, dum.hello());
        dum.inc();
        assertEquals(2, dum.hello());
        assertTrue(dummies.containsKey("S"));

        /*
        * get object
        * */
        dum = dummies.get("S");
        assertEquals(2, dum.hello());

        /*
        * override object with other object
        * */
        predum = new MapDummy();
        dummies.put("S", predum);
        assertEquals(1, dummies.get("S").hello());

        /*
        * check keyset working correctly
        * */
        assertEquals(1, dummies.keySet().size());
        assertTrue(dummies.keySet().contains("S"));


    }

}
