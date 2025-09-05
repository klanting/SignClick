package com.sign;


import com.klanting.signclick.signs.SignLookup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignLookupTests {

    @Test
    public void setSignStock(){
        SignLookup sl1 = new SignLookup("hello");

        assertFalse(sl1.equals("hello"));

        assertTrue(sl1.equals("§b[hello]"));
        assertTrue(sl1.equals("§b[signclick_hello]"));
        assertTrue(sl1.equals("§b[sign_hello]"));


    }
}
