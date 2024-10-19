package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.economy.Country;

import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        CountryManager.create("empire1", testPlayer);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
    }

    @Test
    void createParty(){
        /*
        * Create a party
        * */
        Country country = CountryManager.getCountry("empire1");
        assertNull(country.getParty(testPlayer.getUniqueId()));

        country.createParty("TestParty", testPlayer.getUniqueId());

        Party p = country.getParty(testPlayer.getUniqueId());
        /*
        * check party exists
        * */
        assertNotNull(p);
        assertEquals("TestParty", p.name);

        /*
         * check party exists
         * */
        p = country.getParty("TestParty");
        assertNotNull(p);
        assertEquals("TestParty", p.name);

        assertTrue(country.hasPartyName("TestParty"));

    }

    @Test
    void getRulingParty(){

        /*
        * Test retrieving ruling party information
        * */

        /*
        * no ruling party
        * */
        Country country = CountryManager.getCountry("empire1");
        Party p = country.getRuling();
        assertNull(p);

        country.createParty("TestParty", testPlayer.getUniqueId());

        /*
         * the ruling party is 'TestParty'
         * */

        p = country.getRuling();
        assertNotNull(p);
        assertEquals("TestParty", p.name);
    }

    @Test
    void partyMembership(){
        Player testPlayer2 = server.addPlayer();
        testPlayer2.addAttachment(plugin, "signclick.staff", true);

        Country country = CountryManager.getCountry("empire1");
        country.addMember(testPlayer2);

        country.createParty("TestParty", testPlayer.getUniqueId());

        /*
        * Pre party join
        * */
        assertTrue(country.inParty(testPlayer.getUniqueId()));
        assertFalse(country.inParty(testPlayer2.getUniqueId()));

        /*
        * let testPlayer2, join the party
        * */
        Party p = country.getParty("TestParty");
        assertNotNull(p);

        /*Add member to party*/
        p.addMember(testPlayer2.getUniqueId());

        /*
         * Post party join
         * */
        assertTrue(country.inParty(testPlayer.getUniqueId()));
        assertTrue(country.inParty(testPlayer2.getUniqueId()));
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
        assertFalse(country.inParty(testPlayer2.getUniqueId()));
        assertFalse(p.inParty(testPlayer2.getUniqueId()));

    }

    @Test
    void removeParty(){
        Country country = CountryManager.getCountry("empire1");
        country.createParty("TestParty", testPlayer.getUniqueId());

        assertEquals(70, country.getStability());


        Party p = country.getParty("TestParty");
        assertNotNull(p);

        country.removeParty(p);

        assertEquals(60, country.getStability());
    }
}