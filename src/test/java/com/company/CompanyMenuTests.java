package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.economy.*;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.companyPatent.PatentUpgradeJumper;
import com.klanting.signclick.menus.company.AuctionMenu;
import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.yaml.snakeyaml.error.Mark;
import tools.ExpandedServerMock;
import tools.TestTools;


import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CompanyMenuTests {
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

        suc6 = server.execute("company", testPlayer, "menu").hasSucceeded();
        assertTrue(suc6);

        inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
        LicenseSingleton.clear();
    }

    private CompanyI getCompany(int slot){
        ItemStack companyOption = inventoryMenu.getItem(slot);
        assertNotNull(companyOption);

        int startPos = companyOption.getItemMeta().getDisplayName().indexOf("[");
        int endPos = companyOption.getItemMeta().getDisplayName().length()-1;

        String companyName = companyOption.getItemMeta().getDisplayName().substring(startPos+1, endPos);
        return Market.getCompany(companyName);
    }

    private InventoryView openMenu(int slot){
        ItemStack companyOption = inventoryMenu.getItem(slot);
        assertNotNull(companyOption);


        testPlayer.simulateInventoryClick(inventoryMenu, slot);

        InventoryView companyMenu = testPlayer.getOpenInventory();
        assertNotNull(companyMenu);

        /*
         * Verify correct content
         * */

        assertEquals("§6Balance", companyMenu.getItem(13).getItemMeta().getDisplayName());
        assertEquals(Material.GOLD_BLOCK, companyMenu.getItem(13).getType());

        assertEquals("§6Patent", companyMenu.getItem(9).getItemMeta().getDisplayName());
        assertEquals(Material.NETHERITE_HELMET, companyMenu.getItem(9).getType());

        assertEquals("§6Upgrades", companyMenu.getItem(22).getItemMeta().getDisplayName());
        assertEquals(Material.EMERALD, companyMenu.getItem(22).getType());

        assertEquals("§6Patent Auction", companyMenu.getItem(27).getItemMeta().getDisplayName());
        assertEquals(Material.IRON_NUGGET, companyMenu.getItem(27).getType());

        return companyMenu;
    }

    private InventoryView openMenu(InventoryView inv, int slot){
        ItemStack option = inv.getItem(slot);
        assertNotNull(option);

        testPlayer.simulateInventoryClick(inv, slot);

        return testPlayer.getOpenInventory();
    }

    @Test
    void changeCompanyType(){

        plugin.getConfig().set("companyConfirmation", false);

        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        /*
        * open type menu
        * */
        InventoryView typeMenu = testPlayer.getOpenInventory();
        assertNotNull(typeMenu);

        assertEquals("§6bank", typeMenu.getItem(0).getItemMeta().getDisplayName());
        assertEquals(Material.GOLD_INGOT, typeMenu.getItem(0).getType());

        assertEquals("§6transport", typeMenu.getItem(1).getItemMeta().getDisplayName());
        assertEquals(Material.MINECART, typeMenu.getItem(1).getType());

        assertEquals("§6product", typeMenu.getItem(2).getItemMeta().getDisplayName());
        assertEquals(Material.IRON_CHESTPLATE, typeMenu.getItem(2).getType());

        assertEquals("§6real estate", typeMenu.getItem(3).getItemMeta().getDisplayName());
        assertEquals(Material.QUARTZ_BLOCK, typeMenu.getItem(3).getType());

        assertEquals("§6military", typeMenu.getItem(4).getItemMeta().getDisplayName());
        assertEquals(Material.BOW, typeMenu.getItem(4).getType());

        assertEquals("§6building", typeMenu.getItem(5).getItemMeta().getDisplayName());
        assertEquals(Material.BRICKS, typeMenu.getItem(5).getType());

        assertEquals("§6other", typeMenu.getItem(6).getItemMeta().getDisplayName());
        assertEquals(Material.SUNFLOWER, typeMenu.getItem(6).getType());

        testPlayer.simulateInventoryClick(typeMenu, 2);

        /*
        * Check that company is now a 'product' company
        * */
        assertEquals("product", Market.getCompany("COMP").getType());
    }

    @Test
    void companyUpgrade(){
        CompanyI comp = getCompany(0);

        InventoryView companyMenu = openMenu(0);

        /*
         * Click upgrade menu button
         * */
        testPlayer.simulateInventoryClick(companyMenu, 22);

        InventoryView upgradeMenu = testPlayer.getOpenInventory();
        assertNotNull(upgradeMenu);

        assertEquals("§6Patent Slot Lvl. §c0", upgradeMenu.getItem(10).getItemMeta().getDisplayName());
        assertEquals(Material.END_CRYSTAL, upgradeMenu.getItem(10).getType());
        assertEquals("§6Patent Upgrade Slot Lvl. §c0", upgradeMenu.getItem(11).getItemMeta().getDisplayName());
        assertEquals(Material.ITEM_FRAME, upgradeMenu.getItem(11).getType());
        assertEquals("§6Product Slots Lvl. §c0", upgradeMenu.getItem(12).getItemMeta().getDisplayName());
        assertEquals(Material.APPLE, upgradeMenu.getItem(12).getType());
        assertEquals("§6Board Size Lvl. §c0", upgradeMenu.getItem(13).getItemMeta().getDisplayName());
        assertEquals(Material.CHEST, upgradeMenu.getItem(13).getType());
        assertEquals("§6Invest Return Time Lvl. §c0", upgradeMenu.getItem(14).getItemMeta().getDisplayName());
        assertEquals(Material.EMERALD, upgradeMenu.getItem(14).getType());
        assertEquals("§6Research Modifier Lvl. §c0", upgradeMenu.getItem(15).getItemMeta().getDisplayName());
        assertEquals(Material.EXPERIENCE_BOTTLE, upgradeMenu.getItem(15).getType());
        assertEquals("§6Product Modifier Lvl. §c0", upgradeMenu.getItem(16).getItemMeta().getDisplayName());
        assertEquals(Material.FURNACE, upgradeMenu.getItem(16).getType());

        assertEquals(0, comp.getUpgrades().get(0).level);

        /*
        * Upgrade first option
        * */
        /*
        * Give company enough spendable
        * */
        comp.addBal(20000000.0);

        testPlayer.simulateInventoryClick(upgradeMenu, 11);
        assertEquals(1, comp.getUpgrades().get(0).level);
    }

    @Test
    void companyAuction(){
        CompanyI comp = getCompany(0);
        comp.addBal(10000000.0);

        /*
        * Tick needed to get auction values
        * */
        server.getScheduler().performTicks(1);

        InventoryView companyMenu = openMenu(0);

        /*
         * Click Auction menu button
         * */
        testPlayer.simulateInventoryClick(companyMenu, 27);

        InventoryView auctionMenu = testPlayer.getOpenInventory();
        assertNotNull(auctionMenu);

        ItemStack bidItem = auctionMenu.getItem(0);
        assertEquals("§7Bid by: None", bidItem.getItemMeta().getLore().get(1));
        /*
        * Bid on first
        * */
        testPlayer.simulateInventoryClick(auctionMenu, 0);
        bidItem = auctionMenu.getItem(0);
        assertEquals("§7Bid by: TCI", bidItem.getItemMeta().getLore().get(1));

        assertEquals(0, comp.getPatentUpgrades().size());
        testPlayer.closeInventory();

        /*
        * Check that after some time, stock delay has changed
        * */
        int i = 60*60*24*7*20+1;
        server.getScheduler().performTicks(i);

        AuctionMenu new_screen = new AuctionMenu(comp);
        testPlayer.openInventory(new_screen.getInventory());

        auctionMenu = testPlayer.getOpenInventory();
        bidItem = auctionMenu.getItem(0);

        assertEquals("§7Bid by: None", bidItem.getItemMeta().getLore().get(1));
        assertEquals(1, comp.getPatentUpgrades().size());
    }

    @Test
    void companyPatentDesign(){
        CompanyI comp = getCompany(0);

        /*
        * Add patent upgrade
        * */
        PatentUpgrade up = new PatentUpgradeJumper();
        up.level = 1;
        comp.getPatentUpgrades().add(up);
        assertNotNull(comp);

        /*
        * open company menu
        * */
        InventoryView companyMenu = openMenu(0);

        InventoryView patentSelector = openMenu(companyMenu, 9);

        InventoryView patentTypeSelector = openMenu(patentSelector, 0);

        InventoryView patentDesigner = openMenu(patentTypeSelector, 10);

        /*
        * Add patent upgrade
        * */
        InventoryView patentDesignerAddUpgrade = openMenu(patentDesigner, 18);

        patentDesigner = openMenu(patentDesignerAddUpgrade, 0);

        /*
        * Click apply button
        * */
        patentSelector = openMenu(patentDesigner, 8);

        assertEquals(Material.NETHERITE_HELMET, patentSelector.getItem(0).getType());

    }

    @Test
    void companyCreatePatentItem(){
        companyPatentDesign();
        testPlayer.closeInventory();

        /*
        * open inventory menu
        * */
        boolean suc6 = server.execute("company", testPlayer, "menu").hasSucceeded();
        assertTrue(suc6);

        inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);

        /*
        * Select menu
        * */
        InventoryView companyMenu = openMenu(0);

        InventoryView patentCraftingSelector = openMenu(companyMenu, 18);

        assertEquals(Material.NETHERITE_HELMET, patentCraftingSelector.getItem(0).getType());

        InventoryView craftingTutorialMenu = openMenu(patentCraftingSelector, 0);

        testPlayer.simulateInventoryClick(craftingTutorialMenu, 8);

        ItemStack patentSheet = testPlayer.getInventory().getItem(0);
        assertNotNull(patentSheet);

        /*Craft item*/
        ItemStack patentHelmetItem = TestTools.craftItemShapeless(server, new ItemStack[]{new ItemStack(Material.NETHERITE_HELMET), patentSheet});

        assertEquals("§6TCI:Nameless:0", patentHelmetItem.getItemMeta().getDisplayName());

        /*
        * Get created Item
        * */
        ItemStack patentItem = TestTools.craftItemShapeless(server, new ItemStack[]{patentHelmetItem, new ItemStack(Material.FEATHER)});
        assertEquals("§6Nameless", patentItem.getItemMeta().getDisplayName());

        assertEquals("§7Jumper 1", patentItem.getItemMeta().getLore().get(0));
        assertEquals("§9JumpBonus: 0.5", patentItem.getItemMeta().getLore().get(1));

    }

    @Test
    void companyPatentUpgradePersistent(){
        /*
        * Check if a patent upgrade is saved and loaded by the company save and load
        * */

        CompanyI comp = getCompany(0);

        PatentUpgrade up = new PatentUpgradeJumper();
        up.level = 1;
        comp.getPatentUpgrades().add(up);

        plugin = TestTools.reboot(server);

        comp = Market.getCompany("TCI");
        assertEquals(1, comp.getPatentUpgrades().size());

    }
    @Test
    void companyLicenseTest(){
        /*
        * ask a given company request a license from another company
        * */
        Market.addCompany("TestCaseInc2", "TCI2", Market.getAccount(testPlayer));

        CompanyI comp = Market.getCompany("TCI");
        CompanyI comp2 = Market.getCompany("TCI2");
        comp2.addProduct(new Product(Material.DIRT, 100, 100));

        server.getScheduler().performTicks(1);

        InventoryView companyMenu = openMenu(0);

        /*
        * Select the productList
        * */
        assertEquals(Material.JUKEBOX, companyMenu.getItem(21).getType());
        testPlayer.simulateInventoryClick(21);
        InventoryView productList = testPlayer.getOpenInventory();

        /*
        * choose to license an item
        * */
        assertEquals(Material.BOOK, productList.getItem(52).getType());
        testPlayer.simulateInventoryClick(52);


        /*
        * Select the product we want to license
        * */
        assertEquals("§6TestCaseInc2 [TCI2]", testPlayer.getOpenInventory().getItem(0).getItemMeta().getDisplayName());
        testPlayer.simulateInventoryClick(0);
        assertEquals(Material.DIRT, testPlayer.getOpenInventory().getItem(0).getType());
        testPlayer.simulateInventoryClick(0);

        /*
        * Change settings for the license request menu
        * increase royalty by 1%
        * decrease weekly pay by 1000
        * */
        InventoryView requestView = testPlayer.getOpenInventory();
        assertEquals("§7Weekly License cost: $1000.0", requestView.getItem(29).getItemMeta().getLore().get(0));
        assertEquals("§7Increased Cost: 0,00%", requestView.getItem(31).getItemMeta().getLore().get(0));
        assertEquals("§7Royalty Fee: 0,00%", requestView.getItem(33).getItemMeta().getLore().get(0));

        testPlayer.simulateInventoryClick(33-9);
        assertEquals("§7Weekly License cost: $1000.0", requestView.getItem(29).getItemMeta().getLore().get(0));
        assertEquals("§7Increased Cost: 0,00%", requestView.getItem(31).getItemMeta().getLore().get(0));
        assertEquals("§7Royalty Fee: 1,00%", requestView.getItem(33).getItemMeta().getLore().get(0));

        testPlayer.simulateInventoryClick(29+9);
        assertEquals("§7Weekly License cost: $0.0", requestView.getItem(29).getItemMeta().getLore().get(0));
        assertEquals("§7Increased Cost: 0,00%", requestView.getItem(31).getItemMeta().getLore().get(0));
        assertEquals("§7Royalty Fee: 1,00%", requestView.getItem(33).getItemMeta().getLore().get(0));

        /*
        * Send request
        * */
        testPlayer.simulateInventoryClick(52);

        assertEquals(1, LicenseSingleton.getInstance().getLicenseRequests().getLicensesFrom(comp2).size());
        assertEquals(1, LicenseSingleton.getInstance().getLicenseRequests().getLicensesTo(comp).size());

        /*
        * as the 2nd company accept the license request
        * */

        boolean suc6 = server.execute("company", testPlayer, "menu").hasSucceeded();
        assertTrue(suc6);

        inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);

        companyMenu = openMenu(1);

        /*
         * Select the productList
         * */
        assertEquals(Material.JUKEBOX, companyMenu.getItem(21).getType());
        testPlayer.simulateInventoryClick(21);
        productList = testPlayer.getOpenInventory();
        assertEquals(Material.DIRT, productList.getItem(0).getType());

        /*
        * Go to the list of requested licenses
        * */
        testPlayer.simulateInventoryClick(51);
        assertEquals(Material.DIRT, testPlayer.getOpenInventory().getItem(0).getType());

        testPlayer.simulateInventoryClick(0);

        /*
        * Check accept menu open
        * showing the right information
        * */
        assertEquals(Material.RED_WOOL, testPlayer.getOpenInventory().getItem(12).getType());
        assertEquals(Material.DIRT, testPlayer.getOpenInventory().getItem(13).getType());
        assertEquals(Material.LIME_WOOL, testPlayer.getOpenInventory().getItem(14).getType());

        /*
        * accept request
        * */
        testPlayer.simulateInventoryClick(14);

        assertEquals(0, LicenseSingleton.getInstance().getLicenseRequests().getLicensesFrom(comp2).size());
        assertEquals(0, LicenseSingleton.getInstance().getLicenseRequests().getLicensesTo(comp).size());

        assertEquals(1, LicenseSingleton.getInstance().getCurrentLicenses().getLicensesFrom(comp2).size());
        assertEquals(1, LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp).size());

    }


}
