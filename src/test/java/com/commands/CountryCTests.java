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


class CountryCTests {


    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        PluginManagerMock pluginManager = server.getPluginManager();

        //dynmap mock
        Plugin dynmap = MockBukkit.createMockPlugin("dynmap");
        pluginManager.enablePlugin(dynmap);

        // Mock the Vault plugin
        Plugin vault = MockBukkit.createMockPlugin("Vault");
        pluginManager.enablePlugin(vault);
        MockEconomy mockEconomy = new MockEconomy();
        MockDynmap mockDynmap = new MockDynmap();

        // add Mock Vault to server
        server.getServicesManager().register(Economy.class, mockEconomy, vault, org.bukkit.plugin.ServicePriority.Highest);
        server.getServicesManager().register(DynmapAPI.class, mockDynmap, vault, org.bukkit.plugin.ServicePriority.Highest);

        plugin = MockBukkit.load(SignClick.class);

        assertNotNull(SignClick.getEconomy());
        assertNotNull(plugin.getServer().getPluginManager().getPlugin("Vault"));
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Banking.clear();
    }

    @Test
    void createCountry(){

        PlayerMock testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);

        boolean result = server.execute("country", testPlayer, "create", "empire1", testPlayer.getName()).hasSucceeded();
        assertTrue(result);

        assertEquals("empire1", Banking.Element(testPlayer));


    }
}
