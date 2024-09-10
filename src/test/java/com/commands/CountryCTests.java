package com.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.plugin.PluginManagerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.SignClick;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.milkbowl.vault.economy.Economy;
import tools.MockDynmap;
import tools.MockEconomy;

import static org.junit.jupiter.api.Assertions.*;
import org.dynmap.DynmapAPI;
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
        Banking.clear();
    }

    @Test
    void createCountry(){

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*
        * Join message
        * */
        testPlayer.nextMessage();

        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);

        assertEquals("empire1", Banking.Element(testPlayer));

        testPlayer.assertSaid("§bcountry has been succesfully created");
        testPlayer.assertNoMoreSaid();


    }

    @Test
    void createCountryFailedPerms() {
        PlayerMock testPlayer = server.addPlayer();

        /*
         * Join message
         * */
        testPlayer.nextMessage();

        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);

        assertFalse(Banking.GetBanks().contains("empire1"));

        testPlayer.assertSaid("§bplayer does not have permission to create a country");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void createCountryFailedDuplicatedName() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*
         * Join message
         * */
        testPlayer.nextMessage();

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
        /*
         * Join message
         * */
        testPlayer.nextMessage();

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
    void countryDonateNegative() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        SignClick.getEconomy().depositPlayer(testPlayer, 1000);
        /*
         * Join message
         * */
        testPlayer.nextMessage();

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
        /*
         * Join message
         * */
        testPlayer.nextMessage();

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
         * Join message
         * */
        testPlayer.nextMessage();
        testPlayer2.nextMessage();

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

        assertEquals("empire1", Banking.Element(testPlayer2));


    }
}
