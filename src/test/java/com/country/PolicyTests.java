package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.economy.Country;

import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;


class PolicyTests {

    private ServerMock server;
    private SignClick plugin;
    private Player testPlayer;

    private Country country;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        /*Create country*/
        testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);

        country = CountryManager.create("empire1", testPlayer);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        CountryManager.clear();
    }

    @Test
    void setPolicyEconomics(){
        /*
        * Test policy changes
        * */

        assertEquals(70.0, country.getStability());

        country.setPoliciesReal(0, 2, 3);
        assertEquals(69.0, country.getStability());

        country.setPoliciesReal(0, 3, 1);
        assertEquals(71.0, country.getStability());

    }

    @Test
    void policyMakeDecision(){
        /*
        * Check that a decision is created to decide weather or not to follow a certain policy
        * */

        assertEquals(0, country.getDecisions().size());
        assertTrue(country.setPolicies(0, 3));
        assertEquals(1, country.getDecisions().size());

        assertTrue(country.setPolicies(1, 3));
        assertEquals(2, country.getDecisions().size());

        /*
        * require missing capital
        * */
        assertFalse(country.setPolicies(4, 3));
        assertEquals(2, country.getDecisions().size());

        country.deposit(5000000);
        assertTrue(country.setPolicies(4, 3));
        assertEquals(3, country.getDecisions().size());
    }

    @Test
    void policySaveAndRestore(){
        policyMakeDecision();

        plugin = TestTools.reboot(server);

        Country country = CountryManager.getCountry("empire1");
        assertEquals(3, country.getDecisions().size());
    }
}