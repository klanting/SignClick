package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CountryTests {


    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp()
    {

        server = MockBukkit.mock();

        plugin = MockBukkit.load(SignClick.class);
    }

    @AfterEach
    public void tearDown()
    {

        MockBukkit.unmock();
        Banking.clear();
    }

    @Test
    void countryCreate(){
        Player testPlayer = server.addPlayer();

        /*Verify that no countries exist*/
        assertEquals(0, Banking.countryCount());

        /*Create a com.country*/
        Banking.create("empire1", testPlayer);

        /*Verify that 1 com.country exist*/
        assertEquals(1, Banking.countryCount());

        /*Check that the player is part of the com.country*/
        String country = Banking.Element(testPlayer);
        assertEquals("empire1", country);

    }

    @Test
    void countryDelete(){
        Player testPlayer = server.addPlayer();

        /*Create a com.country*/
        Banking.create("empire1", testPlayer);

        /*Verify that 1 com.country exist*/
        assertEquals(1, Banking.countryCount());

        /*Check that the player is part of the com.country*/
        String country = Banking.Element(testPlayer);
        assertEquals("empire1", country);

        /*Remove the com.country*/
        Banking.delete("empire1", testPlayer);

        /*Verify that no countries exist*/
        assertEquals(0, Banking.countryCount());

        /*Check that the player is not part of a com.country anymore*/
        country = Banking.Element(testPlayer);
        assertEquals("none", country);

    }

    @Test
    void countryFailedCreate(){
        Player testPlayer = server.addPlayer();
        Player testPlayer2 = server.addPlayer();

        /*Verify that no countries exist*/
        assertEquals(0, Banking.countryCount());

        /*Create a com.country*/
        Banking.create("empire1", testPlayer);

        /*Verify that 1 com.country exist*/
        assertEquals(1, Banking.countryCount());

        Banking.create("empire1", testPlayer2);

        /*Verify that 1 com.country exist*/
        assertEquals(1, Banking.countryCount());

        String country = Banking.Element(testPlayer2);

        /*Check that the player is part of a com.country*/
        country = Banking.Element(testPlayer);
        assertEquals("empire1", country);

        String country2 = Banking.Element(testPlayer2);

        /*Check that the player is not part of a com.country*/
        country2 = Banking.Element(testPlayer2);
        assertEquals("none", country2);
    }

    @Test
    void countryOwner(){
        Player testPlayer = server.addPlayer();
        Player testPlayer2 = server.addPlayer();
        Banking.create("empire1", testPlayer);

        /*
        * testPlayer is owner, verify this
        * */
        assertTrue(Banking.isOwner("empire1", testPlayer));
        assertEquals(1, Banking.GetOwners("empire1").size());
        assertEquals(testPlayer.getUniqueId(), Banking.GetOwners("empire1").get(0));

        /*
         * testPlayer2 is not owner, verify this
         * */
        assertFalse(Banking.isOwner("empire1", testPlayer2));
    }

    @Test
    void CountryGetBalance(){
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);

        /*Check that a com.country has at least 0 dollars*/
        assertTrue(Banking.has("empire1", 0));

        /*Check that a com.country has at most 0 dollars*/
        assertFalse(Banking.has("empire1", 1));

        /*
        * Check money is zero
        * */
        assertEquals(0, Banking.bal("empire1"));

        /*
        * add money to the com.country
        * */
        Banking.deposit("empire1", 100);

        /*
         * Check money is 100
         * */
        assertEquals(100, Banking.bal("empire1"));
    }

    @Test
    void CountryDepositWithdraw(){
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);

        /*
         * Check money is zero
         * */
        assertEquals(0, Banking.bal("empire1"));

        /*
         * add money to the com.country
         * */
        Banking.deposit("empire1", 100);

        /*
         * Check money is 100
         * */
        assertEquals(100, Banking.bal("empire1"));

        /*
         * remove money from the com.country
         * */
        assertTrue(Banking.withdraw("empire1", 60));

        /*
         * Check money is 40
         * */
        assertEquals(40, Banking.bal("empire1"));

        /*Failed withdrawal*/
        assertFalse(Banking.withdraw("empire1", 60));

        /*
         * Check money is -20
         * */
        assertEquals(40, Banking.bal("empire1"));
    }

    @Test
    void getTopCountries(){
        /*
        * Get the richest countries
        * */

        Player testPlayer = server.addPlayer();
        Player testPlayer2 = server.addPlayer();
        Banking.create("empire1", testPlayer);
        Banking.create("empire2", testPlayer2);

        Banking.deposit("empire1", 100);

        List<String> countries = Banking.getTop();

        /*
        * check that com.country top is correctly ranked
        * */
        assertEquals(2, countries.size());
        assertEquals("empire1", countries.get(0));
        assertEquals("empire2", countries.get(1));
    }

    @Test
    void countryColors(){
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);

        /*Check that TAB color is white*/
        assertEquals(ChatColor.valueOf("WHITE"), Banking.GetColor("empire1"));

        /*
        * Change color
        * */
        Banking.SetColor("empire1", "AQUA");

        /*Check that TAB color is AQUA*/
        assertEquals(ChatColor.valueOf("AQUA"), Banking.GetColor("empire1"));
    }

    @Test
    void countrySpawn(){
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);
        Location spawn = Banking.GetSpawn("empire1");

        /*
        * Check that there is no default spawn location
        * */
        assertNull(spawn);

        /*
        * set new location for spawn
        * */
        Banking.SetSpawn("empire1", testPlayer.getLocation());

        /*
         * Check that the spawn location is correctly set
         * */

        spawn = Banking.GetSpawn("empire1");
        assertEquals(testPlayer.getLocation(), spawn);

    }

    @Test
    void countryLeaveOwner(){
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);

        Banking.removeOwner("empire1", testPlayer);

        /*
        * Check that the player does not have an association with the com.country anymore
        * */
        assertFalse(Banking.isOwner("empire1", testPlayer));
        assertEquals("none", Banking.Element(testPlayer));
        assertEquals(0, Banking.GetOwners("empire1").size());

    }

    @Test
    void countryAddMember(){
        Player testPlayer = server.addPlayer();
        Player testPlayer2 = server.addPlayer();
        Banking.create("empire1", testPlayer);

        Banking.addMember("empire1", testPlayer2);

        /*
        * Check player is correctly added to com.country
        * */
        assertEquals(1, Banking.GetOwners("empire1").size());
        assertEquals(1, Banking.getMembers("empire1").size());
        assertEquals(testPlayer2.getUniqueId(), Banking.getMembers("empire1").get(0));

        assertFalse(Banking.isOwner("empire1", testPlayer2));
        assertEquals("empire1", Banking.Element(testPlayer2));
    }

    @Test
    void countryRemoveMember(){
        Player testPlayer = server.addPlayer();
        Player testPlayer2 = server.addPlayer();
        Banking.create("empire1", testPlayer);

        Banking.addMember("empire1", testPlayer2);

        /*
         * Check player is correctly added to com.country
         * */
        assertEquals(1, Banking.GetOwners("empire1").size());
        assertEquals(1, Banking.getMembers("empire1").size());
        assertEquals(testPlayer2.getUniqueId(), Banking.getMembers("empire1").get(0));

        assertFalse(Banking.isOwner("empire1", testPlayer2));
        assertEquals("empire1", Banking.Element(testPlayer2));

        /*
        * Remove Player
        * */
        Banking.removeMember("empire1", testPlayer2);

        /*
         * Check player is correctly removed from com.country
         * */
        assertEquals(1, Banking.GetOwners("empire1").size());
        assertEquals(0, Banking.getMembers("empire1").size());

        assertFalse(Banking.isOwner("empire1", testPlayer2));
        assertEquals("none", Banking.Element(testPlayer2));
    }

    @Test
    void countrySaveLoad(){
        /*
        * Check that the com.country saves and loads correctly
        * */

        /*
        * Create Country
        * */
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);

        /*
        * Save Data
        * */
        Banking.SaveData();
        Banking.clear();
        Banking.RestoreData();

        /*
        * Check that country is loaded again
        * */
        String country = Banking.Element(testPlayer);
        assertEquals("empire1", country);
    }

    @Test
    void countryTax(){
        /*
        * Check the country tax data
        * */
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);

        assertEquals(0, Banking.getPCT("empire1"));

        Banking.setPCT("empire1", 20);
        assertEquals(20, Banking.getPCT("empire1"));
    }

    @Test
    void countryAddRemoveOwner(){
        Player testPlayer = server.addPlayer();
        Player testPlayer2 = server.addPlayer();
        Banking.create("empire1", testPlayer);

        /*
        * Add owner
        * */
        Banking.addOwner("empire1", testPlayer2);

        /*
        * Check that both are owners
        * */
        assertTrue(Banking.isOwner("empire1", testPlayer));
        assertTrue(Banking.isOwner("empire1", testPlayer2));
        assertEquals(2, Banking.GetOwners("empire1").size());

        /*
         * Remove owner
         * */
        Banking.removeOwner("empire1", testPlayer2);

        assertTrue(Banking.isOwner("empire1", testPlayer));
        assertFalse(Banking.isOwner("empire1", testPlayer2));
        assertEquals("none", Banking.Element(testPlayer2));
        assertEquals(1, Banking.GetOwners("empire1").size());
    }

    @Test
    void countryStability(){
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);

        double stability = Banking.getStability("empire1");

        /*
        * assert base stability
        * */
        assertEquals(70.0, stability);
    }

    @Test
    void capitalChangeStability(){
        Player testPlayer = server.addPlayer();
        Banking.create("empire1", testPlayer);

        assertEquals(70.0, Banking.getStability("empire1"));

        /*
        * Over 40 mil -> +5 stability
        * */
        Banking.deposit("empire1", 40000001);

        assertEquals(75.0, Banking.getStability("empire1"));

        /*
        * Over 60 mil -> +7 stability
        * */
        Banking.deposit("empire1", 20000000);
        assertEquals(77.0, Banking.getStability("empire1"));

        /*
         * Over 40 mil -> +5 stability (reduction)
         * */
        Banking.withdraw("empire1", 20000000);
        assertEquals(75.0, Banking.getStability("empire1"));
    }

}

