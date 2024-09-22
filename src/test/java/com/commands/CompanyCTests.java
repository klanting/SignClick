package com.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.Economy.CountryDep;
import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.Economy.CountryManager;
import com.klanting.signclick.Economy.Market;
import com.klanting.signclick.SignClick;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tools.TestTools;


import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CompanyCTests {

    private ServerMock server;
    private SignClick plugin;

    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.add_business("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
    }

    @Test
    void companyMenu(){

        boolean suc6 = server.execute("company", testPlayer, "menu").hasSucceeded();
        assertTrue(suc6);


        InventoryView inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);

        ItemStack companyOption = inventoryMenu.getItem(0);
        ItemStack companyOption2 = inventoryMenu.getItem(1);

        assertNotNull(companyOption);
        assertNull(companyOption2);

        /*
        * Check first item is a company
        * */
        String companyName = companyOption.getItemMeta().getDisplayName();
        assertEquals("TCI", companyName);
    }

    @Test
    void companyShareTop(){

        boolean suc6 = server.execute("company", testPlayer, "sharetop", "TCI").hasSucceeded();
        assertTrue(suc6);

        /*
        * Check correctly received sharetop
        * */
        testPlayer.assertSaid("§bsharetop:");
        testPlayer.assertSaid("§9Player0: §f1.000.000 (100,00%)");
        testPlayer.assertSaid("§eMarket: §f0 (0,00%)");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyInfo(){

        boolean suc6 = server.execute("company", testPlayer, "info", "TCI").hasSucceeded();
        assertTrue(suc6);

        /*
         * Check correctly received info
         * */
        testPlayer.assertSaid("§bName: §7TestCaseInc\n" +
                "§bStockname: §7TCI\n" +
                "§bCEO: §7[Player0]\n" +
                "§bbal: §70\n" +
                "§bshares: §71.000.000\n" +
                "§bshareholders: §7[Player0]");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyGive(){

        SignClick.getEconomy().depositPlayer(testPlayer, 1000);

        Company comp = Market.get_business("TCI");
        assertEquals(0, comp.get_value());

        boolean suc6 = server.execute("company", testPlayer, "give", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to give §f1.000§b to §fTCI\n" +
                "§c/company give TCI 1000.0");
        testPlayer.assertNoMoreSaid();

        /*
        * Confirmation
        * */
        suc6 = server.execute("company", testPlayer, "give", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§byou succesfully gave §f1.000§b to §fTCI");
        testPlayer.assertSaid("§byour business §fTCI §b received §f1000.0 §b from §fPlayer0");
        testPlayer.assertNoMoreSaid();

        assertEquals(1000, comp.get_value());

    }

    @Test
    void companySellBuyShares(){
        companyGive();
        Company comp = Market.get_business("TCI");

        assertEquals(1000000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));

        /*
        * Sell 1000 stocks
        * */
        boolean suc6 = server.execute("company", testPlayer, "sell", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to sell §f1000§b from §fTCI§b for a price of §63,42 \n" +
                "§c/company sell TCI 1000");
        testPlayer.assertNoMoreSaid();
        suc6 = server.execute("company", testPlayer, "sell", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bsell: §aaccepted");
        testPlayer.assertNoMoreSaid();

        assertEquals(999000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(1000, Market.get_market_amount("TCI"));
        assertEquals(993, Math.round(comp.get_value()));

        /*
         * Buy 1000 stocks
         * */
        SignClick.getEconomy().depositPlayer(testPlayer, 500);

        suc6 = server.execute("company", testPlayer, "buy", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);
        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to buy §f1000§b from §fTCI for a price of §63,60 \n" +
                "§c/company buy TCI 1000");
        testPlayer.assertNoMoreSaid();
        suc6 = server.execute("company", testPlayer, "buy", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);
        testPlayer.assertSaid("§bbuy: §aaccepted");
        testPlayer.assertNoMoreSaid();

        assertEquals(1000000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(0, Market.get_market_amount("TCI"));
        assertEquals(997, Math.round(comp.get_value()));

    }

    @Test
    void companyTransferShares(){

        PlayerMock testPlayer2 = server.addPlayer();

        assertEquals(1000000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(null, Market.getAccount(testPlayer2.getUniqueId()).shares.get("TCI"));

        boolean suc6 = server.execute("company", testPlayer, "transfer",
                "TCI", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to transfer §f1000§b shares to §fPlayer1\n" +
                "§c/company transfer TCI Player1 1000");
        testPlayer.assertNoMoreSaid();

        /*
        * confirm transfer
        * */
        suc6 = server.execute("company", testPlayer, "transfer",
                "TCI", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§btransfer: §aaccepted");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§breceived: 1000 shares for TCI from Player0");
        testPlayer2.assertNoMoreSaid();

        assertEquals(999000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(1000, Market.getAccount(testPlayer2.getUniqueId()).shares.get("TCI"));
    }

    @Test
    void companyPortfolio(){

        boolean suc6 = server.execute("company", testPlayer, "portfolio").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bportfolio:\n");
        testPlayer.assertSaid("§b1. §3TCI: §70 (100,00%)\n");
        testPlayer.assertSaid("§9Total value: §e0");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyGetSupport(){

        boolean suc6 = server.execute("company", testPlayer, "get_support", "TCI", testPlayer.getName()).hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplayer supports §7Player0");
        testPlayer.assertNoMoreSaid();
    }
}
