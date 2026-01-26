package com.autoFlush;


import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.events.MenuEvents;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.companyLogic.patent.Auction;
import com.klanting.signclick.interactionLayer.routines.AutoSave;
import com.klanting.signclick.utils.autoFlush.ClassFlush;
import com.klanting.signclick.utils.autoFlush.DatabaseSingleton;
import com.klanting.signclick.utils.autoFlush.access.OrderedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;
import static org.gradle.internal.impldep.org.junit.Assert.assertNotEquals;

@ClassFlush
class Dummy{

    private int val = 1;

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
        OrderedList<Dummy> dummies = new OrderedList<>();
        Dummy dum = dummies.createRow(new Dummy());
        assertEquals(1, dum.hello());
        dum.inc();
        assertEquals(2, dum.hello());
    }

    @Test
    void createRowChained(){
        /*
        * Dummy2 has a reference to Dummy 3
        * */

        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<Dummy2> dummies = new OrderedList<>();
        Dummy2 dum = dummies.createRow(new Dummy2());
        assertEquals(1, dum.hello());
        dum.inc();
        assertEquals(2, dum.hello());

        assertEquals(1, dum.getDummy3().hello());
        dum.getDummy3().inc();
        assertEquals(2, dum.getDummy3().hello());

    }
}
