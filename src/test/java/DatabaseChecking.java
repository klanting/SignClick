import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.access.MapDict;
import com.klanting.signclick.utils.statefullSQL.access.OrderedList;
import tools.DataBaseTest;

import java.io.IOException;
import java.util.HashMap;
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
public class DatabaseChecking {
    public static void main(String[] args) throws IOException {
        
        MapDict<String, Dummy6> dummies = new MapDict<>("a",String.class, Dummy6.class);
        Dummy6 dum = dummies.createRow("S", new Dummy6());

        assertTrue(dum.getDummies5().containsKey("A"));
        assertEquals(1, dum.getDummies5().get("A").getVal());

        Dummy7 d7 = dum.getDummies5().get("A");
        d7.setVal(2);


    }
}
