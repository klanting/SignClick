package com.dynmap;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.TestTools;

import static groovy.test.GroovyTestCase.assertEquals;

public class DynmapTax {
    private ServerMock server;
    private SignClick plugin;
    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        Market.clear();
    }

    @Test
    public void taxDynmap(){

        SignClick.getEconomy().depositPlayer(testPlayer, 1000);
        SignClick.getDynmap().setPlayerVisiblity(testPlayer, false);
        assertEquals(1000, SignClick.getEconomy().getBalance(testPlayer));

        server.getScheduler().performTicks(60*10*20L+1);

        assertEquals(0, SignClick.getEconomy().getBalance(testPlayer));

    }

    @Test
    public void taxDynmapCountries(){
        /*
        * Do tax when a country exists
        * */
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        CountryManager.create("empire1", testPlayer2);
        Country country = CountryManager.getCountry("empire1");

        SignClick.getEconomy().depositPlayer(testPlayer, 1000);
        SignClick.getDynmap().setPlayerVisiblity(testPlayer, false);
        assertEquals(1000, SignClick.getEconomy().getBalance(testPlayer));
        assertEquals(0, country.getBalance());

        server.getScheduler().performTicks(60*10*20L+1);

        assertEquals(0, SignClick.getEconomy().getBalance(testPlayer));
        assertEquals(1000, country.getBalance());

    }

    @Test
    public void taxDynmapNotEnoughMoney(){
        SignClick.getEconomy().depositPlayer(testPlayer, 1);
        SignClick.getDynmap().setPlayerVisiblity(testPlayer, false);

        server.getScheduler().performTicks(60*10*20L+1);

        testPlayer.assertSaid("§cYou couldn't pay the money, so now you are visible on the dynmap");
        testPlayer.assertNoMoreSaid();

        assertEquals(1, SignClick.getEconomy().getBalance(testPlayer));
    }
}
