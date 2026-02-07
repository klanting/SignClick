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

import java.util.Set;
import java.util.UUID;

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

        dummies.get("S").inc();
        assertEquals(2, dummies.get("S").hello());

    }

    @Test
    void sizeTests(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, MapDummy> dummies = new MapDict<>("a",String.class, MapDummy.class);
        MapDummy preDum = new MapDummy();

        assertTrue(dummies.isEmpty());
        assertEquals(0, dummies.size());

        dummies.put("A", preDum);
        dummies.put("B", preDum);

        assertEquals(2, dummies.size());
        assertFalse(dummies.isEmpty());

        /*
        * override element
        * */
        MapDummy preDum2 = new MapDummy();
        dummies.put("B", preDum2);
        assertEquals(2, dummies.size());

        /*
        * pop element
        * */
        dummies.remove("B");
        assertEquals(1, dummies.size());
    }

    @Test
    void intAsKeyTests(){
        /*
         * check that contains key works correctly, when an integer is the key
         * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<Integer, MapDummy> dummies = new MapDict<>("a",Integer.class, MapDummy.class);
        MapDummy preDum = new MapDummy();

        dummies.put(1, preDum);
        MapDummy dummy = dummies.get(1);

        assertNotNull(dummy);
        assertEquals(1, dummy.hello());

        assertTrue(dummies.containsKey(1));

        /*
        * check key set
        * */
        Set<Integer> integers = dummies.keySet();
        assertEquals(1, integers.size());
        assertTrue(integers.contains(1));

    }

    @Test
    void UUIDAsKeyTests(){
        /*
         * check that contains key works correctly, when an integer is the key
         * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<UUID, MapDummy> dummies = new MapDict<>("a",UUID.class, MapDummy.class);
        MapDummy preDum = new MapDummy();

        UUID uuid = UUID.randomUUID();

        dummies.put(uuid, preDum);
        MapDummy dummy = dummies.get(uuid);

        assertNotNull(dummy);
        assertEquals(1, dummy.hello());

        assertTrue(dummies.containsKey(uuid));

        /*
         * check key set
         * */
        Set<UUID> uuids = dummies.keySet();
        assertEquals(1, uuids.size());
        assertTrue(uuids.contains(uuid));

    }

    @Test
    void containsKeyTests(){
        /*
        * check that contains key works correctly
        * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, MapDummy> dummies = new MapDict<>("a",String.class, MapDummy.class);
        MapDummy preDum = new MapDummy();

        dummies.put("A", preDum);
        assertTrue(dummies.containsKey("A"));
    }

    @Test
    void containsValueTests(){
        /*
         * check that contains key works correctly
         * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, MapDummy> dummies = new MapDict<>("a",String.class, MapDummy.class);
        MapDummy preDum = new MapDummy();

        dummies.put("A", preDum);
        MapDummy dum = dummies.get("A");
        assertTrue(dummies.containsValue(dum));
    }

}
