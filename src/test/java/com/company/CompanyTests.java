package com.company;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.Account;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Machine;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.utils.BlockPosKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;



class CompanyTests {


    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

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

        CompanyI comp = Market.getCompany("TCI");
        assertEquals(0, comp.getValue());
        assertEquals(1000, Market.getAccount(testPlayer).shares.get("TCI"));

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
        plugin = TestTools.reboot(server);

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


        CompanyI comp = Market.getCompany("TCI");
        comp.addBal(100.0);

        assertEquals(100.0, comp.getBal());

    }

    @Test
    void companySaveLoad(){
        Player testPlayer = server.addPlayer();

        Boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(succes);

        CompanyI comp = Market.getCompany("TCI");
        comp.addBal(1000.0);
        assertEquals(1000.0, comp.getBal());

        plugin = TestTools.reboot(server);

        comp = Market.getCompany("TCI");
        assertNotNull(comp);

        assertEquals(1000.0, comp.getBal());

    }

    @Test
    void companyRunTicks(){
        Player testPlayer = server.addPlayer();

        Boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(succes);

        CompanyI comp = Market.getCompany("TCI");
        comp.addBal(1000.0);

        server.getScheduler().performTicks(60*60*24*7*20+1);

    }

    @Test
    void companyContractServerToCompany(){
        PlayerMock testPlayer = server.addPlayer();

        boolean succes = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(succes);

        CompanyI comp = Market.getCompany("TCI");
        comp.addBal(1000000000.0);
        comp.setSpendable(1000000000.0);
        comp.doUpgrade(0);

        Market.getContracts("TCI", testPlayer);

        testPlayer.assertSaid("""
                §aincome:§0
                §aContract: from SERVER(S) to TCI(C) amount: 500,0 for 10 weeks, reason: Upgrade[0] 1 delay: 0§0
                §coutgoing:""");

        testPlayer.assertNoMoreSaid();

        plugin = TestTools.reboot(server);

        Market.getContracts("TCI", testPlayer);

        testPlayer.assertSaid("""
                §aincome:§0
                §aContract: from SERVER(S) to TCI(C) amount: 500,0 for 10 weeks, reason: Upgrade[0] 1 delay: 0§0
                §coutgoing:""");
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

    @Test
    void companyCalculateCountry(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        CountryManager.create("empire1", testPlayer);
        CountryManager.create("empire2", testPlayer2);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        CompanyI company = Market.getCompany("TCI");
        company.calculateCountry();
        assertEquals("empire1", company.getCountry());

        company.getCOM().changeShareHolder(Market.getAccount(testPlayer), -900000);
        company.getCOM().changeShareHolder(Market.getAccount(testPlayer2), 900000);
        company.calculateCountry();

        assertEquals("empire2", company.getCountry());
    }

    @Test
    void companyBalanceLargeValue(){
        /*
        * Check that the balance keeps working fine for large values
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        CompanyI company = Market.getCompany("TCI");
        company.addBal(Math.pow(2, 65));
        assertTrue(company.getBal() > 0);
    }

    @Test
    void companyOpenTradeBug(){
        /*
        * Do the following scenario
        * 1. set comp open trade
        * 2. buy shares
        * 3. buy shares
        * 4. disable open trade
        * 5. enable open trade
        * Now each time I buy shares, it gets cheaper
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        CompanyI company = Market.getCompany("TCI");
        company.addBal(4000.0);
        company.getCOM().setOpenTrade(true);

        SignClick.getEconomy().depositPlayer(testPlayer, 1000.0);

        double amount1;
        double amount2;
        double amount3;

        Account acc =  Market.getAccount(testPlayer);

        /*
         * Check second buy more expensive
         * */
        amount1 = SignClick.getEconomy().getBalance(testPlayer);
        acc.buyShare("TCI", 10, testPlayer);
        amount2 = SignClick.getEconomy().getBalance(testPlayer);
        acc.buyShare("TCI", 10, testPlayer);
        amount3 = SignClick.getEconomy().getBalance(testPlayer);

        double dif1 = (amount2 - amount1)*-1;
        double dif2 = (amount3 - amount2)*-1;

        assertTrue(dif1<dif2);

        /*
        * Change the open trade
        * */
        company.getCOM().setOpenTrade(false);
        company.getCOM().setOpenTrade(true);

        /*
        * Check second buy more expensive
        * */
        amount1 = SignClick.getEconomy().getBalance(testPlayer);
        acc.buyShare("TCI", 10, testPlayer);
        amount2 = SignClick.getEconomy().getBalance(testPlayer);
        acc.buyShare("TCI", 10, testPlayer);
        amount3 = SignClick.getEconomy().getBalance(testPlayer);

        dif1 = (amount2 - amount1)*-1;
        dif2 = (amount3 - amount2)*-1;

        assertTrue(dif1<dif2);
    }

    @Test
    void companyBuyPriceMoreThanSell(){
        /*
        * Check that the buy price is more than the sell price
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        SignClick.getEconomy().depositPlayer(testPlayer, 1000.0);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        CompanyI company = Market.getCompany("TCI");
        company.addBal(4000.0);
        company.getCOM().setOpenTrade(true);

        Account acc =  Market.getAccount(testPlayer);

        double val1 = Market.getBuyPrice("TCI", 100);
        acc.buyShare("TCI", 100, testPlayer);
        double val2 = Market.getSellPrice("TCI", 100);

        assertTrue(val1 > val2);
    }

    @Test
    void companyChiefBug(){
        /*
        * let a player start as CEO of a company
        * Now vote on itself as CFO, this player should remain just CEO, but in the bug he/she also becomes CFO
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        SignClick.getEconomy().depositPlayer(testPlayer, 1000.0);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        CompanyI company = Market.getCompany("TCI");

        assertEquals(testPlayer.getUniqueId(), company.getCOM().getBoard().getChief("CEO"));

        company.getCOM().getBoard().boardChiefVote(testPlayer.getUniqueId(), "CFO", testPlayer.getUniqueId());

        assertEquals(testPlayer.getUniqueId(), company.getCOM().getBoard().getChief("CEO"));
        assertEquals(null, company.getCOM().getBoard().getChief("CFO"));

    }

    @Test
    void companyStoreMachinesBug(){
        /*
        * when having multiple machines, the storing only returns 1 machine
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        SignClick.getEconomy().depositPlayer(testPlayer, 1000.0);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        CompanyI company = Market.getCompany("TCI");

        /*
        * create machine 1
        * */
        company.getMachines().put(BlockPosKey.from(new Location(server.addSimpleWorld("world"), 0, 0, 0)),
                new Machine(null, company));

        /*
         * create machine 2
         * */
        company.getMachines().put(BlockPosKey.from(new Location(server.addSimpleWorld("world"), 1, 0, 0)),
                new Machine(null, company));

        assertEquals(2, company.getMachines().size());

        plugin = TestTools.reboot(server);

        company = Market.getCompany("TCI");
        assertEquals(2, company.getMachines().size());
    }

    @Test
    void companySellMoreThenBuyBug(){
        /*
        * In the following scenario selling shares is worth more than buying:
        * buy a lot of shares,
        * spend a lot of the shareBal money,
        * now sell the shares
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        SignClick.getEconomy().depositPlayer(testPlayer, 10000000.0);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        /*
        * set open market
        * */
        CompanyI company = Market.getCompany("TCI");
        company.addBal(20);
        company.getCOM().setOpenTrade(true);

        /*
        * buy shares
        * */
        Market.getAccount(testPlayer).buyShare("TCI", 2000, testPlayer);
        /*
        * spend money
        * */

        double toSpend = company.getShareBalance();
        company.removeBal(toSpend, true);

        assertTrue(company.getShareBalance() > 0);
        assertTrue(company.getBal() > 0);

        /*
        * ensure we cannot sell for more than the value
        * */
        assertTrue(company.getValue() >= Market.getSellPrice("TCI", 3000));

    }

    @Test
    void valueLossTest(){
        /*
        * Ensure that we do not lose too much value after selling
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        /*Ensure tax goes to country*/
        CountryManager.create("TCI", testPlayer);

        SignClick.getEconomy().depositPlayer(testPlayer, 10000000.0);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

        CompanyI company = Market.getCompany("TCI");
        company.addBal(500);
        company.addShareBal(500.0);

        /*
        * sell 500 shares and ensure we still have 40% of the value, which is sufficient
        * should be less than 50% because the top 500 shares should have the most value
        * */
        Market.getAccount(testPlayer.getUniqueId()).sellShare("TCI", 500, testPlayer);
        assertTrue(company.getValue() <= 500);
        assertTrue(company.getValue() >= 400);
    }

}

