package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.TestTools;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CountryTests {


    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp()
    {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);
    }

    @AfterEach
    public void tearDown()
    {

        MockBukkit.unmock();
        Country.clear();
    }

    @Test
    void countryCreate(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*Verify that no countries exist*/
        assertEquals(0, Country.countryCount());

        /*Create a country*/
        Country.create("empire1", testPlayer);

        /*Verify that 1 country exist*/
        assertEquals(1, Country.countryCount());

        /*Check that the player is part of the country*/
        String country = Country.Element(testPlayer);
        assertEquals("empire1", country);

    }

    @Test
    void countryDelete(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*Create a country*/
        Country.create("empire1", testPlayer);

        /*Verify that 1 country exist*/
        assertEquals(1, Country.countryCount());

        /*Check that the player is part of the country*/
        String country = Country.Element(testPlayer);
        assertEquals("empire1", country);

        /*Remove the country*/
        Country.delete("empire1", testPlayer);

        /*Verify that no countries exist*/
        assertEquals(0, Country.countryCount());

        /*Check that the player is not part of a country anymore*/
        country = Country.Element(testPlayer);
        assertEquals("none", country);

    }

    @Test
    void countryDeleteFailed(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*Verify that no country exists*/
        assertEquals(0, Country.countryCount());

        /*Remove the country*/
        Country.delete("empire1", testPlayer);

        testPlayer.assertSaid("this bank does not exists");
        testPlayer.assertNoMoreSaid();

    }

    @Test
    void countryFailedCreate(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        /*Verify that no countries exist*/
        assertEquals(0, Country.countryCount());

        /*Create a country*/
        Country.create("empire1", testPlayer);

        /*Verify that 1 country exist*/
        assertEquals(1, Country.countryCount());

        Country.create("empire1", testPlayer2);

        /*Verify that 1 country exist*/
        assertEquals(1, Country.countryCount());

        String country = Country.Element(testPlayer2);

        /*Check that the player is part of a country*/
        country = Country.Element(testPlayer);
        assertEquals("empire1", country);

        String country2 = Country.Element(testPlayer2);

        /*Check that the player is not part of a country*/
        country2 = Country.Element(testPlayer2);
        assertEquals("none", country2);
    }

    @Test
    void countryOwner(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        /*
        * testPlayer is owner, verify this
        * */
        assertTrue(Country.isOwner("empire1", testPlayer));
        assertEquals(1, Country.GetOwners("empire1").size());
        assertEquals(testPlayer.getUniqueId(), Country.GetOwners("empire1").get(0));

        /*
         * testPlayer2 is not owner, verify this
         * */
        assertFalse(Country.isOwner("empire1", testPlayer2));
    }

    @Test
    void CountryGetBalance(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        /*Check that a country has at least 0 dollars*/
        assertTrue(Country.has("empire1", 0));

        /*
        * invalid country does not have enough
        * */
        assertFalse(Country.has("empire2", 1));

        /*Check that a country has at most 0 dollars*/
        assertFalse(Country.has("empire1", 1));

        /*
        * Check money is zero
        * */
        assertEquals(0, Country.bal("empire1"));

        /*
        * add money to the country
        * */
        Country.deposit("empire1", 100);

        /*
         * Check money is 100
         * */
        assertEquals(100, Country.bal("empire1"));
    }

    @Test
    void CountryDepositWithdraw(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        /*
         * Check money is zero
         * */
        assertEquals(0, Country.bal("empire1"));

        /*
         * add money to the country
         * */
        Country.deposit("empire1", 100);

        /*
         * Check money is 100
         * */
        assertEquals(100, Country.bal("empire1"));

        /*
         * remove money from the country
         * */
        assertTrue(Country.withdraw("empire1", 60));

        /*
         * Check money is 40
         * */
        assertEquals(40, Country.bal("empire1"));

        /*Failed withdrawal*/
        assertFalse(Country.withdraw("empire1", 60));

        /*
         * Check money is -20
         * */
        assertEquals(40, Country.bal("empire1"));
    }

    @Test
    void getTopCountries(){
        /*
        * Get the richest countries
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);
        Country.create("empire2", testPlayer2);

        Country.deposit("empire1", 100);

        List<String> countries = Country.getTop();

        /*
        * check that country top is correctly ranked
        * */
        assertEquals(2, countries.size());
        assertEquals("empire1", countries.get(0));
        assertEquals("empire2", countries.get(1));
    }

    @Test
    void countryColors(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        /*Check that TAB color is white*/
        assertEquals(ChatColor.valueOf("WHITE"), Country.GetColor("empire1"));

        /*
        * Change color
        * */
        Country.SetColor("empire1", "AQUA");

        /*Check that TAB color is AQUA*/
        assertEquals(ChatColor.valueOf("AQUA"), Country.GetColor("empire1"));
    }

    @Test
    void countrySpawn(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);
        Location spawn = Country.GetSpawn("empire1");

        /*
        * Check that there is no default spawn location
        * */
        assertNull(spawn);

        /*
        * set new location for spawn
        * */
        Country.SetSpawn("empire1", testPlayer.getLocation());

        /*
         * Check that the spawn location is correctly set
         * */

        spawn = Country.GetSpawn("empire1");
        assertEquals(testPlayer.getLocation(), spawn);

    }

    @Test
    void countryLeaveOwner(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        Country.create("empire1", testPlayer);

        Country.removeOwner("empire1", testPlayer);

        /*
        * Check that the player does not have an association with the country anymore
        * */
        assertFalse(Country.isOwner("empire1", testPlayer));
        assertEquals("none", Country.Element(testPlayer));
        assertEquals(0, Country.GetOwners("empire1").size());

    }

    @Test
    void countryAddMember(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer3 = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        Country.addMember("empire1", testPlayer2);
        Country.addMember("empire1", testPlayer3);

        /*
        * Check player is correctly added to country
        * */
        assertEquals(1, Country.GetOwners("empire1").size());
        assertEquals(2, Country.getMembers("empire1").size());
        assertEquals(testPlayer2.getUniqueId(), Country.getMembers("empire1").get(0));

        assertFalse(Country.isOwner("empire1", testPlayer2));
        assertEquals("empire1", Country.Element(testPlayer2));
        assertEquals("empire1", Country.Element(testPlayer3));
    }

    @Test
    void countryElementByUUID(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        String countryString = Country.ElementUUID(testPlayer.getUniqueId());
        assertEquals("empire1", countryString);
    }

    @Test
    void countryElementOffline(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        String countryString = Country.Element(testPlayer);
        assertEquals("empire1", countryString);
    }

    @Test
    void countryRemoveMember(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        Country.addMember("empire1", testPlayer2);

        /*
         * Check player is correctly added to country
         * */
        assertEquals(1, Country.GetOwners("empire1").size());
        assertEquals(1, Country.getMembers("empire1").size());
        assertEquals(testPlayer2.getUniqueId(), Country.getMembers("empire1").get(0));

        assertFalse(Country.isOwner("empire1", testPlayer2));
        assertEquals("empire1", Country.Element(testPlayer2));

        /*
        * Remove Player
        * */
        Country.removeMember("empire1", testPlayer2);

        /*
         * Check player is correctly removed from country
         * */
        assertEquals(1, Country.GetOwners("empire1").size());
        assertEquals(0, Country.getMembers("empire1").size());

        assertFalse(Country.isOwner("empire1", testPlayer2));
        assertEquals("none", Country.Element(testPlayer2));
    }

    @Test
    void countrySaveLoad(){
        /*
        * Check that the country saves and loads correctly
        * */

        /*
        * Create Country
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        /*
        * Save Data
        * */
        Country.SaveData();
        Country.clear();
        Country.RestoreData();

        /*
        * Check that country is loaded again
        * */
        String country = Country.Element(testPlayer);
        assertEquals("empire1", country);
    }

    @Test
    void countryTax(){
        /*
        * Check the country tax data
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        assertEquals(0, Country.getPCT("empire1"));

        Country.setPCT("empire1", 20);
        assertEquals(20, Country.getPCT("empire1"));
    }

    @Test
    void countryAddRemoveOwner(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        /*
        * Add owner
        * */
        Country.addOwner("empire1", testPlayer2);


        /*
        * Check that both are owners
        * */
        assertTrue(Country.isOwner("empire1", testPlayer));
        assertTrue(Country.isOwner("empire1", testPlayer2));
        assertEquals(2, Country.GetOwners("empire1").size());

        /*
         * Remove owner
         * */
        Country.removeOwner("empire1", testPlayer2);

        assertTrue(Country.isOwner("empire1", testPlayer));
        assertFalse(Country.isOwner("empire1", testPlayer2));
        assertEquals("none", Country.Element(testPlayer2));
        assertEquals(1, Country.GetOwners("empire1").size());
    }

    @Test
    void countryStability(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        double stability = Country.getStability("empire1");

        /*
        * assert base stability
        * */
        assertEquals(70.0, stability);
    }

    @Test
    void capitalChangeStability(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country.create("empire1", testPlayer);

        assertEquals(70.0, Country.getStability("empire1"));

        /*
        * Over 40 mil -> +5 stability
        * */
        Country.deposit("empire1", 40000001);

        assertEquals(75.0, Country.getStability("empire1"));

        /*
        * Over 60 mil -> +7 stability
        * */
        Country.deposit("empire1", 20000000);
        assertEquals(77.0, Country.getStability("empire1"));

        /*
         * Over 40 mil -> +5 stability (reduction)
         * */
        Country.withdraw("empire1", 20000000);
        assertEquals(75.0, Country.getStability("empire1"));
    }

}

