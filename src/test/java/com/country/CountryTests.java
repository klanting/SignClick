package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.Economy.CountryDep;
import com.klanting.signclick.Economy.CountryManager;
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
        CountryDep.clear();
        CountryManager.clear();
    }

    @Test
    void countryCreate(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*Verify that no countries exist*/
        assertEquals(0, CountryManager.countryCount());

        /*Create a country*/
        Country country = CountryManager.create("empire1", testPlayer);

        /*Verify that 1 country exist*/
        assertNotNull(country);
        assertEquals("empire1", country.getName());
        assertEquals(1, CountryManager.countryCount());

        /*Check that the player is part of the country*/
        country = CountryManager.getCountry(testPlayer);
        assertEquals("empire1", country.getName());

    }

    @Test
    void countryDelete(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*Create a country*/
        CountryManager.create("empire1", testPlayer);

        /*Verify that 1 country exist*/
        assertEquals(1, CountryManager.countryCount());

        /*Check that the player is part of the country*/
        Country country = CountryManager.getCountry(testPlayer);
        assertEquals("empire1", country.getName());

        /*Remove the country*/
        assertTrue(CountryManager.delete("empire1", testPlayer));

        /*Verify that no countries exist*/
        assertEquals(0, CountryManager.countryCount());

        /*Check that the player is not part of a country anymore*/
        country = CountryManager.getCountry(testPlayer);
        assertNull(country);

    }

    @Test
    void countryDeleteFailed(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*Verify that no country exists*/
        assertEquals(0, CountryManager.countryCount());

        /*Remove the country*/
        CountryManager.delete("empire1", testPlayer);

        testPlayer.assertSaid("this country does not exists");
        testPlayer.assertNoMoreSaid();

    }

    @Test
    void countryFailedCreate(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        /*Verify that no countries exist*/
        assertEquals(0, CountryManager.countryCount());

        /*Create a country*/
        CountryManager.create("empire1", testPlayer);

        /*Verify that 1 country exist*/
        assertEquals(1, CountryManager.countryCount());

        assertNull(CountryManager.create("empire1", testPlayer2));

        /*Verify that 1 country exist*/
        assertEquals(1, CountryManager.countryCount());

        Country country = CountryManager.getCountry(testPlayer2);

        /*Check that the player is part of a country*/
        country = CountryManager.getCountry(testPlayer);
        assertEquals("empire1", country.getName());

        Country country2 = CountryManager.getCountry(testPlayer2);

        /*Check that the player is not part of a country*/
        country2 = CountryManager.getCountry(testPlayer2);
        assertNull(country2);
    }

    @Test
    void countryOwner(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);

        /*
        * testPlayer is owner, verify this
        * */
        assertNotNull(country);
        assertTrue(country.isOwner(testPlayer));
        assertEquals(1, country.getOwners().size());
        assertEquals(testPlayer.getUniqueId(), country.getOwners().get(0));

        /*
         * testPlayer2 is not owner, verify this
         * */
        assertFalse(country.isOwner(testPlayer2));
    }

    @Test
    void CountryGetBalance(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        assertNotNull(country);

        /*Check that a country has at least 0 dollars*/
        assertTrue(country.has(0));

        /*Check that a country has at most 0 dollars*/
        assertFalse(country.has(1));

        /*
        * Check money is zero
        * */
        assertEquals(0, country.getBalance());

        /*
        * add money to the country
        * */
        country.deposit(100);

        /*
         * Check money is 100
         * */
        assertEquals(100, country.getBalance());
    }

    @Test
    void CountryDepositWithdraw(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        /*
         * Check money is zero
         * */
        assertEquals(0, CountryDep.bal("empire1"));

        /*
         * add money to the country
         * */
        CountryDep.deposit("empire1", 100);

        /*
         * Check money is 100
         * */
        assertEquals(100, CountryDep.bal("empire1"));

        /*
         * remove money from the country
         * */
        assertTrue(CountryDep.withdraw("empire1", 60));

        /*
         * Check money is 40
         * */
        assertEquals(40, CountryDep.bal("empire1"));

        /*Failed withdrawal*/
        assertFalse(CountryDep.withdraw("empire1", 60));

        /*
         * Check money is -20
         * */
        assertEquals(40, CountryDep.bal("empire1"));
    }

    @Test
    void getTopCountries(){
        /*
        * Get the richest countries
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);
        CountryDep.create("empire2", testPlayer2);

        CountryDep.deposit("empire1", 100);

        List<String> countries = CountryDep.getTop();

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
        CountryDep.create("empire1", testPlayer);

        /*Check that TAB color is white*/
        assertEquals(ChatColor.valueOf("WHITE"), CountryDep.GetColor("empire1"));

        /*
        * Change color
        * */
        CountryDep.SetColor("empire1", "AQUA");

        /*Check that TAB color is AQUA*/
        assertEquals(ChatColor.valueOf("AQUA"), CountryDep.GetColor("empire1"));
    }

    @Test
    void countrySpawn(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);
        Location spawn = CountryDep.GetSpawn("empire1");

        /*
        * Check that there is no default spawn location
        * */
        assertNull(spawn);

        /*
        * set new location for spawn
        * */
        CountryDep.SetSpawn("empire1", testPlayer.getLocation());

        /*
         * Check that the spawn location is correctly set
         * */

        spawn = CountryDep.GetSpawn("empire1");
        assertEquals(testPlayer.getLocation(), spawn);

    }

    @Test
    void countryLeaveOwner(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        CountryDep.create("empire1", testPlayer);

        CountryDep.removeOwner("empire1", testPlayer);

        /*
        * Check that the player does not have an association with the country anymore
        * */
        assertFalse(CountryDep.isOwner("empire1", testPlayer));
        assertEquals("none", CountryDep.Element(testPlayer));
        assertEquals(0, CountryDep.GetOwners("empire1").size());

    }

    @Test
    void countryAddMember(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer3 = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        CountryDep.addMember("empire1", testPlayer2);
        CountryDep.addMember("empire1", testPlayer3);

        /*
        * Check player is correctly added to country
        * */
        assertEquals(1, CountryDep.GetOwners("empire1").size());
        assertEquals(2, CountryDep.getMembers("empire1").size());
        assertEquals(testPlayer2.getUniqueId(), CountryDep.getMembers("empire1").get(0));

        assertFalse(CountryDep.isOwner("empire1", testPlayer2));
        assertEquals("empire1", CountryDep.Element(testPlayer2));
        assertEquals("empire1", CountryDep.Element(testPlayer3));
    }

    @Test
    void countryElementByUUID(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        String countryString = CountryDep.ElementUUID(testPlayer.getUniqueId());
        assertEquals("empire1", countryString);
    }

    @Test
    void countryElementOffline(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        String countryString = CountryDep.Element(testPlayer);
        assertEquals("empire1", countryString);
    }

    @Test
    void countryRemoveMember(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        CountryDep.addMember("empire1", testPlayer2);

        /*
         * Check player is correctly added to country
         * */
        assertEquals(1, CountryDep.GetOwners("empire1").size());
        assertEquals(1, CountryDep.getMembers("empire1").size());
        assertEquals(testPlayer2.getUniqueId(), CountryDep.getMembers("empire1").get(0));

        assertFalse(CountryDep.isOwner("empire1", testPlayer2));
        assertEquals("empire1", CountryDep.Element(testPlayer2));

        /*
        * Remove Player
        * */
        CountryDep.removeMember("empire1", testPlayer2);

        /*
         * Check player is correctly removed from country
         * */
        assertEquals(1, CountryDep.GetOwners("empire1").size());
        assertEquals(0, CountryDep.getMembers("empire1").size());

        assertFalse(CountryDep.isOwner("empire1", testPlayer2));
        assertEquals("none", CountryDep.Element(testPlayer2));
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
        CountryDep.create("empire1", testPlayer);

        /*
        * Save Data
        * */
        CountryDep.SaveData();
        CountryDep.clear();
        CountryDep.RestoreData();

        /*
        * Check that country is loaded again
        * */
        String country = CountryDep.Element(testPlayer);
        assertEquals("empire1", country);
    }

    @Test
    void countryTax(){
        /*
        * Check the country tax data
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        assertEquals(0, CountryDep.getPCT("empire1"));

        CountryDep.setPCT("empire1", 20);
        assertEquals(20, CountryDep.getPCT("empire1"));
    }

    @Test
    void countryAddRemoveOwner(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        /*
        * Add owner
        * */
        CountryDep.addOwner("empire1", testPlayer2);


        /*
        * Check that both are owners
        * */
        assertTrue(CountryDep.isOwner("empire1", testPlayer));
        assertTrue(CountryDep.isOwner("empire1", testPlayer2));
        assertEquals(2, CountryDep.GetOwners("empire1").size());

        /*
         * Remove owner
         * */
        CountryDep.removeOwner("empire1", testPlayer2);

        assertTrue(CountryDep.isOwner("empire1", testPlayer));
        assertFalse(CountryDep.isOwner("empire1", testPlayer2));
        assertEquals("none", CountryDep.Element(testPlayer2));
        assertEquals(1, CountryDep.GetOwners("empire1").size());
    }

    @Test
    void countryStability(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        double stability = CountryDep.getStability("empire1");

        /*
        * assert base stability
        * */
        assertEquals(70.0, stability);
    }

    @Test
    void capitalChangeStability(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryDep.create("empire1", testPlayer);

        assertEquals(70.0, CountryDep.getStability("empire1"));

        /*
        * Over 40 mil -> +5 stability
        * */
        CountryDep.deposit("empire1", 40000001);

        assertEquals(75.0, CountryDep.getStability("empire1"));

        /*
        * Over 60 mil -> +7 stability
        * */
        CountryDep.deposit("empire1", 20000000);
        assertEquals(77.0, CountryDep.getStability("empire1"));

        /*
         * Over 40 mil -> +5 stability (reduction)
         * */
        CountryDep.withdraw("empire1", 20000000);
        assertEquals(75.0, CountryDep.getStability("empire1"));
    }

}

