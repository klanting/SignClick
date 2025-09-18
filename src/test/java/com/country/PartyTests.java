package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.logicLayer.countryLogic.Country;

import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.countryLogic.parties.Election;
import com.klanting.signclick.logicLayer.countryLogic.parties.Party;
import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;


class PartyTests {


    private ServerMock server;
    private SignClick plugin;

    private Player testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

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
        assertEquals("Government",country.getParty(testPlayer.getUniqueId()).name);

        PlayerMock testPlayer2 = server.addPlayer();

        country.createParty("TestParty", testPlayer2.getUniqueId());

        Party p = country.getParty(testPlayer2.getUniqueId());
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
        assertNotNull(p);
        assertEquals("Government", p.name);

        PlayerMock testPlayer2 = server.addPlayer();

        country.createParty("TestParty", testPlayer2.getUniqueId());

        /*
         * the ruling party is 'Government'
         * */

        p = country.getRuling();
        assertNotNull(p);
        assertEquals("Government", p.name);
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

    @Test
    void electionParty(){
        Country country = CountryManager.getCountry("empire1");
        country.createParty("TestParty", testPlayer.getUniqueId());

        /*
        * add player to country
        * */
        PlayerMock testPlayer2 = server.addPlayer();
        country.addMember(testPlayer2.getUniqueId());

        /*
        * make second party
        * */
        country.createParty("TestParty2", testPlayer2.getUniqueId());

        long system_end = server.getCurrentTick() + 60*60*20L;
        country.setCountryElection(new Election(country.getName(), system_end));

        Election election = country.getCountryElection();
        assertNotNull(election);

        assertEquals("Government", country.getRuling().name);

        /*
        * vote on the party
        * */
        election.vote("TestParty2", testPlayer.getUniqueId());
        assertTrue(election.alreadyVoted.contains(testPlayer.getUniqueId()));

        /*
         * Restart Server, check persistence
         * */
        plugin = TestTools.reboot(server);

        country = CountryManager.getCountry("empire1");
        election = country.getCountryElection();
        assertNotNull(election);

        server.getScheduler().performTicks(60*60*20L+1);

        /*
        * check change of power happened
        * */
        assertEquals("TestParty2", country.getRuling().name);

    }
}