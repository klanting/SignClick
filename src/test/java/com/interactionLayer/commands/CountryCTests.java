package com.interactionLayer.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.interactionLayer.events.MenuEvents;
import com.klanting.signclick.interactionLayer.routines.AutoSave;
import com.klanting.signclick.logicLayer.countryLogic.Country;

import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.BookParser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import tools.ExpandedServerMock;
import tools.TestTools;

import java.util.ArrayList;
import java.util.List;


class CountryCTests {


    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

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
    void countryInviteToLate() {
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

        server.getScheduler().performTicks(120*20L+1);

        /*
         * Accept invite to late
         * */

        result = server.execute("country", testPlayer2, "accept").hasSucceeded();
        assertTrue(result);
        testPlayer2.assertSaid("§bNo pending invites");
        testPlayer2.assertNoMoreSaid();

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
        testPlayer.assertSaid("§bCountry name: §7empire1\n" +
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
        plugin = TestTools.reboot(server);

        result = server.execute("country", testPlayer, "info").hasSucceeded();
        assertTrue(result);

        testPlayer.assertSaid("§bCountry name: §7empire1\n" +
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
                §bBaltop: page 1/1 §0
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
        AutoSave.stop();
        MenuEvents.stopMachineCheck();

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

    @Test
    void leaveCountry(){
        createCountry();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");
        country.addMember(testPlayer2);

        boolean result = server.execute("country", testPlayer2, "leave").hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bcountry succesfully left");
        testPlayer2.assertNoMoreSaid();

        assertEquals(0, country.getMembers().size());
    }

    @Test
    void promoteCountry(){
        /*
        * Check the country promote (staff only)
        * */
        createCountry();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");
        country.addMember(testPlayer2);

        assertEquals(1, country.getMembers().size());
        assertFalse(country.isOwner(testPlayer2));

        boolean result = server.execute("country", testPlayer2, "promote", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        assertEquals(0, country.getMembers().size());
        assertTrue(country.isOwner(testPlayer2));
    }

    @Test
    void promoteCountryFailed(){
        /*
         * Check the country promote (staff only) would fail when no username provided
         * */
        createCountry();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");
        country.addMember(testPlayer2);

        boolean result = server.execute("country", testPlayer2, "promote").hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bPlease enter /country promote <username>");
        testPlayer2.assertNoMoreSaid();
    }

    @Test
    void demoteCountry(){
        /*
         * Check the country demote (staff only)
         * */
        createCountry();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");
        country.addOwner(testPlayer2);

        assertEquals(0, country.getMembers().size());
        assertTrue(country.isOwner(testPlayer2));

        boolean result = server.execute("country", testPlayer2, "demote", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        assertEquals(1, country.getMembers().size());
        assertFalse(country.isOwner(testPlayer2));
    }

    @Test
    void demoteCountryFailed(){
        /*
         * Check the country demote (staff only) would fail when no username provided
         * */
        createCountry();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");
        country.addMember(testPlayer2);

        boolean result = server.execute("country", testPlayer2, "demote").hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bPlease enter /country demote <username>");
        testPlayer2.assertNoMoreSaid();
    }

    @Test
    void removeCountry(){
        /*
        * remove a country (staff only) from the system
        * */
        createCountry();
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");
        assertNotNull(country);

        boolean result = server.execute("country", testPlayer2, "remove", "empire1").hasSucceeded();
        assertTrue(result);

        country = CountryManager.getCountry("empire1");
        assertNull(country);
    }

    @Test
    void setOwnerCountry(){
        /*
        * set a player as owner (staff only)
        * */
        createCountry();
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer3 = TestTools.addPermsPlayer(server, plugin);

        Country country = CountryManager.getCountry("empire1");
        assertFalse(country.isOwner(testPlayer3));

        boolean result = server.execute("country", testPlayer2, "setowner", "empire1", testPlayer3.getName()).hasSucceeded();
        assertTrue(result);

        assertTrue(country.isOwner(testPlayer3));

        testPlayer3.assertSaid("§bYou are added as owner");
        testPlayer3.assertNoMoreSaid();

        testPlayer2.assertSaid("§bOwner has been set");
        testPlayer2.assertNoMoreSaid();
    }

    @Test
    void addMemberCountry(){
        /*
         * Check the country add Member (staff only)
         * */
        createCountry();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");

        assertEquals(0, country.getMembers().size());

        boolean result = server.execute("country", testPlayer2, "addmember", "empire1", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        assertEquals(1, country.getMembers().size());
    }

    @Test
    void removeMemberCountry(){
        /*
         * Check the country remove Member (staff only)
         * */
        createCountry();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");
        country.addMember(testPlayer2);

        assertEquals(1, country.getMembers().size());

        boolean result = server.execute("country", testPlayer2, "removemember", "empire1", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        assertEquals(0, country.getMembers().size());
    }

    @Test
    void removeOwnerCountry(){
        /*
         * Check the country remove Member (staff only)
         * */
        createCountry();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Country country = CountryManager.getCountry("empire1");
        country.addOwner(testPlayer2);

        assertEquals(2, country.getOwners().size());

        boolean result = server.execute("country", testPlayer2, "removeowner", "empire1", testPlayer2.getName()).hasSucceeded();
        assertTrue(result);

        assertEquals(1, country.getOwners().size());
    }

    @Test
    void countryTabComplete(){
        PlayerMock testPlayer2 = server.addPlayer();
        List<String> receivedAutoCompletes =  server.getCommandTabComplete(testPlayer2, "country ");

        List<String> autoCompletes = new ArrayList<>();
        autoCompletes.add("bal");
        autoCompletes.add("pay");
        autoCompletes.add("donate");
        autoCompletes.add("tax");
        autoCompletes.add("accept");
        autoCompletes.add("invite");
        autoCompletes.add("info");
        autoCompletes.add("leave");
        autoCompletes.add("kick");
        autoCompletes.add("spawn");
        autoCompletes.add("setspawn");
        autoCompletes.add("add_enforcement");
        autoCompletes.add("remove_enforcement");
        autoCompletes.add("menu");
        autoCompletes.add("election");
        autoCompletes.add("vote");
        autoCompletes.add("guide");

        assertEquals(autoCompletes, receivedAutoCompletes);

        /*
        * test staff autocomplete
        * */
        PlayerMock testPlayer3 = TestTools.addPermsPlayer(server, plugin);

        autoCompletes.add(0, "create");
        autoCompletes.add(1, "setowner");
        autoCompletes.add(2, "removeowner");
        autoCompletes.add(3, "color");
        autoCompletes.add(4, "promote");
        autoCompletes.add(5, "demote");
        autoCompletes.add(6, "remove");
        autoCompletes.add(7, "addmember");
        autoCompletes.add(8, "removemember");

        receivedAutoCompletes =  server.getCommandTabComplete(testPlayer3, "country ");

        assertEquals(autoCompletes, receivedAutoCompletes);
    }

    @Test
    void countryGuide(){
        PlayerMock testPlayer = server.addPlayer();
        assertNull(testPlayer.getInventory().getItem(0));
        boolean suc6 = server.execute("country", testPlayer, "guide").hasSucceeded();
        assertTrue(suc6);

        assertNotNull(testPlayer.getInventory().getItem(0));
        ItemStack book = testPlayer.getInventory().getItem(0);
        assertEquals(Material.WRITTEN_BOOK, book.getType());
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        List<String> pages = BookParser.getPages("countryGuide.book", testPlayer);
        assertEquals(pages, bookMeta.getPages());
    }

}
