package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarketMenuTests {
    private ServerMock server;
    private SignClick plugin;

    private PlayerMock testPlayer;
    private InventoryView inventoryMenu;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        suc6 = Market.addCompany("TestCaseInc2", "TCI2", Market.getAccount(testPlayer));
        assertTrue(suc6);
        Market.getCompany("TCI2").type = "bank";

        suc6 = server.execute("company", testPlayer, "market").hasSucceeded();
        assertTrue(suc6);

        inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
    }

    @Test
    void menuSelector(){
        /*
        * Check that the menu selector is displaying properly
        * */
        ItemStack firstComp = inventoryMenu.getItem(0);
        TestTools.assertItem(firstComp, Material.SUNFLOWER, "§bTCI");

        ItemStack secondComp = inventoryMenu.getItem(1);
        TestTools.assertItem(secondComp, Material.GOLD_INGOT, "§bTCI2");
    }

    @Test
    void sellShare(){
        /*
        * Test to sell 1 share
        * */
        Company comp = Market.getCompany("TCI");
        assertEquals(1000000, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(0, comp.getCOM().getMarketShares());

        testPlayer.simulateInventoryClick(inventoryMenu, 0);
        InventoryView marketMenu = testPlayer.getOpenInventory();

        TestTools.assertItem(marketMenu.getItem(29), Material.RED_DYE, "§cSELL: 1 Share");
        testPlayer.simulateInventoryClick(29);

        assertEquals(999999, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(1, comp.getCOM().getMarketShares());

    }

    @Test
    void buyShare(){
        sellShare();
        /*
         * Test to buy 1 share
         * */
        Company comp = Market.getCompany("TCI");
        assertEquals(999999, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(1, comp.getCOM().getMarketShares());

        testPlayer.simulateInventoryClick(inventoryMenu, 0);
        InventoryView marketMenu = testPlayer.getOpenInventory();

        TestTools.assertItem(marketMenu.getItem(11), Material.LIME_DYE, "§aBUY: 1 Share");
        testPlayer.simulateInventoryClick(11);

        assertEquals(1000000, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(0, comp.getCOM().getMarketShares());

    }

    @Test
    void buyShareAlternative(){
        sellShare();
        /*
         * Test to buy 1 share, by pressing on the emerald
         * (indicating a buy of 10 normally, but now 1 because only 1 share available)
         * */
        Company comp = Market.getCompany("TCI");
        assertEquals(999999, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(1, comp.getCOM().getMarketShares());

        testPlayer.simulateInventoryClick(inventoryMenu, 0);
        InventoryView marketMenu = testPlayer.getOpenInventory();

        TestTools.assertItem(marketMenu.getItem(12), Material.EMERALD, "§aBUY: 1 Share");
        testPlayer.simulateInventoryClick(12);

        assertEquals(1000000, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(0, comp.getCOM().getMarketShares());

    }

    @Test
    void buySellAll(){
        /*
        * Check pre actions the Buy&Sell All
        * */
        Company comp = Market.getCompany("TCI");
        assertEquals(1000000, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(0, comp.getCOM().getMarketShares());

        testPlayer.simulateInventoryClick(inventoryMenu, 0);
        InventoryView marketMenu = testPlayer.getOpenInventory();

        TestTools.assertItem(marketMenu.getItem(15), Material.LIME_CONCRETE, "§fDOES NOTHING");
        TestTools.assertItem(marketMenu.getItem(14), Material.LIME_STAINED_GLASS, "§fDOES NOTHING");
        TestTools.assertItem(marketMenu.getItem(33), Material.RED_CONCRETE, "§cSELL: 1000000 Shares");

        sellShare();

        /*
         * Check after sell 1 share: actions the Buy&Sell All
         * */

        assertEquals(999999, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(1, comp.getCOM().getMarketShares());

        marketMenu = testPlayer.getOpenInventory();

        TestTools.assertItem(marketMenu.getItem(15), Material.LIME_CONCRETE, "§aBUY: 1 Share");
        TestTools.assertItem(marketMenu.getItem(33), Material.RED_CONCRETE, "§cSELL: 999999 Shares");

        /*
         * Check after Sell all the: the Buy&Sell All
         * */

        marketMenu = testPlayer.getOpenInventory();
        testPlayer.simulateInventoryClick(33);
        TestTools.assertItem(marketMenu.getItem(15), Material.LIME_CONCRETE, "§aBUY: 1000000 Shares");
        TestTools.assertItem(marketMenu.getItem(33), Material.RED_CONCRETE, "§fDOES NOTHING");
    }
}
