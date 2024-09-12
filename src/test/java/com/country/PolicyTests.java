package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.plugin.PluginManagerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.SignClick;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.MockDynmap;
import tools.MockEconomy;
import tools.TestTools;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class PolicyTests {

    private ServerMock server;
    private SignClick plugin;
    private Player testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        /*Create country*/
        testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);
        Banking.create("empire1", testPlayer);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Banking.clear();
    }

    @Test
    void setPolicyEconomics(){
        /*
        * Test policy changes
        * */

        assertEquals(70.0, Banking.getStability("empire1"));

        Banking.setPoliciesReal("empire1", 0, 2, 3);
        assertEquals(69.0, Banking.getStability("empire1"));

        Banking.setPoliciesReal("empire1", 0, 3, 1);
        assertEquals(71.0, Banking.getStability("empire1"));

    }

    @Test
    void policyMakeDecision(){
        /*
        * Check that a decision is created to decide weather or not to follow a certain policy
        * */

        assertEquals(0, Banking.decisions.getOrDefault("empire1", new ArrayList<>()).size());
        Banking.setPolicies("empire1", 0, 3);
        assertEquals(1, Banking.decisions.getOrDefault("empire1", new ArrayList<>()).size());

        Banking.setPolicies("empire1", 1, 3);
        assertEquals(2, Banking.decisions.getOrDefault("empire1", new ArrayList<>()).size());
    }
}