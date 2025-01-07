package com.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.economy.Country;

import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.SignClick;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import tools.TestTools;


class CountryCTests {


    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
    }

    @Test
    void createCountry(){

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);

        assertEquals("empire1", CountryManager.getCountry(testPlayer).getName());

        testPlayer.assertSaid("§bcountry has been succesfully created");
        testPlayer.assertNoMoreSaid();


    }

    @Test
    void createCountryFailedPerms() {
        PlayerMock testPlayer = server.addPlayer();

        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);

        assertFalse(CountryManager.getCountriesString().contains("empire1"));

        testPlayer.assertSaid("§bplayer does not have permission to create a country");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void createCountryFailedDuplicatedName() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*
        * First creation
        * */
        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);
        testPlayer.nextMessage();

        result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bthis country already exists");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void countryDonateSuc6() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        SignClick.getEconomy().depositPlayer(testPlayer, 1000);

        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);
        testPlayer.nextMessage();

        result = server.execute("country", testPlayer, "donate", "1000").hasSucceeded();
        assertTrue(result);
        testPlayer.assertSaid("§bYou paid 1000 to empire1");
        testPlayer.assertSaid("§bPlayer0 donated 1000 to your country");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void countryBalance() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        CountryManager.create("empire1", testPlayer);

        testPlayer.nextMessage();
        testPlayer.assertNoMoreSaid();

        CountryManager.getCountry("empire1").deposit(1000);

        /*
        * check balance of own country
        * */
        boolean result = server.execute("country", testPlayer, "bal").hasSucceeded();
        assertTrue(result);

        /*
        * check country balance
        * */
        testPlayer.assertSaid("§bsaldo: 1.000");
        testPlayer.assertNoMoreSaid();

        /*
         * check balance of provided country
         * */
        result = server.execute("country", testPlayer, "bal", "empire1").hasSucceeded();
        assertTrue(result);

        /*
         * check country balance
         * */
        testPlayer.assertSaid("§bsaldo: 1.000");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void countryDonateNegative() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        SignClick.getEconomy().depositPlayer(testPlayer, 1000);

        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);
        testPlayer.nextMessage();
        testPlayer.assertNoMoreSaid();
        result = server.execute("country", testPlayer, "donate", "-1000").hasSucceeded();
        assertTrue(result);
        testPlayer.assertSaid("§bYou cannot donate negative amounts");

        testPlayer.assertNoMoreSaid();
    }

    @Test
    void countryDonateNotEnoughMoney() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);
        testPlayer.nextMessage();
        testPlayer.assertNoMoreSaid();
        result = server.execute("country", testPlayer, "donate", "1000").hasSucceeded();
        assertTrue(result);
        testPlayer.assertSaid("§bYou have not enough money");

        testPlayer.assertNoMoreSaid();
    }

    @Test
    void countryInvite() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        /*
        * create country
        * */
        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);
        testPlayer.nextMessage();
        testPlayer.assertNoMoreSaid();

        result = server.execute("country", testPlayer, "invite", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bthe invite to join the country has been send to Player1");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§byou have an invite for §8empire1 §byou have 120s for accepting by \n" +
                "§c/country accept");
        testPlayer2.assertNoMoreSaid();

        /*
        * Accept invite
        * */

        result = server.execute("country", testPlayer2, "accept").hasSucceeded();
        assertTrue(result);
        testPlayer2.assertSaid("§byou succesfully joint this country");
        testPlayer2.assertNoMoreSaid();

        assertEquals("empire1", CountryManager.getCountry(testPlayer2).getName());

    }

    @Test
    void countryKick() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        Country country = CountryManager.create("empire1", testPlayer);
        country.addMember(testPlayer2);
        testPlayer.nextMessage();

        boolean result = server.execute("country", testPlayer, "kick", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§btarget has been kicked from your country");
        testPlayer.assertNoMoreSaid();

        /*
        * Check player2 not in country anymore
        * */
        assertNull(CountryManager.getCountry(testPlayer2));
    }

    @Test
    void countryAddLawEnforcement() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        country.addMember(testPlayer2);

        /*
        * Add player as law enforcement
        * */
        assertEquals(0, country.getLawEnforcement().size());
        country.addLawEnforcement(testPlayer2);
        assertEquals(1, country.getLawEnforcement().size());
        assertEquals(testPlayer2.getUniqueId(), country.getLawEnforcement().get(0));

        /*
        * Remove Law Enforcement
        * */
        country.removeLawEnforcement(testPlayer2);
        assertEquals(0, country.getLawEnforcement().size());

    }

    @Test
    void countrySpawn() {

        /*
        * Test the /spawn and /setspawn command
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        country.addMember(testPlayer2);

        testPlayer.nextMessage();

        /*
        * set spawn location
        * */
        World world = server.addSimpleWorld("world");
        testPlayer.setLocation(new Location(world, 10, 10, 10));
        boolean result = server.execute("country", testPlayer, "setspawn").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bspawn succesfully relocated");
        testPlayer.assertNoMoreSaid();

        /*
        * test spawn command executed by owner
        * */
        testPlayer.setLocation(new Location(world, 20, 10, 10));

        result = server.execute("country", testPlayer, "spawn").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bteleported to country spawn");
        testPlayer.assertNoMoreSaid();

        assertEquals(new Location(world, 10, 10, 10), testPlayer.getLocation());

        /*
         * test spawn command executed by member
         * */
        testPlayer2.setLocation(new Location(world, 20, 10, 10));

        result = server.execute("country", testPlayer2, "spawn").hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bteleported to country spawn");
        testPlayer2.assertNoMoreSaid();

        assertEquals(new Location(world, 10, 10, 10), testPlayer2.getLocation());

    }

}
