import com.klanting.signclick.utils.autoFlush.ClassFlush;
import com.klanting.signclick.utils.autoFlush.DatabaseSingleton;
import com.klanting.signclick.utils.autoFlush.access.OrderedList;

import java.io.IOException;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;


@ClassFlush
class Dummy{
    public int hello(){
        return 1;
    }

}

public class DatabaseChecking {
    public static void main(String[] args) throws IOException {
        DatabaseSingleton.getInstance().checkTables();
        OrderedList<Dummy> dummies = new OrderedList<>("a", Dummy.class);
        Dummy dum = dummies.createRow(new Dummy());
        assertEquals(dum.hello(), 1);


    }
}
