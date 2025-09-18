package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.logicLayer.*;
import com.klanting.signclick.logicLayer.companyPatent.PatentUpgrade;
import com.klanting.signclick.logicLayer.companyPatent.PatentUpgradeJumper;
import com.klanting.signclick.interactionLayer.menus.company.AuctionMenu;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tools.ExpandedServerMock;
import tools.TestTools;


import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        assertEquals("§6§lBalance", companyMenu.getItem(13).getItemMeta().getDisplayName());
        assertEquals(Material.GOLD_BLOCK, companyMenu.getItem(13).getType());

        assertEquals("§6§lPatent", companyMenu.getItem(9).getItemMeta().getDisplayName());
        assertEquals(Material.NETHERITE_HELMET, companyMenu.getItem(9).getType());

        assertEquals("§6§lUpgrades", companyMenu.getItem(22).getItemMeta().getDisplayName());
        assertEquals(Material.EMERALD, companyMenu.getItem(22).getType());

        assertEquals("§6§lPatent Auction", companyMenu.getItem(27).getItemMeta().getDisplayName());
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

        SignClick.getConfigManager().getConfig("companies.yml").set("companyConfirmation", false);
        SignClick.getConfigManager().save();

        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        /*
        * open type menu
        * */
        InventoryView typeMenu = testPlayer.getOpenInventory();
        assertNotNull(typeMenu);

        assertEquals("§6Farmer", typeMenu.getItem(0).getItemMeta().getDisplayName());
        assertEquals(Material.WHEAT, typeMenu.getItem(0).getType());

        assertEquals("§6Fisherman", typeMenu.getItem(1).getItemMeta().getDisplayName());
        assertEquals(Material.COD, typeMenu.getItem(1).getType());

        assertEquals("§6Woodcutter", typeMenu.getItem(2).getItemMeta().getDisplayName());
        assertEquals(Material.OAK_LOG, typeMenu.getItem(2).getType());

        assertEquals("§6Building", typeMenu.getItem(3).getItemMeta().getDisplayName());
        assertEquals(Material.BRICKS, typeMenu.getItem(3).getType());

        assertEquals("§6Decoration", typeMenu.getItem(4).getItemMeta().getDisplayName());
        assertEquals(Material.LANTERN, typeMenu.getItem(4).getType());

        assertEquals("§6Nether", typeMenu.getItem(5).getItemMeta().getDisplayName());
        assertEquals(Material.NETHERRACK, typeMenu.getItem(5).getType());

        assertEquals("§6End", typeMenu.getItem(6).getItemMeta().getDisplayName());
        assertEquals(Material.END_STONE, typeMenu.getItem(6).getType());

        assertEquals("§6Mining", typeMenu.getItem(7).getItemMeta().getDisplayName());
        assertEquals(Material.STONE, typeMenu.getItem(7).getType());

        assertEquals("§6Fighter", typeMenu.getItem(8).getItemMeta().getDisplayName());
        assertEquals(Material.IRON_SWORD, typeMenu.getItem(8).getType());

        assertEquals("§6Hunter", typeMenu.getItem(9).getItemMeta().getDisplayName());
        assertEquals(Material.ROTTEN_FLESH, typeMenu.getItem(9).getType());

        assertEquals("§6Brewery", typeMenu.getItem(10).getItemMeta().getDisplayName());
        assertEquals(Material.GLASS_BOTTLE, typeMenu.getItem(10).getType());

        assertEquals("§6Enchantment", typeMenu.getItem(11).getItemMeta().getDisplayName());
        assertEquals(Material.ENCHANTED_BOOK, typeMenu.getItem(11).getType());

        assertEquals("§6Redstone", typeMenu.getItem(12).getItemMeta().getDisplayName());
        assertEquals(Material.REDSTONE, typeMenu.getItem(12).getType());

        testPlayer.simulateInventoryClick(typeMenu, 2);

        /*
        * Check that company is now a 'product' company
        * */
        assertEquals("Woodcutter", Market.getCompany("COMP").getType());
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

        assertEquals("§6§lPatent Slot§6 Lvl. §c0", upgradeMenu.getItem(10).getItemMeta().getDisplayName());
        assertEquals(Material.END_CRYSTAL, upgradeMenu.getItem(10).getType());
        assertEquals("§6§lPatent Upgrade Slot§6 Lvl. §c0", upgradeMenu.getItem(11).getItemMeta().getDisplayName());
        assertEquals(Material.ITEM_FRAME, upgradeMenu.getItem(11).getType());
        assertEquals("§6§lProduct Slots§6 Lvl. §c0", upgradeMenu.getItem(12).getItemMeta().getDisplayName());
        assertEquals(Material.APPLE, upgradeMenu.getItem(12).getType());
        assertEquals("§6§lBoard Size§6 Lvl. §c0", upgradeMenu.getItem(13).getItemMeta().getDisplayName());
        assertEquals(Material.CHEST, upgradeMenu.getItem(13).getType());
        assertEquals("§6§lInvest Return Time§6 Lvl. §c0", upgradeMenu.getItem(14).getItemMeta().getDisplayName());
        assertEquals(Material.EMERALD, upgradeMenu.getItem(14).getType());
        assertEquals("§6§lResearch Modifier§6 Lvl. §c0", upgradeMenu.getItem(15).getItemMeta().getDisplayName());
        assertEquals(Material.EXPERIENCE_BOTTLE, upgradeMenu.getItem(15).getType());
        assertEquals("§6§lProduct Modifier§6 Lvl. §c0", upgradeMenu.getItem(16).getItemMeta().getDisplayName());
        assertEquals(Material.FURNACE, upgradeMenu.getItem(16).getType());

        assertEquals(0, comp.getUpgrades().get(0).level);

        /*
        * Upgrade first option
        * */
        /*
        * Give company enough spendable
        * */
        comp.addBal(20000000.0);
        comp.setSpendable(20000000.0);

        testPlayer.simulateInventoryClick(upgradeMenu, 10);
        assertEquals(1, comp.getUpgrades().get(0).level);
    }

    @Test
    void companyAuction(){
        CompanyI comp = getCompany(0);
        comp.addBal(10000000.0);
        comp.setSpendable(10000000.0);

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
        assertTrue(bidItem.getItemMeta().getLore().contains("§7Bid by: None"));
        /*
        * Bid on first
        * */
        testPlayer.simulateInventoryClick(auctionMenu, 0);
        bidItem = auctionMenu.getItem(0);
        assertTrue(bidItem.getItemMeta().getLore().contains("§7Bid by: TCI"));

        assertEquals(0, comp.getPatentUpgrades().size());
        testPlayer.closeInventory();

        /*
        * Check that after some time, stock delay has changed
        * */
        int i = 60*60*24*7*20+1;
        server.getScheduler().performTicks(i);

        AuctionMenu new_screen = new AuctionMenu(comp, testPlayer.getUniqueId());
        testPlayer.openInventory(new_screen.getInventory());

        auctionMenu = testPlayer.getOpenInventory();
        bidItem = auctionMenu.getItem(0);

        assertTrue(bidItem.getItemMeta().getLore().contains("§7Bid by: None"));
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
        assertEquals("§9JumpBonus: 1.0", patentItem.getItemMeta().getLore().get(1));

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
        * After request, check back button working properly,
        * Should be routed to list of products and on press 'back' -> go to owner menu
        * */
        /*
        * indicator of productList
        * */
        assertEquals(Material.ENCHANTING_TABLE, testPlayer.getOpenInventory().getItem(49).getType());
        assertEquals(Material.BARRIER, testPlayer.getOpenInventory().getItem(53).getType());
        testPlayer.simulateInventoryClick(53);
        assertEquals(Material.GOLD_BLOCK, testPlayer.getOpenInventory().getItem(13).getType());

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

    @Test
    void companyProductCraftTest(){
        /*
        * Test that we can craft new products using other products
        * */
        ShapelessRecipe recip = new ShapelessRecipe(NamespacedKey.minecraft("test"), new ItemStack(Material.CRAFTING_TABLE));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        server.addRecipe(recip);

        CompanyI comp = Market.getCompany("TCI");
        comp.addProduct(new Product(Material.OAK_PLANKS, 10, 10));

        InventoryView companyMenu = openMenu(0);
        /*
        * Select product crafting icon
        * */
        assertEquals(Material.CRAFTING_TABLE, companyMenu.getItem(39).getType());
        testPlayer.simulateInventoryClick(39);

        /*
        * Check product craft menu properly initialized
        * */

        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(10).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(11).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(12).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(19).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(20).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(21).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(28).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(29).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(30).getType());

        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(25).getType());

        /*
        * Add 1 wooden plank
        * */
        testPlayer.simulateInventoryClick(10);
        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(0).getType());
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(1).getType());
        testPlayer.simulateInventoryClick(0);

        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(10).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(11).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(12).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(19).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(20).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(21).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(28).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(29).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(30).getType());

        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(25).getType());

        /*
        * 2nd item
        * */
        testPlayer.simulateInventoryClick(11);
        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(0).getType());
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(1).getType());
        testPlayer.simulateInventoryClick(0);
        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(11).getType());

        /*
        * 3rd item
        * */
        testPlayer.simulateInventoryClick(12);
        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(0).getType());
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(1).getType());
        testPlayer.simulateInventoryClick(0);
        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(12).getType());

        /*
        * 4th
        * */
        testPlayer.simulateInventoryClick(19);
        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(0).getType());
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(1).getType());
        testPlayer.simulateInventoryClick(0);
        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(19).getType());

        assertEquals(Material.CRAFTING_TABLE, testPlayer.getOpenInventory().getItem(25).getType());

        /*
        * Save product
        * */
        assertEquals(Material.LIME_WOOL, testPlayer.getOpenInventory().getItem(43).getType());
        testPlayer.simulateInventoryClick(43);

        assertEquals(2, comp.getProducts().size());


    }

    void addMockCraft(ItemStack finalResult){
        server.removeRecipe(NamespacedKey.minecraft("test"));
        ShapelessRecipe recip = new ShapelessRecipe(NamespacedKey.minecraft("test"), finalResult);
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        server.addRecipe(recip);
    }

    @Test
    void companyProductNotCraftLicenseTest(){
        /*
        * Check that we can craft with licensed products
        * */
        addMockCraft(new ItemStack(Material.CRAFTING_TABLE));

        CompanyI comp = Market.getCompany("TCI");
        Market.addCompany("TCI2", "TCI2", Market.getAccount(testPlayer));
        CompanyI comp2 = Market.getCompany("TCI2");
        comp2.addProduct(new Product(Material.OAK_PLANKS, 10, 10));
        LicenseSingleton.getInstance().getCurrentLicenses().addLicense(new License(comp2, comp,
                comp2.getProducts().get(0), 0.0, 0.0, 0.0));

        assertEquals(1, LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp).size());

        InventoryView companyMenu = openMenu(0);
        /*
         * Select product crafting icon
         * */
        assertEquals(Material.CRAFTING_TABLE, companyMenu.getItem(39).getType());
        testPlayer.simulateInventoryClick(39);

        /*
         * Check product craft menu properly initialized
         * */
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(10).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(11).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(12).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(19).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(20).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(21).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(28).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(29).getType());
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(30).getType());

        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, testPlayer.getOpenInventory().getItem(25).getType());

        /*
         * Ensure the wooden plank not visible
         * */
        testPlayer.simulateInventoryClick(10);
        assertEquals(Material.OAK_PLANKS, testPlayer.getOpenInventory().getItem(0).getType());

    }

    @Test
    void companyResearch(){
        /*
        * Process company research
        * */
        CompanyI comp = getCompany(0);
        comp.addBal(1000.0);
        comp.setSpendable(1000.0);

        assertNotNull(comp);

        /*
         * open company menu
         * */
        InventoryView companyMenu = openMenu(0);

        assertEquals(Material.POTION, companyMenu.getItem(41).getType());
        InventoryView researchSelector = openMenu(companyMenu, 41);

        assertEquals(Material.TORCH, researchSelector.getItem(0).getType());
        assertEquals(Material.RED_STAINED_GLASS_PANE, researchSelector.getItem(2).getType());
        assertEquals(Material.BLACK_STAINED_GLASS_PANE, researchSelector.getItem(4).getType());

        assertEquals("§7Research 0,00% completed", researchSelector.getItem(0).getItemMeta().getLore().get(0));
        assertEquals("§7IDLE", researchSelector.getItem(0).getItemMeta().getLore().get(1));

        /*
        * Start Research
        * */
        testPlayer.simulateInventoryClick(4);
        assertEquals(Material.TORCH, researchSelector.getItem(0).getType());
        assertEquals(Material.LIGHT_BLUE_STAINED_GLASS_PANE, researchSelector.getItem(2).getType());
        assertEquals(Material.WHITE_STAINED_GLASS_PANE, researchSelector.getItem(4).getType());

        assertEquals("§7Research 0,00% completed", researchSelector.getItem(0).getItemMeta().getLore().get(0));
        assertEquals("§70h 21m 25s", researchSelector.getItem(0).getItemMeta().getLore().get(1));


        server.getScheduler().performTicks(1300*20L);
        testPlayer.closeInventory();
        researchSelector = openMenu(companyMenu, 41);

        assertEquals(Material.TORCH, researchSelector.getItem(0).getType());
        assertEquals(Material.LIME_STAINED_GLASS_PANE, researchSelector.getItem(2).getType());
        assertEquals(Material.LIME_STAINED_GLASS_PANE, researchSelector.getItem(4).getType());
        assertEquals("§aCOMPLETED", researchSelector.getItem(2).getItemMeta().getDisplayName());

    }

    @Test
    void companySpendable(){
        /*
        * Change spendable of your company from within the UI
        * */
        CompanyI comp = getCompany(0);
        assertNotNull(comp);

        /*
         * open company menu
         * */
        InventoryView companyMenu = openMenu(0);
        assertEquals(Material.GOLD_INGOT, companyMenu.getItem(40).getType());

        InventoryView financialMenu = openMenu(companyMenu, 40);

        /*
        * Increase spendable
        * */
        assertEquals(0.0, comp.getSpendable());
        assertEquals(Material.LIME_DYE, financialMenu.getItem(11).getType());
        testPlayer.simulateInventoryClick(financialMenu, 11);
        assertEquals(1.0, comp.getSpendable());

    }

    @Test
    void companyProductSlotLimit(){
        /*
         * Test that the amount of product slots is limited
         * */

        /*
        * open menu
        * */
        CompanyI comp = Market.getCompany("TCI");
        comp.addProduct(new Product(Material.OAK_PLANKS, 10, 10));

        InventoryView companyMenu = openMenu(0);

        List<Material> results = List.of(Material.CRAFTING_TABLE, Material.DIRT, Material.STONE,
                Material.COBBLESTONE, Material.RED_STAINED_GLASS_PANE, Material.RED_DYE,
                Material.ITEM_FRAME, Material.LIGHT_GRAY_DYE, Material.BARRIER);

        for (Material material: results){
            /*
             * Add recipe
             * */
            addMockCraft(new ItemStack(material));

            /*
             * Select product crafting icon
             * */
            assertEquals(Material.CRAFTING_TABLE, companyMenu.getItem(39).getType());
            testPlayer.simulateInventoryClick(39);

            /*
             * select all items
             * */
            testPlayer.simulateInventoryClick(10);
            testPlayer.simulateInventoryClick(0);
            testPlayer.simulateInventoryClick(11);
            testPlayer.simulateInventoryClick(0);
            testPlayer.simulateInventoryClick(12);
            testPlayer.simulateInventoryClick(0);
            testPlayer.simulateInventoryClick(19);
            testPlayer.simulateInventoryClick(0);

            assertEquals(Material.LIME_WOOL, testPlayer.getOpenInventory().getItem(43).getType());
            testPlayer.simulateInventoryClick(43);

            assertNotEquals(10, comp.getProducts().size());
        }

        assertEquals("§cYou don't have any free product slots. Used: 9/9 (Research products are always added)",
                testPlayer.nextMessage());
        testPlayer.assertNoMoreSaid();

    }

    @Test
    void companyChiefMenuTest(){
        /*
        * test that checks that we can choose a new chief using the UI
        * */
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        CompanyI comp = Market.getCompany("TCI");
        assertEquals(testPlayer.getUniqueId(), comp.getCOM().getBoard().getChief("CEO"));

        InventoryView companyMenu = openMenu(0);

        /*
        * select the iron helmet -> chief menu
        * */
        testPlayer.simulateInventoryClick(companyMenu, 23);

        assertEquals(Material.PLAYER_HEAD, testPlayer.getOpenInventory().getItem(2).getType());
        assertEquals("§7CEO: Player0", testPlayer.getOpenInventory().getItem(2).getItemMeta().getDisplayName());
        assertEquals(Material.IRON_HELMET, testPlayer.getOpenInventory().getItem(4).getType());
        assertEquals("§7CTO: Unassigned", testPlayer.getOpenInventory().getItem(4).getItemMeta().getDisplayName());
        assertEquals(Material.IRON_HELMET, testPlayer.getOpenInventory().getItem(6).getType());
        assertEquals("§7CFO: Unassigned", testPlayer.getOpenInventory().getItem(6).getItemMeta().getDisplayName());

        /*
        * go to the CEO menu
        * */
        testPlayer.simulateInventoryClick(2);
        assertEquals(Material.PLAYER_HEAD, testPlayer.getOpenInventory().getItem(13).getType());
        assertEquals("§7CEO: Player0", testPlayer.getOpenInventory().getItem(13).getItemMeta().getDisplayName());

        assertEquals(Material.PLAYER_HEAD, testPlayer.getOpenInventory().getItem(24).getType());
        assertEquals("§7Supporting: Player0", testPlayer.getOpenInventory().getItem(24).getItemMeta().getDisplayName());
        assertEquals("§7CEO Votes: 1", testPlayer.getOpenInventory().getItem(24).getItemMeta().getLore().get(0));

        /*
        * support someone else as CEO
        * */
        testPlayer.simulateInventoryClick(24);

        PlayerChatEvent chatEvent = new PlayerChatEvent(testPlayer, testPlayer2.getName());
        server.getPluginManager().callEvent(chatEvent);

        /*
        * Check CEO has been changed
        * */
        assertEquals(Material.PLAYER_HEAD, testPlayer.getOpenInventory().getItem(13).getType());
        assertEquals("§7CEO: Player1", testPlayer.getOpenInventory().getItem(13).getItemMeta().getDisplayName());

        assertEquals(Material.PLAYER_HEAD, testPlayer.getOpenInventory().getItem(24).getType());
        assertEquals("§7Supporting: Player1", testPlayer.getOpenInventory().getItem(24).getItemMeta().getDisplayName());
        assertEquals("§7CEO Votes: 1", testPlayer.getOpenInventory().getItem(24).getItemMeta().getLore().get(0));

    }

    @Test
    void companyChiefMenuTestBug(){
        /*
         * let a player start as CEO of a company
         * Now vote on itself as CFO, this player should remain just CEO, but in the bug he/she also becomes CFO
         * */

        CompanyI comp = Market.getCompany("TCI");
        assertEquals(testPlayer.getUniqueId(), comp.getCOM().getBoard().getChief("CEO"));

        InventoryView companyMenu = openMenu(0);

        /*
         * select the iron helmet -> chief menu
         * */
        testPlayer.simulateInventoryClick(companyMenu, 23);

        assertEquals(Material.PLAYER_HEAD, testPlayer.getOpenInventory().getItem(2).getType());
        assertEquals("§7CEO: Player0", testPlayer.getOpenInventory().getItem(2).getItemMeta().getDisplayName());
        assertEquals(Material.IRON_HELMET, testPlayer.getOpenInventory().getItem(4).getType());
        assertEquals("§7CTO: Unassigned", testPlayer.getOpenInventory().getItem(4).getItemMeta().getDisplayName());
        assertEquals(Material.IRON_HELMET, testPlayer.getOpenInventory().getItem(6).getType());
        assertEquals("§7CFO: Unassigned", testPlayer.getOpenInventory().getItem(6).getItemMeta().getDisplayName());

        /*
         * go to the CFO menu
         * */
        testPlayer.simulateInventoryClick(6);
        assertEquals(Material.IRON_HELMET, testPlayer.getOpenInventory().getItem(13).getType());
        assertEquals(Material.SKELETON_SKULL, testPlayer.getOpenInventory().getItem(24).getType());

        /*
         * support yourself as CFO
         * */
        testPlayer.simulateInventoryClick(24);

        PlayerChatEvent chatEvent = new PlayerChatEvent(testPlayer, testPlayer.getName());
        server.getPluginManager().callEvent(chatEvent);

        /*
         * Check CEO has been changed
         * */
        assertEquals(Material.IRON_HELMET, testPlayer.getOpenInventory().getItem(13).getType());
        assertEquals("§7CFO: Unassigned", testPlayer.getOpenInventory().getItem(13).getItemMeta().getDisplayName());

        assertEquals(Material.PLAYER_HEAD, testPlayer.getOpenInventory().getItem(24).getType());
        assertEquals("§7Supporting: Player0", testPlayer.getOpenInventory().getItem(24).getItemMeta().getDisplayName());
        assertEquals("§7CFO Votes: 1", testPlayer.getOpenInventory().getItem(24).getItemMeta().getLore().get(0));

    }


}
