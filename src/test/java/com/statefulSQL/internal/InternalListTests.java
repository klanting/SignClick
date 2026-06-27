package com.statefulSQL.internal;

import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.MapDict;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import tools.DataBaseTest;

import java.util.ArrayList;
import java.util.List;

import static org.gradle.internal.impldep.org.junit.Assert.*;

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
        assertFalse(dum2.getDummies5().isEmpty());

        /*
        * remove from dum
        * */
        dum.getDummies5().remove(0);

        assertTrue(dum.getDummies5().isEmpty());
        assertEquals(0, dum.getDummies5().size());
        assertFalse(dum2.getDummies5().isEmpty());
        assertEquals(2, dum2.getDummies5().size());

        dum2.getDummies5().clear();
        assertTrue(dum2.getDummies5().isEmpty());


    }

    @ParameterizedTest
    @CsvSource({
            "false, -1",
            "true, 0",

    })
    void addAllMustAddAllItems(boolean useIndex, int index){
        /*
        * make sure the addAll has the correct behaviour
        * have different index configurations
        * */

        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, InternalListDummy4> dummies = new MapDict<>("a",String.class, InternalListDummy4.class);
        InternalListDummy4 dum = dummies.createRow("S", new InternalListDummy4());
        InternalListDummy4 dum2 = dummies.createRow("S", new InternalListDummy4());
        InternalListDummy4 dum3 = dummies.createRow("S", new InternalListDummy4());

        List<InternalListDummy5> otherList = new ArrayList<>();
        otherList.add(dum.getDummies5().get(0));
        otherList.add(dum3.getDummies5().get(0));

        /*
        * apply the add all method
        * */
        if (!useIndex){
            dum2.getDummies5().addAll(otherList);
        }else{
            dum2.getDummies5().addAll(index, otherList);
        }


        assertEquals(1, dum.getDummies5().size());
        assertEquals(3, dum2.getDummies5().size());

        if (useIndex){
            assertEquals(dum.getDummies5().get(0), dum2.getDummies5().get(index));
            assertEquals(dum3.getDummies5().get(0), dum2.getDummies5().get(index+1));
        }else{
            assertEquals(dum.getDummies5().get(0), dum2.getDummies5().get(1));
            assertEquals(dum3.getDummies5().get(0), dum2.getDummies5().get(2));
        }
        assertTrue(dum2.getDummies5().containsAll(otherList));

    }

    @ParameterizedTest
    @CsvSource({
            "-1",
            "2",
    })
    void addAllInvalidIndex(int invalidIndex){
        /*
        * throw IndexOutOfBoundsException when index -1
        * */
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, InternalListDummy4> dummies = new MapDict<>("a",String.class, InternalListDummy4.class);
        InternalListDummy4 dum = dummies.createRow("S", new InternalListDummy4());
        InternalListDummy4 dum2 = dummies.createRow("S", new InternalListDummy4());
        InternalListDummy4 dum3 = dummies.createRow("S", new InternalListDummy4());

        List<InternalListDummy5> otherList = new ArrayList<>();
        otherList.add(dum.getDummies5().get(0));
        otherList.add(dum3.getDummies5().get(0));

        assertThrows(IndexOutOfBoundsException.class, () -> dum2.getDummies5().addAll(invalidIndex, otherList));
    }

    @Test
    void lastIndexOf(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        MapDict<String, InternalListDummy4> dummies = new MapDict<>("a",String.class, InternalListDummy4.class);
        InternalListDummy4 dum = dummies.createRow("S", new InternalListDummy4());
        InternalListDummy4 dum2 = dummies.createRow("S", new InternalListDummy4());

        dum2.getDummies5().add(dum.getDummies5().get(0));
        dum2.getDummies5().add(dum.getDummies5().get(0));

        assertEquals(3, dum2.getDummies5().size());

        assertEquals(2, dum2.getDummies5().lastIndexOf(dum.getDummies5().get(0)));


    }

}
