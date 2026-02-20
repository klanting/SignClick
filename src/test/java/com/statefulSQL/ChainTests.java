package com.statefulSQL;

import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.MapDict;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import static org.gradle.internal.impldep.org.testng.AssertJUnit.assertEquals;

@ClassFlush
class ChainA{

    public int getCounter() {
        return counter;
    }

    public int counter = 1;

    private ChainB chainB;
    ChainA(){
        chainB = new ChainB(this);
    }
    void call1(){
        counter += 1;
        chainB.call1();
        counter += 1;

    }

    void call2(){
        counter += 2;
    }


}

@ClassFlush
class ChainB{

    public int useless = -1;

    private ChainA chainA;

    ChainB(ChainA a){
        chainA = a;
    }

    void call1(){
        chainA.call2();
    }
}

public class ChainTests {
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
    void simpleChain(){
        /*
        * scenario where we have 2 objects A and B
        * A calls method of B and B calls method of A again, ensure correct value update
        * This scenario will check for temp dirty pages, when method A and then inside this method A again, need avoid fetch again
        * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, ChainA> dummies = new MapDict<>("a",String.class, ChainA.class);
        ChainA a = dummies.createRow("A", new ChainA());

        assertEquals(1, a.getCounter());
        a.call1();
        assertEquals(5, a.getCounter());
    }
}
