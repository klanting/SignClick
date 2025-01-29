package com.company;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;



class CompanyTests {


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
        Market.clear();
    }

    @Test
    void companyCreate(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*
        * Give player 40 million
        * */

        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        assertTrue(SignClick.getEconomy().has(testPlayer, 40000000));

        Boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(succes);
        SignClick.getEconomy().withdrawPlayer(testPlayer, 40000000);

        Company comp = Market.getCompany("TCI");
        assertEquals(0, comp.getValue());
        assertEquals(1000000, Market.getAccount(testPlayer).shares.get("TCI"));

    }

    @Test
    void companyCreateDuplicate(){
        companyCreate();

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*
         * Verify that the company is not overridden by uniquely identifying the object
         * */
        Market.getCompany("TCI").addBal(10.0);
        assertEquals(10.0, Market.getCompany("TCI").getValue());

        /*
        * Create duplicate company
        * */
        boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertFalse(succes);

        assertEquals(10.0, Market.getCompany("TCI").getValue());
    }

    @Test
    void companyDuplicateAfterReload(){
        companyCreate();

        /*
        * Verify that the company is not overridden by uniquely identifying the object
        * */
        Market.getCompany("TCI").addBal(10.0);
        assertEquals(10.0, Market.getCompany("TCI").getValue());

        /*
        * Restart server
        * */
        plugin.onDisable();
        CountryManager.clear();
        Market.clear();
        plugin = TestTools.setupPlugin(server);

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);


        /*
         * Create duplicate company
         * */
        boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertFalse(succes);
        assertEquals(10.0, Market.getCompany("TCI").getValue());

        /*
         * Create duplicate company with different Sname, but same namebus
         * */
        succes = Market.addCompany("TestCaseInc", "TCI2", Market.getAccount(testPlayer));
        assertFalse(succes);
        assertEquals(10.0, Market.getCompany("TCI").getValue());

    }

    @Test
    void companyAddMoney(){
        Player testPlayer = server.addPlayer();

        Boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(succes);


        Company comp = Market.getCompany("TCI");
        comp.addBal(100.0);

        assertEquals(100.0, comp.getBal());

    }

    @Test
    void companySaveLoad(){
        Player testPlayer = server.addPlayer();

        Boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(succes);

        Company comp = Market.getCompany("TCI");
        comp.addBal(1000.0);

        plugin.onDisable();
        CountryManager.clear();
        Market.clear();
        plugin = TestTools.setupPlugin(server);

        comp = Market.getCompany("TCI");
        assertNotNull(comp);
        assertEquals(1000.0, comp.getBal());

    }

    @Test
    void companyRunTicks(){
        Player testPlayer = server.addPlayer();

        Boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(succes);

        Company comp = Market.getCompany("TCI");
        comp.addBal(1000.0);

        server.getScheduler().performTicks(60*60*24*7*20+1);

    }

    @Test
    void companyContractServerToCompany(){
        PlayerMock testPlayer = server.addPlayer();

        boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(succes);

        Company comp = Market.getCompany("TCI");
        comp.addBal(1000000000.0);
        comp.doUpgrade(0);

        Market.getContracts("TCI", testPlayer);

        testPlayer.assertSaid("§aincome:");
        testPlayer.assertSaid("§aContract: from SERVER (S) to TCI(C) amount: 200000.0 for 10 weeks, reason: Upgrade[0] 1 delay: 0");
        testPlayer.assertSaid("§coutgoing:");
        testPlayer.assertNoMoreSaid();

        plugin.onDisable();
        CountryManager.clear();
        Market.clear();
        plugin = TestTools.setupPlugin(server);

        Market.getContracts("TCI", testPlayer);

        testPlayer.assertSaid("§aincome:");
        testPlayer.assertSaid("§aContract: from SERVER (S) to TCI(C) amount: 200000.0 for 10 weeks, reason: Upgrade[0] 1 delay: 0");
        testPlayer.assertSaid("§coutgoing:");
        testPlayer.assertNoMoreSaid();

    }

    @Test
    void companyGetCompanies(){
        PlayerMock testPlayer = server.addPlayer();

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        suc6 = Market.addCompany("TestCaseInc2", "TCI2", Market.getAccount(testPlayer));
        assertTrue(suc6);

        suc6 = Market.addCompany("TestCaseInc3", "TCI3", Market.getAccount(testPlayer));
        assertTrue(suc6);

        assertEquals(3, Market.getBusinesses().size());
        assertEquals("TCI", Market.getBusinesses().get(0));
        assertEquals("TCI3", Market.getBusinesses().get(1));
        assertEquals("TCI2", Market.getBusinesses().get(2));
    }


}

