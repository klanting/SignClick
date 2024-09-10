package com.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Market;
import com.klanting.signclick.Menus.CompanySelector;
import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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


class CompanyCTests {

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
        Market.clear();
    }

    @Test
    void companyMenu(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.add_business("TestCaseInc", "TCI", Market.get_account(testPlayer));
        assertTrue(suc6);

        suc6 = server.execute("company", testPlayer, "menu").hasSucceeded();
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
}
