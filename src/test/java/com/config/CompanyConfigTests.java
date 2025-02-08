package com.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.companyPatent.Auction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;
import static org.junit.jupiter.api.Assertions.*;

public class CompanyConfigTests {
    private ServerMock server;
    private SignClick plugin;

    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
    }

    @Test
    void companyCreateNoConfirm(){

        plugin.getConfig().set("companyConfirmation", false);

        /*
        * Check that we can bypass company confirmation
        * */

        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§byou succesfully found TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyCreateCheaper(){

        plugin.getConfig().set("companyConfirmation", false);
        plugin.getConfig().set("companyCreateCost", 30_000_000.0);

        /*
         * Check that we can create a company cheaper
         * */

        SignClick.getEconomy().depositPlayer(testPlayer, 30_000_000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§byou succesfully found TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyCreateLessShares(){

        plugin.getConfig().set("companyConfirmation", false);
        plugin.getConfig().set("companyStartShares", 1000);

        /*
         * Check that we can create a company cheaper
         * */

        SignClick.getEconomy().depositPlayer(testPlayer, 40_000_000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§byou succesfully found TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();

        Company comp = Market.getCompany("COMP");
        assertEquals(1000, comp.getTotalShares());
        assertEquals(1000, Market.getAccount(testPlayer).shares.get("COMP"));
    }

    @Test
    void auctionUpdate(){

        plugin.getConfig().set("companyConfirmation", false);
        plugin.getConfig().set("companyStartShares", 1000);
        plugin.getConfig().set("auctionCycle", 360);

        plugin = TestTools.reboot(server);

        Auction auction = Auction.getInstance();

        PlayerMock testPlayer = server.addPlayer();

        Market.addCompany("A", "A", Market.getAccount(testPlayer));
        auction.setBit(0, 100, "A");
        assertEquals(100, auction.getBit(0));

        /*
        * First Bid
        * */
        server.getScheduler().performTicks(360*20L+1);
        assertNotEquals(100, auction.getBit(0));

        auction.setBit(0, 100, "A");
        assertEquals(100, auction.getBit(0));

        /*
         * Second Bid
         * */
        server.getScheduler().performTicks(360*20L+1);
        assertNotEquals(100, auction.getBit(0));

        auction.setBit(0, 100, "A");
        assertEquals(100, auction.getBit(0));
        /*
         * Third Bid
         * */

        server.getScheduler().performTicks(180*20L+1);
        assertEquals(100, auction.getBit(0));
        /*
         * Save Data
         * */

        plugin = TestTools.reboot(server);
        auction = Auction.getInstance();
        assertEquals(100, auction.getBit(0));


        server.getScheduler().performTicks(180*20L+1);
        assertNotEquals(100, auction.getBit(0));
    }
}
