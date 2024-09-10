package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.plugin.PluginManagerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.Economy.Market;
import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.milkbowl.vault.economy.Economy;
import tools.MockEconomy;

import static org.junit.jupiter.api.Assertions.*;



class CompanyTests {


    private ServerMock server;
    private SignClick plugin;
    private MockEconomy mockEconomy;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = MockBukkit.load(SignClick.class);

        PluginManagerMock pluginManager = server.getPluginManager();

        // Mock the Vault plugin
        Plugin vault = MockBukkit.createMockPlugin("Vault");
        pluginManager.enablePlugin(vault);
        mockEconomy = new MockEconomy();

        // add Mock Vault to server
        server.getServicesManager().register(Economy.class, mockEconomy, vault, org.bukkit.plugin.ServicePriority.Highest);

        plugin.onEnable();
        assertNotNull(SignClick.getEconomy());
        assertNotNull(plugin.getServer().getPluginManager().getPlugin("Vault"));
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Market.clear();
    }

    @Test
    void companyCreate(){
        Player testPlayer = server.addPlayer();

        /*
        * Give player 40 million
        * */
        mockEconomy.depositPlayer(testPlayer, 40000000);
        assertTrue(mockEconomy.has(testPlayer, 40000000));

        Boolean succes = Market.add_business("TestCaseInc", "TCI", Market.get_account(testPlayer));
        assertTrue(succes);
        mockEconomy.withdrawPlayer(testPlayer, 40000000);

        Company comp = Market.get_business("TCI");
        assertEquals(0, comp.get_value());
        assertEquals(1000000, Market.get_account(testPlayer).shares.get("TCI"));

    }

    @Test
    void companyAddMoney(){
        Player testPlayer = server.addPlayer();

        Boolean succes = Market.add_business("TestCaseInc", "TCI", Market.get_account(testPlayer));
        assertTrue(succes);


        Company comp = Market.get_business("TCI");

    }
}

