package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.Economy.Market;
import com.klanting.signclick.Menus.CompanySelector;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tools.TestTools;


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

import java.util.Objects;


public class CompanyMenuTests {
    private ServerMock server;
    private SignClick plugin;

    private PlayerMock testPlayer;
    private InventoryView inventoryMenu;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.add_business("TestCaseInc", "TCI", Market.get_account(testPlayer));
        assertTrue(suc6);

        suc6 = server.execute("company", testPlayer, "menu").hasSucceeded();
        assertTrue(suc6);

        inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Banking.clear();
        Market.clear();
    }

    private Company getCompany(int slot){
        ItemStack companyOption = inventoryMenu.getItem(slot);
        assertNotNull(companyOption);
        String companyName = companyOption.getItemMeta().getDisplayName();
        return Market.get_business(companyName);
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
        assertEquals("§6Type", companyMenu.getItem(8).getItemMeta().getDisplayName());
        assertEquals(Material.SUNFLOWER, companyMenu.getItem(8).getType());

        assertEquals("§6Balance", companyMenu.getItem(13).getItemMeta().getDisplayName());
        assertEquals(Material.GOLD_BLOCK, companyMenu.getItem(13).getType());

        assertEquals("§6Patent", companyMenu.getItem(21).getItemMeta().getDisplayName());
        assertEquals(Material.NETHERITE_HELMET, companyMenu.getItem(21).getType());

        assertEquals("§6Upgrades", companyMenu.getItem(22).getItemMeta().getDisplayName());
        assertEquals(Material.EMERALD, companyMenu.getItem(22).getType());

        assertEquals("§6Auction", companyMenu.getItem(23).getItemMeta().getDisplayName());
        assertEquals(Material.GOLD_NUGGET, companyMenu.getItem(23).getType());

        return companyMenu;
    }

    void printInventory(InventoryView inv){
        for (int i=0; i<inv.countSlots(); i++){
            if (inv.getItem(i) == null){
                continue;
            }
            System.out.print(i+": "+inv.getItem(i).getType().toString()+"\n");
        }
    }

    @Test
    void changeCompanyType(){
        Company comp = getCompany(0);
        InventoryView companyMenu = openMenu(0);

        /*
        * Click type selector
        * */
        testPlayer.simulateInventoryClick(companyMenu, 8);

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

        testPlayer.simulateInventoryClick(typeMenu, 2);

        /*
        * Check that company is now a 'product' company
        * */
        assertEquals("product", comp.type);
    }

    @Test
    void companyUpgrade(){
        Company comp = getCompany(0);

        InventoryView companyMenu = openMenu(0);

        /*
         * Click upgrade menu button
         * */
        testPlayer.simulateInventoryClick(companyMenu, 22);

        InventoryView upgradeMenu = testPlayer.getOpenInventory();
        assertNotNull(upgradeMenu);

        assertEquals("§6Extra Points Lvl. §c0", upgradeMenu.getItem(11).getItemMeta().getDisplayName());
        assertEquals(Material.GOLD_NUGGET, upgradeMenu.getItem(11).getType());
        assertEquals("§6Patent Slot Lvl. §c0", upgradeMenu.getItem(12).getItemMeta().getDisplayName());
        assertEquals(Material.END_CRYSTAL, upgradeMenu.getItem(12).getType());
        assertEquals("§6Patent Upgrade Slot Lvl. §c0", upgradeMenu.getItem(13).getItemMeta().getDisplayName());
        assertEquals(Material.ITEM_FRAME, upgradeMenu.getItem(13).getType());
        assertEquals("§6Craft Limit Lvl. §c0", upgradeMenu.getItem(14).getItemMeta().getDisplayName());
        assertEquals(Material.CRAFTING_TABLE, upgradeMenu.getItem(14).getType());
        assertEquals("§6Invest Return Time Lvl. §c0", upgradeMenu.getItem(15).getItemMeta().getDisplayName());
        assertEquals(Material.EMERALD, upgradeMenu.getItem(15).getType());

        assertEquals(0, comp.upgrades.get(0).level);

        /*
        * Upgrade first option
        * */
        /*
        * Give company enough spendable
        * */
        comp.add_bal(20000000.0);

        testPlayer.simulateInventoryClick(upgradeMenu, 11);
        assertEquals(1, comp.upgrades.get(0).level);

        printInventory(upgradeMenu);
    }


}
