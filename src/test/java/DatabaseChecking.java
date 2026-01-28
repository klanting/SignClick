import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import com.klanting.signclick.utils.statefullSQL.access.OrderedList;

import java.io.IOException;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;


@ClassFlush
class Dummy{
    public int hello = 4;
    public int hello(){
        return 1;
    }

}

public class DatabaseChecking {
    public static void main(String[] args) throws IOException {
        OrderedList<Dummy> dummies = new OrderedList<>("a", Dummy.class);
        Dummy dum = dummies.createRow(new Dummy());
        dummies.createRow(new Dummy());
        assertEquals(dum.hello(), 1);
        dummies.remove(dum);


    }
}
