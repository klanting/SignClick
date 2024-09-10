package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PartyTests {


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
    void createParty(){
        /*
        * Create a party
        * */
        assertNull(Banking.getParty("empire1", testPlayer.getUniqueId()));

        Banking.createParty("empire1", "TestParty", testPlayer.getUniqueId());

        Party p = Banking.getParty("empire1", testPlayer.getUniqueId());
        /*
        * check party exists
        * */
        assertNotNull(p);
        assertEquals("TestParty", p.name);

        /*
         * check party exists
         * */
        p = Banking.getParty("empire1", "TestParty");
        assertNotNull(p);
        assertEquals("TestParty", p.name);

        assertTrue(Banking.hasPartyName("empire1", "TestParty"));

    }

    @Test
    void getRulingParty(){

        /*
        * Test retrieving ruling party information
        * */

        /*
        * no ruling party
        * */
        Party p = Banking.getRuling("empire1");
        assertNull(p);

        Banking.createParty("empire1", "TestParty", testPlayer.getUniqueId());

        /*
         * the ruling party is 'TestParty'
         * */

        p = Banking.getRuling("empire1");
        assertNotNull(p);
        assertEquals("TestParty", p.name);

    }
}