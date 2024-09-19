package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.Economy.CountryDep;
import com.klanting.signclick.Economy.CountryManager;
import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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
        Country country = CountryManager.create("empire1", testPlayer);
        assertNotNull(country);

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

        /*
         * remove money from the country
         * */
        assertTrue(country.withdraw( 60));

        /*
         * Check money is 40
         * */
        assertEquals(40, country.getBalance());

        /*Failed withdrawal*/
        assertFalse(country.withdraw( 60));

        /*
         * Check money remains to 40
         * */
        assertEquals(40, country.getBalance());
    }

    @Test
    void getTopCountries(){
        /*
        * Get the richest countries
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        Country country2 = CountryManager.create("empire2", testPlayer2);

        assertNotNull(country);
        assertNotNull(country2);

        country.deposit(100);

        List<Country> countries = CountryManager.getTop();

        /*
        * check that country top is correctly ranked
        * */
        assertEquals(2, countries.size());
        assertEquals("empire1", countries.get(0).getName());
        assertEquals("empire2", countries.get(1).getName());
    }

    @Test
    void countryColors(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);

        assertNotNull(country);

        /*Check that TAB color is white*/
        assertEquals(Color.WHITE, country.getColor());

        /*
        * Change color
        * */
        country.setColor(Color.AQUA);

        /*Check that TAB color is AQUA*/
        assertEquals(Color.AQUA, country.getColor());
    }

    @Test
    void countrySpawn(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);

        assertNotNull(country);

        Location spawn = CountryDep.GetSpawn("empire1");

        /*
        * Check that there is no default spawn location
        * */
        assertNull(spawn);

        /*
        * set new location for spawn
        * */
        country.setSpawn(testPlayer.getLocation());

        /*
         * Check that the spawn location is correctly set
         * */

        spawn = country.getSpawn();
        assertEquals(testPlayer.getLocation(), spawn);

    }

    @Test
    void countryLeaveOwner(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        Country country = CountryManager.create("empire1", testPlayer);

        assertNotNull(country);

        country.removeOwner(testPlayer);

        /*
        * Check that the player does not have an association with the country anymore
        * */
        assertFalse(country.isOwner(testPlayer));
        assertEquals(null, CountryManager.getCountry(testPlayer));
        assertEquals(0, country.getOwners().size());

    }

    @Test
    void countryAddMember(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer3 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);

        assertNotNull(country);

        country.addMember(testPlayer2);
        country.addMember(testPlayer3);

        /*
        * Check player is correctly added to country
        * */
        assertEquals(1, country.getOwners().size());
        assertEquals(2, country.getMembers().size());
        assertEquals(testPlayer2.getUniqueId(), country.getMembers().get(0));

        assertFalse(country.isOwner(testPlayer2));

        assertEquals("empire1", CountryManager.getCountry(testPlayer2).getName());
        assertEquals("empire1", CountryManager.getCountry(testPlayer3).getName());
    }

    @Test
    void countryElementByUUID(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        assertNotNull(country);

        country = CountryManager.getCountry(testPlayer.getUniqueId());
        assertNotNull(country);
        assertEquals("empire1", country.getName());
    }

    @Test
    void countryElementOffline(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryManager.create("empire1", testPlayer);

        Country country = CountryManager.getCountry(testPlayer);
        assertEquals("empire1", country.getName());
    }

    @Test
    void countryRemoveMember(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);

        assertNotNull(country);
        country.addMember(testPlayer2);

        /*
         * Check player is correctly added to country
         * */
        assertEquals(1, country.getOwners().size());
        assertEquals(1, country.getMembers().size());
        assertEquals(testPlayer2.getUniqueId(), country.getMembers().get(0));

        assertFalse(country.isOwner(testPlayer2));
        assertEquals("empire1", CountryManager.getCountry(testPlayer2).getName());

        /*
        * Remove Player
        * */
        country.removeMember(testPlayer2);

        /*
         * Check player is correctly removed from country
         * */
        assertEquals(1, country.getOwners().size());
        assertEquals(0, country.getMembers().size());

        assertFalse(country.isOwner(testPlayer2));
        assertNull(CountryManager.getCountry(testPlayer2));
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

