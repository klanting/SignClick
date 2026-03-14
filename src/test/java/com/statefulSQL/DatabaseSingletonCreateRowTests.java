package com.statefulSQL;

import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;

import static org.junit.Assert.assertThrows;

public class DatabaseSingletonCreateRowTests {
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
    void createEntityNull(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());

        /*
        * ensure lookup table created
        * */
        assertThrows(AssertionError.class,
                () -> DatabaseSingleton.getInstance().createRow("a", "a", null, null)
        );

    }
}
