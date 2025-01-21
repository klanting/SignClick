package com.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.economy.Country;

import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Market;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
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

    @Test
    void countrySetColor() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryManager.create("empire1", testPlayer);

        testPlayer.nextMessage();

        assertEquals(testPlayer.getName(), testPlayer.getPlayerListName());

        boolean result = server.execute("country", testPlayer, "color", "empire1", "AQUA").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bColor has been changed to AQUA");
        testPlayer.assertNoMoreSaid();

        /*
        * Check TabColor Updated
        * */
        assertEquals("§b"+testPlayer.getName(), testPlayer.getPlayerListName());

        /*
        * Simulate player rejoin, to see if TabColor updates accordingly
        * */

        PlayerQuitEvent quitEvent = new PlayerQuitEvent(testPlayer, testPlayer.getName() + " left the game");
        server.getPluginManager().callEvent(quitEvent);

        PlayerJoinEvent joinEvent = new PlayerJoinEvent(testPlayer, testPlayer.getName() + " joined the game");
        server.getPluginManager().callEvent(joinEvent);

        /*
         * Check TabColor Updated
         * */
        assertEquals("§b"+testPlayer.getName(), testPlayer.getPlayerListName());
    }

    @Test
    void countrySetColorIncorrect() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryManager.create("empire1", testPlayer);

        testPlayer.nextMessage();

        assertEquals(testPlayer.getName(), testPlayer.getPlayerListName());

        boolean result = server.execute("country", testPlayer, "color", "empire1", "AQUI").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bColor AQUI is not a valid color");
        testPlayer.assertNoMoreSaid();

        assertEquals(testPlayer.getName(), testPlayer.getPlayerListName());
    }

    @Test
    void countrySetColorCountryIncorrect() {
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryManager.create("empire1", testPlayer);

        testPlayer.nextMessage();

        assertEquals(testPlayer.getName(), testPlayer.getPlayerListName());

        boolean result = server.execute("country", testPlayer, "color", "empire2", "AQUA").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bThe country empire2 does not exists");
        testPlayer.assertNoMoreSaid();

        assertEquals(testPlayer.getName(), testPlayer.getPlayerListName());
    }

    @Test
    void countryPay() {
        /*
        * Pay a player as the country leader
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        country.deposit(1000);

        testPlayer.nextMessage();
        assertEquals(1000, country.getBalance());
        assertEquals(0, SignClick.getEconomy().getBalance(testPlayer2));

        boolean result = server.execute("country", testPlayer, "pay", testPlayer2.getName(), "100").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§byou paid 100 to Player1");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§byou got 100 from empire1");
        testPlayer2.assertNoMoreSaid();

        assertEquals(100, SignClick.getEconomy().getBalance(testPlayer2));
        assertEquals(900, country.getBalance());
    }

    @Test
    void countryPaySelf() {
        /*
         * Pay yourself as the country leader
         * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        country.deposit(1000);

        testPlayer.nextMessage();
        assertEquals(1000, country.getBalance());
        assertEquals(0, SignClick.getEconomy().getBalance(testPlayer));

        boolean result = server.execute("country", testPlayer, "pay", testPlayer.getName(), "100").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§byou cannot pay yourself");
        testPlayer.assertNoMoreSaid();

        assertEquals(1000, country.getBalance());
        assertEquals(0, SignClick.getEconomy().getBalance(testPlayer));
    }

    @Test
    void countryPayNegative() {
        /*
         * Pay a player as the country leader with a negative amount
         * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        country.deposit(1000);

        testPlayer.nextMessage();
        assertEquals(1000, country.getBalance());
        assertEquals(0, SignClick.getEconomy().getBalance(testPlayer2));

        boolean result = server.execute("country", testPlayer, "pay", testPlayer2.getName(), "-100").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bYou cannot pay negative amounts");
        testPlayer.assertNoMoreSaid();

        assertEquals(0, SignClick.getEconomy().getBalance(testPlayer2));
        assertEquals(1000, country.getBalance());
    }

    @Test
    void countryTax() {
        /*
         * Pay a player as the country leader
         * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);

        testPlayer.nextMessage();

        assertEquals(0, country.getTaxRate());

        boolean result = server.execute("country", testPlayer, "tax", "20").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bthe tax has been changed");
        testPlayer.assertNoMoreSaid();

        assertEquals(0.2, country.getTaxRate());

    }

    @Test
    void countryInfo() {
        /*
         * Retrieve the country information
         * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        CountryManager.create("empire1", testPlayer);

        testPlayer.nextMessage();

        boolean result = server.execute("country", testPlayer, "info").hasSucceeded();
        assertTrue(result);
        testPlayer.assertSaid("§bBank: §7empire1\n" +
                "§bbalance: §70\n" +
                "§bowners: §7[Player0]\n" +
                "§bmembers: §7[]\n" +
                "§blaw enforcement: §7[]\n" +
                "§btaxrate: §70.0\n" +
                "§bstability: §770\n" +
                "§bspawn: §7No spawn has been set use '/country setspawn' to set a country spawn location\n" +
                "§bparties: §7[Government]");
        testPlayer.assertNoMoreSaid();

        /*
         * Restart Server, check persistence
         * */
        plugin.onDisable();
        CountryManager.clear();
        Market.clear();
        plugin = TestTools.setupPlugin(server);

        result = server.execute("country", testPlayer, "info").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bBank: §7empire1\n" +
                "§bbalance: §70\n" +
                "§bowners: §7[Player0]\n" +
                "§bmembers: §7[]\n" +
                "§blaw enforcement: §7[]\n" +
                "§btaxrate: §70.0\n" +
                "§bstability: §770\n" +
                "§bspawn: §7No spawn has been set use '/country setspawn' to set a country spawn location\n"+
                "§bparties: §7[Government]");
        testPlayer.assertNoMoreSaid();

    }

    @Test
    void countryBaltop(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        CountryManager.create("empire1", testPlayer);
        CountryManager.create("empire2", testPlayer2);

        testPlayer.nextMessage();
        testPlayer2.nextMessage();

        boolean result = server.execute("country", testPlayer, "baltop").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("""
                §bBaltop:§0
                §b1.§3 empire1: §70
                §b2.§3 empire2: §70""");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void countryHandleLawEnforcement(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        country.addMember(testPlayer2);

        testPlayer.nextMessage();
        assertEquals(0, country.getLawEnforcement().size());

        boolean result = server.execute("country", testPlayer, "add_enforcement", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§byou succesfully assigned an law enforcement agent");
        testPlayer.assertNoMoreSaid();

        assertEquals(1, country.getLawEnforcement().size());
        assertEquals(testPlayer2.getUniqueId(), country.getLawEnforcement().get(0));

        result = server.execute("country", testPlayer, "remove_enforcement", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§byou succesfully resigned an law enforcement agent");
        testPlayer.assertNoMoreSaid();
        assertEquals(0, country.getLawEnforcement().size());
    }

    @Test
    void countryElection(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.create("empire1", testPlayer);
        country.addMember(testPlayer2);
        country.createParty("testParty", testPlayer2.getUniqueId());
        assertNull(country.getCountryElection());

        testPlayer.nextMessage();
        assertEquals("Government", country.getRuling().name);

        /*
        * Start elections
        * */
        boolean result = server.execute("country", testPlayer, "election").hasSucceeded();
        assertTrue(result);
        assertNotNull(country.getCountryElection());
        testPlayer.assertSaid("§belections started");
        testPlayer.assertNoMoreSaid();

        /*
        * Start voting
        * */
        result = server.execute("country", testPlayer, "vote").hasSucceeded();
        assertTrue(result);

        InventoryView electionView = testPlayer.getOpenInventory();
        testPlayer.simulateInventoryClick(electionView, 1);

        server.getScheduler().performTicks(60*20*60*24*7+1);

        assertEquals("testParty", country.getRuling().name);
        assertNull(country.getCountryElection());

    }

}
