package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.Economy.CountryDep;
import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        CountryDep.create("empire1", testPlayer);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryDep.clear();
    }

    @Test
    void setPolicyEconomics(){
        /*
        * Test policy changes
        * */

        assertEquals(70.0, CountryDep.getStability("empire1"));

        CountryDep.setPoliciesReal("empire1", 0, 2, 3);
        assertEquals(69.0, CountryDep.getStability("empire1"));

        CountryDep.setPoliciesReal("empire1", 0, 3, 1);
        assertEquals(71.0, CountryDep.getStability("empire1"));

    }

    @Test
    void policyMakeDecision(){
        /*
        * Check that a decision is created to decide weather or not to follow a certain policy
        * */

        assertEquals(0, CountryDep.decisions.getOrDefault("empire1", new ArrayList<>()).size());
        assertTrue(CountryDep.setPolicies("empire1", 0, 3));
        assertEquals(1, CountryDep.decisions.getOrDefault("empire1", new ArrayList<>()).size());

        assertTrue(CountryDep.setPolicies("empire1", 1, 3));
        assertEquals(2, CountryDep.decisions.getOrDefault("empire1", new ArrayList<>()).size());

        assertFalse(CountryDep.setPolicies("empire1", 4, 3));
        assertEquals(2, CountryDep.decisions.getOrDefault("empire1", new ArrayList<>()).size());

        CountryDep.deposit("empire1", 5000000);
        assertTrue(CountryDep.setPolicies("empire1", 4, 3));
        assertEquals(3, CountryDep.decisions.getOrDefault("empire1", new ArrayList<>()).size());
    }

    @Test
    void policySaveAndRestore(){
        policyMakeDecision();

        plugin.onDisable();
        CountryDep.clear();
        plugin = TestTools.setupPlugin(server);

        assertEquals(3, CountryDep.decisions.getOrDefault("empire1", new ArrayList<>()).size());
    }
}