package com.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.routines.WeeklyPay;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.SignClick;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;
import static org.gradle.internal.impldep.org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeeklyPayCTests {
    private ServerMock server;
    private SignClick plugin;
    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
        WeeklyPay.payments.clear();
    }

    @Test
    public void makeWeeklyPay(){
        PlayerMock testPlayer2 = server.addPlayer();
        SignClick.getEconomy().depositPlayer(testPlayer, 1000);

        assertEquals(1000, Math.round(SignClick.getEconomy().getBalance(testPlayer)));
        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer2)));

        boolean suc6 = server.execute("weeklypay", testPlayer, "pay", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        assertEquals(1000, Math.round(SignClick.getEconomy().getBalance(testPlayer)));

        testPlayer.assertSaid("§byou succesfully started your weekly pay");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("weeklypay", testPlayer, "list").hasSucceeded();
        assertTrue(suc6);

        assertEquals("§bincoming: \n" +
                "outgoing: \n" +
                "§cPlayer1: §71000", testPlayer.nextMessage());

        testPlayer.assertNoMoreSaid();

        testPlayer2.nextMessage();

        /*
        check testPlayer2 is a receiver of money
        * */
        assertNotNull(WeeklyPay.receivers(testPlayer));
        assertEquals(1, WeeklyPay.receivers(testPlayer).size());
        assertEquals(testPlayer2.getName(), WeeklyPay.receivers(testPlayer).get(0));

        /*
        * look at weekly list of other user
        * */
        suc6 = server.execute("weeklypay", testPlayer2, "list", testPlayer.getName()).hasSucceeded();
        assertTrue(suc6);

        assertEquals("§bincoming: \n" +
                "outgoing: \n" +
                "§cPlayer1: §71000", testPlayer2.nextMessage());

        testPlayer2.assertNoMoreSaid();

        assertEquals(1000, Math.round(SignClick.getEconomy().getBalance(testPlayer)));

        server.getScheduler().performTicks(60*60*24*7*20+1);

        /*
        * check payment occurred
        * */
        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer)));
        assertEquals(1000, Math.round(SignClick.getEconomy().getBalance(testPlayer2)));

        assertEquals("§c[weeklypay] you weekly paid 1000 toPlayer1", testPlayer.nextMessage());
        testPlayer.assertNoMoreSaid();
    }

    @Test
    public void makeWeeklyPayNotEnoughMoney(){
        PlayerMock testPlayer2 = server.addPlayer();

        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer)));
        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer2)));

        boolean suc6 = server.execute("weeklypay", testPlayer, "pay", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer)));

        testPlayer.assertSaid("§byou succesfully started your weekly pay");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("weeklypay", testPlayer, "list").hasSucceeded();
        assertTrue(suc6);

        assertEquals("§bincoming: \n" +
                "outgoing: \n" +
                "§cPlayer1: §71000", testPlayer.nextMessage());

        testPlayer.assertNoMoreSaid();

        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer)));

        server.getScheduler().performTicks(60*60*24*7*20+1);

        /*
         * check payment occurred
         * */
        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer)));
        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer2)));

        assertEquals("§bweekly payment has been cancelled", testPlayer.nextMessage());
        testPlayer.assertNoMoreSaid();
    }

    @Test
    public void weeklyPaySaveRestore(){

        PlayerMock testPlayer2 = server.addPlayer();
        SignClick.getEconomy().depositPlayer(testPlayer, 1000);

        assertEquals(1000, Math.round(SignClick.getEconomy().getBalance(testPlayer)));
        assertEquals(0, Math.round(SignClick.getEconomy().getBalance(testPlayer2)));

        boolean suc6 = server.execute("weeklypay", testPlayer, "pay", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        assertEquals(1, WeeklyPay.payments.size());
        plugin = TestTools.reboot(server);

        assertEquals(1, WeeklyPay.payments.size());


    }
}
