package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerChatEvent;
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

    private void openMenu(){
        /*
        * open the market menu
        * */
        boolean suc6 = server.execute("company", testPlayer, "market").hasSucceeded();
        assertTrue(suc6);

        inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);
    }

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        suc6 = Market.addCompany("TestCaseInc2", "TCI2", Market.getAccount(testPlayer));
        assertTrue(suc6);
        Market.getCompany("TCI2").setType("Nature");

        openMenu();

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
        TestTools.assertItem(firstComp, Material.LANTERN, "§6TestCaseInc [TCI]");

        ItemStack secondComp = inventoryMenu.getItem(1);
        TestTools.assertItem(secondComp, Material.GRASS_BLOCK, "§6TestCaseInc2 [TCI2]");
    }

    @Test
    void sellShare(){
        /*
        * Test to sell 1 share
        * */
        CompanyI comp = Market.getCompany("TCI");
        assertEquals(1000, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(0, comp.getCOM().getMarketShares());

        testPlayer.simulateInventoryClick(inventoryMenu, 0);
        InventoryView marketMenu = testPlayer.getOpenInventory();

        TestTools.assertItem(marketMenu.getItem(29), Material.RED_DYE, "§c§lSELL: 1 Share");
        testPlayer.simulateInventoryClick(29);

        assertEquals(999, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(1, comp.getCOM().getMarketShares());

    }

    @Test
    void buyShare(){
        sellShare();
        /*
         * Test to buy 1 share
         * */
        CompanyI comp = Market.getCompany("TCI");
        assertEquals(999, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(1, comp.getCOM().getMarketShares());

        testPlayer.simulateInventoryClick(inventoryMenu, 0);
        InventoryView marketMenu = testPlayer.getOpenInventory();

        SignClick.getEconomy().depositPlayer(testPlayer, 100.0);
        TestTools.assertItem(marketMenu.getItem(11), Material.LIME_DYE, "§a§lBUY: 1 Share");
        testPlayer.simulateInventoryClick(11);

        assertEquals(1000, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(0, comp.getCOM().getMarketShares());

    }

    @Test
    void buyShareAlternative(){
        sellShare();
        /*
         * Test to buy 1 share, by pressing on the emerald
         * (indicating a buy of 10 normally, but now 1 because only 1 share available)
         * */
        CompanyI comp = Market.getCompany("TCI");
        assertEquals(999, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(1, comp.getCOM().getMarketShares());

        testPlayer.simulateInventoryClick(inventoryMenu, 0);
        InventoryView marketMenu = testPlayer.getOpenInventory();

        SignClick.getEconomy().depositPlayer(testPlayer, 100);
        TestTools.assertItem(marketMenu.getItem(12), Material.EMERALD, "§a§lBUY: 1 Share");
        testPlayer.simulateInventoryClick(12);

        assertEquals(1000, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(0, comp.getCOM().getMarketShares());

    }

    @Test
    void buySellAll(){
        /*
        * Check pre actions the Buy&Sell All
        * */
        CompanyI comp = Market.getCompany("TCI");
        assertEquals(1000, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(0, comp.getCOM().getMarketShares());

        testPlayer.simulateInventoryClick(inventoryMenu, 0);
        InventoryView marketMenu = testPlayer.getOpenInventory();

        TestTools.assertItem(marketMenu.getItem(15), Material.LIME_CONCRETE, "§f§lDOES NOTHING");
        TestTools.assertItem(marketMenu.getItem(14), Material.LIME_STAINED_GLASS, "§f§lDOES NOTHING");
        TestTools.assertItem(marketMenu.getItem(33), Material.RED_CONCRETE, "§c§lSELL: 1000 Shares");

        sellShare();

        /*
         * Check after sell 1 share: actions the Buy&Sell All
         * */

        assertEquals(999, comp.getCOM().getShareHolders().get(testPlayer.getUniqueId()));
        assertEquals(1, comp.getCOM().getMarketShares());

        marketMenu = testPlayer.getOpenInventory();

        TestTools.assertItem(marketMenu.getItem(15), Material.LIME_CONCRETE, "§a§lBUY: 1 Share");
        TestTools.assertItem(marketMenu.getItem(33), Material.RED_CONCRETE, "§c§lSELL: 999 Shares");

        /*
         * Check after Sell all the: the Buy&Sell All
         * */

        marketMenu = testPlayer.getOpenInventory();
        testPlayer.simulateInventoryClick(33);
        TestTools.assertItem(marketMenu.getItem(15), Material.LIME_CONCRETE, "§a§lBUY: 1000 Shares");
        TestTools.assertItem(marketMenu.getItem(33), Material.RED_CONCRETE, "§f§lDOES NOTHING");
    }

    @Test
    void marketMenuPaging(){
        /*
        * Test paging for market menu, when we have 46 companies
        * */
        testPlayer.closeInventory();

        for (int i=3; i<47; i++){
            boolean suc6 = Market.addCompany("TestCaseInc"+i, "TCI"+i, Market.getAccount(testPlayer));
            assertTrue(suc6);
        }

        openMenu();

        /*
        * Check has next page but not previous page
        * */
        assertEquals(Material.RED_DYE, testPlayer.getOpenInventory().getItem(45).getType());
        assertEquals(Material.ARROW, testPlayer.getOpenInventory().getItem(47).getType());

        /*
        * Check has item on each place 0 -44
        * */
        for (int i =0; i<45; i++){
            assertNotNull(testPlayer.getOpenInventory().getItem(i));
            assertNotEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(i).getType());
        }

        /*
        * Click next page
        * */
        testPlayer.simulateInventoryClick(47);

        /*
         * Check has previous page but not next page
         * */
        assertEquals(Material.ARROW, testPlayer.getOpenInventory().getItem(45).getType());
        assertEquals(Material.RED_DYE, testPlayer.getOpenInventory().getItem(47).getType());
        assertEquals("§cNo Next Page", testPlayer.getOpenInventory().getItem(47).getItemMeta().getDisplayName());

        /*
        * Check has 1 item
        * */
        assertEquals(Material.LANTERN, testPlayer.getOpenInventory().getItem(0).getType());
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(1).getType());

        /*
        * Check after click go back to this page
        * */
        testPlayer.simulateInventoryClick(0);
        testPlayer.simulateInventoryClick(44);

        /*
         * Check has previous page but not next page
         * */
        assertEquals(Material.ARROW, testPlayer.getOpenInventory().getItem(45).getType());
        assertEquals(Material.RED_DYE, testPlayer.getOpenInventory().getItem(47).getType());

        /*
         * Check has 1 item
         * */
        assertEquals(Material.LANTERN, testPlayer.getOpenInventory().getItem(0).getType());
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(1).getType());

    }

    @Test
    void marketMenuPagingSearch(){
        /*
         * Test paging search system
         * */
        testPlayer.closeInventory();

        for (int i=3; i<47; i++){
            boolean suc6 = Market.addCompany("TestCaseInc"+i, "TCI"+i, Market.getAccount(testPlayer));
            assertTrue(suc6);
        }

        openMenu();

        /*
         * Check search available
         * */
        assertEquals(Material.NAME_TAG, testPlayer.getOpenInventory().getItem(46).getType());

        /*
         * Check has item on each place 0 -44
         * */
        for (int i =0; i<45; i++){
            assertNotNull(testPlayer.getOpenInventory().getItem(i));
            assertNotEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(i).getType());
        }

        /*
        * do search on number '3'
        * */
        testPlayer.simulateInventoryClick(46);
        testPlayer.sendMessage("3");

        PlayerChatEvent chatEvent = new PlayerChatEvent(testPlayer, "3");
        server.getPluginManager().callEvent(chatEvent);

        assertEquals(Material.RED_WOOL, testPlayer.getOpenInventory().getItem(46).getType());

        /*
        * Clear search keyword
        * */
        testPlayer.simulateInventoryClick(46);

        /*
         * Check search available again
         * */
        assertEquals(Material.NAME_TAG, testPlayer.getOpenInventory().getItem(46).getType());

        /*
         * Check has item on each place 0 -44
         * */
        for (int i =0; i<45; i++){
            assertNotNull(testPlayer.getOpenInventory().getItem(i));
            assertNotEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(i).getType());
        }
    }
}
