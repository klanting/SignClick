package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PolicyTests {

    private ServerMock server;
    private SignClick plugin;
    private Player testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = MockBukkit.load(SignClick.class);

        /*Create country*/
        testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Banking.clear();
    }

    @Test
    void setPolicy(){
        /*
        * Test policy changes
        * */

        assertEquals(70.0, Banking.getStability("empire1"));

        Banking.setPoliciesReal("empire1", 0, 2, 3);
        assertEquals(69.0, Banking.getStability("empire1"));

        Banking.setPoliciesReal("empire1", 0, 3, 1);
        assertEquals(71.0, Banking.getStability("empire1"));

    }
}