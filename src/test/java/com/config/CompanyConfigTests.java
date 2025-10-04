package com.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.producible.Product;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.companyLogic.patent.Auction;
import org.bukkit.Material;
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

        SignClick.getConfigManager().getConfig("companies.yml").set("companyConfirmation", false);
        SignClick.getConfigManager().save();

        /*
        * Check that we can bypass company confirmation
        * */

        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);
        testPlayer.simulateInventoryClick(6);

        testPlayer.assertSaid("§byou succesfully founded TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyCreateCheaper(){

        SignClick.getConfigManager().getConfig("companies.yml").set("companyConfirmation", false);
        SignClick.getConfigManager().getConfig("companies.yml").set("companyCreateCost", 30_000_000.0);
        SignClick.getConfigManager().save();

        /*
         * Check that we can create a company cheaper
         * */

        SignClick.getEconomy().depositPlayer(testPlayer, 30_000_000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);
        testPlayer.simulateInventoryClick(6);

        testPlayer.assertSaid("§byou succesfully founded TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyCreateLessShares(){

        SignClick.getConfigManager().getConfig("companies.yml").set("companyConfirmation", false);
        SignClick.getConfigManager().getConfig("companies.yml").set("companyStartShares", 1000);
        SignClick.getConfigManager().save();

        /*
         * Check that we can create a company cheaper
         * */

        SignClick.getEconomy().depositPlayer(testPlayer, 40_000_000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);
        testPlayer.simulateInventoryClick(6);

        testPlayer.assertSaid("§byou succesfully founded TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();

        CompanyI comp = Market.getCompany("COMP");
        assertEquals(1000, comp.getTotalShares());
        assertEquals(1000, Market.getAccount(testPlayer).shares.get("COMP"));
    }

    @Test
    void auctionUpdate(){

        SignClick.getConfigManager().getConfig("companies.yml").set("companyConfirmation", false);
        SignClick.getConfigManager().getConfig("companies.yml").set("companyStartShares", 1000);
        SignClick.getConfigManager().getConfig("companies.yml").set("auctionCycle", 360);
        SignClick.getConfigManager().save();

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

    @Test
    void productPriceChange(){
        /*
        * Check that product production cost changes to the value in the config
        * */
        Market.addCompany("A", "A", Market.getAccount(testPlayer));
        CompanyI company = Market.getCompany("A");

        /*
        * Add torch with default price 5 and production time 30s
        * */
        company.addProduct(new Product(Material.TORCH, 5, 30, company));

        /*
        * change cost to 1
        * */
        SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection("Miscellaneous").getConfigurationSection("TORCH").set("productionCost", 1);
        SignClick.getConfigManager().save();
        plugin = TestTools.reboot(server);

        /*
        * check product price
        * */
        company = Market.getCompany("A");
        assertEquals(1, company.getProducts().size());
        assertEquals(1, company.getProducts().get(0).getPrice());
        assertEquals(30, company.getProducts().get(0).getProductionTime());

        /*
         * change production time to 10
         * */
        SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection("Miscellaneous").getConfigurationSection("TORCH").set("productionTime", 10);
        SignClick.getConfigManager().save();
        plugin = TestTools.reboot(server);

        /*
         * check product time
         * */
        company = Market.getCompany("A");
        assertEquals(1, company.getProducts().size());
        assertEquals(1, company.getProducts().get(0).getPrice());
        assertEquals(10, company.getProducts().get(0).getProductionTime());

        /*
        * Remove just the price from the config, so it resorts to latest default
        * */
        SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection("Miscellaneous").getConfigurationSection("TORCH").set("productionCost", null);
        SignClick.getConfigManager().save();
        plugin = TestTools.reboot(server);

        /*
         * check product time
         * */
        company = Market.getCompany("A");
        assertEquals(1, company.getProducts().size());

        assertNotEquals(null, company.getProducts().get(0).getPrice());
        assertNotEquals(0.0, company.getProducts().get(0).getPrice());

        assertEquals(10, company.getProducts().get(0).getProductionTime());
    }
}
