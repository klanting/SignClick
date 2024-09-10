package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.plugin.PluginManagerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.SignClick;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.MockDynmap;
import tools.MockEconomy;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;


class PartyTests {


    private ServerMock server;
    private SignClick plugin;

    private Player testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        /*Create country*/
        testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);
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

    @Test
    void partyMembership(){
        Player testPlayer2 = server.addPlayer();
        testPlayer2.addAttachment(plugin, "signclick.staff", true);
        Banking.addMember("empire1", testPlayer2);

        Banking.createParty("empire1", "TestParty", testPlayer.getUniqueId());

        /*
        * Pre party join
        * */
        assertTrue(Banking.inParty("empire1", testPlayer.getUniqueId()));
        assertFalse(Banking.inParty("empire1", testPlayer2.getUniqueId()));

        /*
        * let testPlayer2, join the party
        * */
        Party p = Banking.getParty("empire1", "TestParty");
        assertNotNull(p);

        /*Add member to party*/
        p.addMember(testPlayer2.getUniqueId());

        /*
         * Post party join
         * */
        assertTrue(Banking.inParty("empire1", testPlayer.getUniqueId()));
        assertTrue(Banking.inParty("empire1", testPlayer2.getUniqueId()));
        assertFalse(p.isOwner(testPlayer2.getUniqueId()));

        /*
        * Promote the added party member
        * */
        p.promote(testPlayer2.getUniqueId());
        assertTrue(p.isOwner(testPlayer2.getUniqueId()));

        /*
         * Demote the added party member
         * */
        p.demote(testPlayer2.getUniqueId());
        assertFalse(p.isOwner(testPlayer2.getUniqueId()));

        /*
         * Remove member
         * */
        p.removeMember(testPlayer2.getUniqueId());

        /*
        * check not in party anymore
        * */
        assertFalse(Banking.inParty("empire1", testPlayer2.getUniqueId()));
        assertFalse(p.inParty(testPlayer2.getUniqueId()));

    }

    @Test
    void removeParty(){
        Banking.createParty("empire1", "TestParty", testPlayer.getUniqueId());

        assertEquals(70, Banking.getStability("empire1"));


        Party p = Banking.getParty("empire1", "TestParty");
        assertNotNull(p);

        Banking.removeParty(p);

        assertEquals(60, Banking.getStability("empire1"));
    }
}