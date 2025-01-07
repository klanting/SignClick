package com.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.SignClick;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tools.TestTools;


import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



class CompanyCTests {


    private ServerMock server;
    private SignClick plugin;

    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addBusiness("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
    }

    @Test
    void companyCreate(){
        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm that you want to start a company and want to auto-transfer §640 million §bto your business from your account If you agree, enter: §c/company create TESTINGCOMP COMP");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§byou succesfully found TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();


    }

    @Test
    void companyPay(){
        Company company = Market.getBusiness("TCI");
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        assertNotNull(company);
        company.addBal(100000.0);

        boolean suc6 = server.execute("company", testPlayer, "pay",
                "TCI", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.nextMessage();

        suc6 = server.execute("company", testPlayer, "pay",
                "TCI", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bsuccesfully paid §fPlayer1 1000.0");
        testPlayer.assertNoMoreSaid();
        assertEquals(99000.0, company.getBal());


    }

    @Test
    void companyMenu(){

        boolean suc6 = server.execute("company", testPlayer, "menu").hasSucceeded();
        assertTrue(suc6);


        InventoryView inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);

        ItemStack companyOption = inventoryMenu.getItem(0);
        ItemStack companyOption2 = inventoryMenu.getItem(1);

        assertNotNull(companyOption);
        assertNull(companyOption2);

        /*
        * Check first item is a company
        * */
        String companyName = companyOption.getItemMeta().getDisplayName();
        assertEquals("TCI", companyName);
    }

    @Test
    void companyShareTop(){

        boolean suc6 = server.execute("company", testPlayer, "sharetop", "TCI").hasSucceeded();
        assertTrue(suc6);

        /*
        * Check correctly received sharetop
        * */
        testPlayer.assertSaid("§bsharetop:");
        testPlayer.assertSaid("§9Player0: §f1.000.000 (100,00%)");
        testPlayer.assertSaid("§eMarket: §f0 (0,00%)");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyShareTopInvalidCompany(){

        boolean suc6 = server.execute("company", testPlayer, "sharetop", "TCIS").hasSucceeded();
        assertTrue(suc6);

        /*
         * Check correctly received sharetop invalid message
         * */
        testPlayer.assertSaid("§bplease enter a valid company stockname");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyInfo(){

        boolean suc6 = server.execute("company", testPlayer, "info", "TCI").hasSucceeded();
        assertTrue(suc6);

        /*
         * Check correctly received info
         * */
        testPlayer.assertSaid("§bName: §7TestCaseInc\n" +
                "§bStockname: §7TCI\n" +
                "§bCEO: §7[Player0]\n" +
                "§bbal: §70\n" +
                "§bshares: §71.000.000\n" +
                "§bshareholders: §7[Player0]");


        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyInfoInvalidCompany(){

        boolean suc6 = server.execute("company", testPlayer, "info", "TCIS").hasSucceeded();
        assertTrue(suc6);

        /*
         * Check correctly received info warning
         * */
        testPlayer.assertSaid("§bplease enter a valid company stockname");

        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyGive(){

        SignClick.getEconomy().depositPlayer(testPlayer, 1000);

        Company comp = Market.getBusiness("TCI");
        assertEquals(0, comp.getValue());

        boolean suc6 = server.execute("company", testPlayer, "give", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                        "that you want to give §f1.000§b to §fTCI\n" +
                        "§c/company give TCI 1000.0");

        testPlayer.assertNoMoreSaid();

        /*
        * Confirmation
        * */
        suc6 = server.execute("company", testPlayer, "give", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§byou succesfully gave §f1.000§b to §fTCI");
        testPlayer.assertSaid("§byour business §fTCI §b received §f1000.0 §b from §fPlayer0");
        testPlayer.assertNoMoreSaid();

        assertEquals(1000, comp.getValue());

    }

    @Test
    void companySellBuyShares(){
        companyGive();
        Company comp = Market.getBusiness("TCI");

        assertEquals(1000000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));

        /*
        * Sell 1000 stocks
        * */
        boolean suc6 = server.execute("company", testPlayer, "sell", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to sell §f1000§b from §fTCI§b for a price of §63,42 \n" +
                "§c/company sell TCI 1000");
        testPlayer.assertNoMoreSaid();
        suc6 = server.execute("company", testPlayer, "sell", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bsell: §aaccepted");
        testPlayer.assertNoMoreSaid();

        assertEquals(999000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));

        assertEquals(1000, comp.getMarketShares());
        assertEquals(993, Math.round(comp.getValue()));

        /*
         * Buy 1000 stocks
         * */
        SignClick.getEconomy().depositPlayer(testPlayer, 500);

        suc6 = server.execute("company", testPlayer, "buy", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);
        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to buy §f1000§b from §fTCI for a price of §63,60 \n" +
                "§c/company buy TCI 1000");
        testPlayer.assertNoMoreSaid();
        suc6 = server.execute("company", testPlayer, "buy", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);
        testPlayer.assertSaid("§bbuy: §aaccepted");
        testPlayer.assertNoMoreSaid();

        assertEquals(1000000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(0, comp.getMarketShares());
        assertEquals(997, Math.round(comp.getValue()));

    }

    @Test
    void companyTransferShares(){

        PlayerMock testPlayer2 = server.addPlayer();

        assertEquals(1000000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(null, Market.getAccount(testPlayer2.getUniqueId()).shares.get("TCI"));

        boolean suc6 = server.execute("company", testPlayer, "transfer",
                "TCI", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to transfer §f1000§b shares to §fPlayer1\n" +
                "§c/company transfer TCI Player1 1000");
        testPlayer.assertNoMoreSaid();

        /*
        * confirm transfer
        * */
        suc6 = server.execute("company", testPlayer, "transfer",
                "TCI", testPlayer2.getName(), "1000").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§btransfer: §aaccepted");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§breceived: 1000 shares for TCI from Player0");
        testPlayer2.assertNoMoreSaid();

        assertEquals(999000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(1000, Market.getAccount(testPlayer2.getUniqueId()).shares.get("TCI"));
    }

    @Test
    void companyPortfolio(){

        boolean suc6 = server.execute("company", testPlayer, "portfolio").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bportfolio:\n");
        testPlayer.assertSaid("§b1. §3TCI: §70 (100,00%)\n");
        testPlayer.assertSaid("§9Total value: §e0");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyGetSupport(){

        boolean suc6 = server.execute("company", testPlayer, "get_support", "TCI", testPlayer.getName()).hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplayer supports §7Player0");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyNoParam(){
        boolean suc6 = server.execute("company", testPlayer).hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease enter /company <category>");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyWrongParam(){
        boolean suc6 = server.execute("company", testPlayer).hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease enter /company <category>");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyCreateWrongParamCount(){
        boolean suc6 = server.execute("company", testPlayer, "create", "missingStockName").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease enter /company create <name> <stockname>");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyBaltop(){
        /*
        * Test /company baltop command
        * */

        /*
        * Test when 1 company exists
        * */

        boolean suc6 = server.execute("company", testPlayer, "baltop").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§b1. §3TCI: §70\n");
        testPlayer.assertNoMoreSaid();

        /*
         * Test when 2 company exists
         * */

        Market.addBusiness("otherComp", "TCI2", Market.getAccount(testPlayer.getUniqueId()));
        Market.getBusiness("TCI2").addBal(100.0);

        suc6 = server.execute("company", testPlayer, "baltop").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§b1. §3TCI2: §7100\n");
        testPlayer.assertSaid("§b2. §3TCI: §70\n");
        testPlayer.assertNoMoreSaid();


    }

    @Test
    void createCompany2CompanyContract(){
        Country c = CountryManager.create("C", testPlayer);
        testPlayer.nextMessage();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addBusiness("TestCaseInc2",
                "TCI2", Market.getAccount(testPlayer2.getUniqueId()));
        assertTrue(suc6);


        Market.getBusiness("TCI").country = c;
        Market.getBusiness("TCI2").country = c;

        Market.getBusiness("TCI2").addBal(200000.0);

        suc6 = server.execute("company", testPlayer, "send_contract_ctc",
                "TCI", "TCI2", "100", "2", "I am cold").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to send a contract request to §fTCI2\n" +
                "§bfor an amount of §f100.0\n" +
                "§bfor a time of §f2 weeks \n" +
                "§c/company send_contract_ctc TCI TCI2 100.0 2");

        suc6 = server.execute("company", testPlayer, "send_contract_ctc",
                "TCI", "TCI2", "100", "2", "I am cold").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§b your company §7TCI2§b got a contract from §7TCI§b they " +
                "will ask you §7100.0§b for §72§b weeks, do §c/company sign_contract_ctc TCI2");
        testPlayer2.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer2, "sign_contract_ctc",
                "TCI2").hasSucceeded();
        assertTrue(suc6);
        testPlayer2.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to sign a contract (§cYOU PAY THEM§b) requested from §fTCI§b \n" +
                "for an amount of §f100§b \n" +
                "for a time of §f2 weeks \n" +
                "§c/company sign_contract_ctc TCI2");

        suc6 = server.execute("company", testPlayer2, "sign_contract_ctc",
                "TCI2").hasSucceeded();
        assertTrue(suc6);

        testPlayer2.assertSaid("§bcontract confirmed");

        testPlayer.assertNoMoreSaid();
        testPlayer2.assertNoMoreSaid();

        /*
        * Check 1 comp to comp exists
        * */
        assertEquals(1, Market.contractCompToComp.size());

        /*
        * Check valid transaction
        * */
        assertEquals("TCI2", Market.contractCompToComp.get(0).from());
        assertEquals("TCI", Market.contractCompToComp.get(0).to());
        assertEquals(100.0, Market.contractCompToComp.get(0).getAmount());
        assertEquals(2, Market.contractCompToComp.get(0).getWeeks());
        assertEquals("I am cold", Market.contractCompToComp.get(0).getReason());

        /*
        * Restart Server, check persistence
        * */
        plugin.onDisable();
        CountryManager.clear();
        Market.clear();
        plugin = TestTools.setupPlugin(server);

        /*
         * Check 1 comp to comp exists
         * */
        assertEquals(1, Market.contractCompToComp.size());

        /*
         * Check valid transaction
         * */
        assertEquals("TCI2", Market.contractCompToComp.get(0).from());
        assertEquals("TCI", Market.contractCompToComp.get(0).to());
        assertEquals(100.0, Market.contractCompToComp.get(0).getAmount());
        assertEquals(2, Market.contractCompToComp.get(0).getWeeks());
        assertEquals("I am cold", Market.contractCompToComp.get(0).getReason());

        server.getScheduler().performTicks(60*60*24*7*20L+1);

        /*
         * Check valid transaction, with 1 week less
         * */
        assertEquals(1, Market.contractCompToComp.size());
        assertEquals("TCI2", Market.contractCompToComp.get(0).from());
        assertEquals("TCI", Market.contractCompToComp.get(0).to());
        assertEquals(100.0, Market.contractCompToComp.get(0).getAmount());
        assertEquals(1, Market.contractCompToComp.get(0).getWeeks());
        assertEquals("I am cold", Market.contractCompToComp.get(0).getReason());

    }

    @Test
    void createCompany2PlayerContract(){

        Country c = CountryManager.create("C", testPlayer);
        testPlayer.nextMessage();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        Market.getBusiness("TCI").country = c;

        Market.getBusiness("TCI").addBal(200000.0);

        boolean suc6 = server.execute("company", testPlayer2, "send_contract_ctp",
                "TCI", "100", "2", "I am cold").hasSucceeded();
        assertTrue(suc6);



        testPlayer2.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to send a contract request to §fTCI§b \n" +
                " for an amount of §f100.0§b \n" +
                " for a time of §f2 weeks \n" +
                "§c/company send_contract_ctp TCI 100.0 2");
        testPlayer2.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer2, "send_contract_ctp",
                "TCI", "100", "2", "I am cold").hasSucceeded();
        assertTrue(suc6);

        testPlayer2.assertNoMoreSaid();


        testPlayer.assertSaid("§b your company §7TCI§b got a contract from §7Player1§b he/she will ask you " +
                "§7100.0§b for §72§b weeks, do §c/company sign_contract_ctp TCI");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "sign_contract_ctp",
                "TCI").hasSucceeded();
        assertTrue(suc6);



        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to sign a contract (§cYOU PAY THEM§b) requested from §fPlayer1§b \n" +
                "for an amount of §f100§b \n" +
                "for a time of §f2 weeks \n" +
                "§c/company sign_contract_ctp TCI");

        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "sign_contract_ctp",
                "TCI").hasSucceeded();
        assertTrue(suc6);


        testPlayer.assertSaid("§bcontract confirmed");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertNoMoreSaid();

        /*
         * Check 1 comp to comp exists
         * */
        assertEquals(1, Market.contractCompToPlayer.size());

        /*
         * Check valid transaction
         * */
        assertEquals("TCI", Market.contractCompToPlayer.get(0).from());
        assertEquals(testPlayer2.getName(), Market.contractCompToPlayer.get(0).to());
        assertEquals(100.0, Market.contractCompToPlayer.get(0).getAmount());
        assertEquals(2, Market.contractCompToPlayer.get(0).getWeeks());
        assertEquals("I am cold", Market.contractCompToPlayer.get(0).getReason());

        /*
         * Restart Server, check persistence
         * */
        plugin.onDisable();
        CountryManager.clear();
        Market.clear();
        plugin = TestTools.setupPlugin(server);

        /*
         * Check 1 comp to player exists
         * */
        assertEquals(1, Market.contractCompToPlayer.size());

        /*
         * Check valid transaction
         * */
        assertEquals("TCI", Market.contractCompToPlayer.get(0).from());
        assertEquals(testPlayer2.getName(), Market.contractCompToPlayer.get(0).to());
        assertEquals(100.0, Market.contractCompToPlayer.get(0).getAmount());
        assertEquals(2, Market.contractCompToPlayer.get(0).getWeeks());
        assertEquals("I am cold", Market.contractCompToPlayer.get(0).getReason());

        server.getScheduler().performTicks(60*60*24*7*20+1);

        /*
         * Check valid transaction, with 1 week less
         * */
        assertEquals(1, Market.contractCompToPlayer.size());
        assertEquals("TCI", Market.contractCompToPlayer.get(0).from());
        assertEquals(testPlayer2.getName(), Market.contractCompToPlayer.get(0).to());
        assertEquals(100.0, Market.contractCompToPlayer.get(0).getAmount());
        assertEquals(1, Market.contractCompToPlayer.get(0).getWeeks());
        assertEquals("I am cold", Market.contractCompToPlayer.get(0).getReason());

    }

    @Test
    void createPlayer2CompanyContract(){

        Country c = CountryManager.create("C", testPlayer);
        testPlayer.nextMessage();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        Market.getBusiness("TCI").country = c;

        Market.getBusiness("TCI").addBal(200000.0);

        /*
        * send ptc contract
        * */
        boolean suc6 = server.execute("company", testPlayer, "send_contract_ptc",
                "TCI", testPlayer2.getName(), "100", "2", "I am cold").hasSucceeded();
        assertTrue(suc6);
        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to send a contract request to §fPlayer1§b \n" +
                " for an amount of §f100.0§b \n" +
                " for a time of §f2 weeks \n" +
                "§c/company send_contract_ptc TCI Player1 100.0 2");
        testPlayer.assertNoMoreSaid();
        suc6 = server.execute("company", testPlayer, "send_contract_ptc",
                "TCI", testPlayer2.getName(), "100", "2", "I am cold").hasSucceeded();
        assertTrue(suc6);
        testPlayer2.assertSaid("§b you §7Player1§b got a contract request from §7TCI§b they will ask " +
                "you §7100.0§b for §72§b weeks, do §c/company sign_contract_ptc");
        testPlayer2.assertNoMoreSaid();

        SignClick.getEconomy().depositPlayer(testPlayer2, 1000);

        suc6 = server.execute("company", testPlayer2, "sign_contract_ptc",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer2.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to sign a contract (§cYOU PAY THEM§b) requested from §fTCI§b \n" +
                "for an amount of §f100§b \n" +
                "for a time of §f2 weeks \n" +
                "§c/company sign_contract_ptc");
        testPlayer2.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer2, "sign_contract_ptc",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer2.assertSaid("§bcontract confirmed");
        testPlayer2.assertNoMoreSaid();

        /*
         * Check 1 comp to comp exists
         * */

        assertEquals(1, Market.contractPlayerToComp.size());

        /*
         * Check valid transaction
         * */
        assertEquals(testPlayer2.getName(), Market.contractPlayerToComp.get(0).from());
        assertEquals("TCI", Market.contractPlayerToComp.get(0).to());
        assertEquals(100.0, Market.contractPlayerToComp.get(0).getAmount());
        assertEquals(2, Market.contractPlayerToComp.get(0).getWeeks());
        assertEquals("I am cold", Market.contractPlayerToComp.get(0).getReason());

        /*
         * Restart Server, check persistence
         * */
        plugin.onDisable();
        CountryManager.clear();
        Market.clear();
        plugin = TestTools.setupPlugin(server);

        /*
         * Check 1 comp to player exists
         * */
        assertEquals(1, Market.contractPlayerToComp.size());

        /*
         * Check valid transaction
         * */
        assertEquals(testPlayer2.getName(), Market.contractPlayerToComp.get(0).from());
        assertEquals("TCI", Market.contractPlayerToComp.get(0).to());
        assertEquals(100.0, Market.contractPlayerToComp.get(0).getAmount());
        assertEquals(2, Market.contractPlayerToComp.get(0).getWeeks());
        assertEquals("I am cold", Market.contractPlayerToComp.get(0).getReason());

        server.getScheduler().performTicks(60*60*24*7*20+1);

        /*
         * Check valid transaction, with 1 week less
         * */
        assertEquals(testPlayer2.getName(), Market.contractPlayerToComp.get(0).from());
        assertEquals("TCI", Market.contractPlayerToComp.get(0).to());
        assertEquals(100.0, Market.contractPlayerToComp.get(0).getAmount());
        assertEquals(1, Market.contractPlayerToComp.get(0).getWeeks());
        assertEquals("I am cold", Market.contractPlayerToComp.get(0).getReason());

    }

    @Test
    void companyBuyUnavailableShare(){
        boolean suc6 = server.execute("company", testPlayer, "buy",
                "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to buy §f1§b from §fTCI for a price of §60,00 \n" +
                "§c/company buy TCI 1");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "buy",
                "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bbuy: §cdenied (not enough shares on the market)");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyOpenTrade(){
        boolean suc6 = server.execute("company", testPlayer, "open_trade",
                "TCI", "TRUE").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bopen trade set to true");
        testPlayer.assertNoMoreSaid();

        Company comp = Market.getBusiness("TCI");
        assertTrue(comp.openTrade);
        assertEquals(0, comp.marketShares);

    }

    @Test
    void companyBuyOpenTrade(){
        companyOpenTrade();
        boolean suc6 = server.execute("company", testPlayer, "buy",
                "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to buy §f1§b from §fTCI for a price of §60,00 \n" +
                "§c/company buy TCI 1");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "buy",
                "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bbuy: §aaccepted");
        testPlayer.assertNoMoreSaid();

        Company comp = Market.getBusiness("TCI");
        assertTrue(comp.openTrade);
        assertEquals(1000001, comp.totalShares);


        assertEquals(-1, comp.marketShares);

    }

    @Test
    void companyTransact(){
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Market.addBusiness("TCI2", "TCI2", Market.getAccount(testPlayer2));

        Market.getBusiness("TCI").addBal(100000.0);

        boolean suc6 = server.execute("company", testPlayer, "transact",
                "TCI", "TCI2", "100").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter the command to confirm");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "transact",
                "TCI", "TCI2", "100").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bsuccesfully paid §fTCI2 100.0");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§bsuccesfully received §f100.0 §bfrom §fTCI");
        testPlayer2.assertNoMoreSaid();

        assertEquals(100.0, Market.getBusiness("TCI2").getBal());
        assertEquals(100000.0-100, Market.getBusiness("TCI").getBal());

    }

    @Test
    void companyGetSpendable(){
        Market.getBusiness("TCI").addBal(1000.0);

        boolean suc6 = server.execute("company", testPlayer, "spendable",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§b spendable money: 200,00");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyGetBuyPriceAfterReload(){
        Market.getBusiness("TCI").addBal(1000.0);

        boolean suc6 = server.execute("company", testPlayer, "get_buy_price",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§f1§b share(s) costs §f0,00");
        testPlayer.assertNoMoreSaid();

        /*
         * Restart Server, check persistence
         * */
        plugin.onDisable();
        CountryManager.clear();
        Market.clear();
        plugin = TestTools.setupPlugin(server);


        suc6 = server.execute("company", testPlayer, "get_buy_price",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§f1§b share(s) costs §f0,00");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companySupport(){
        PlayerMock testPlayer2 = server.addPlayer();

        Company comp = Market.getBusiness("TCI");

        /*
        * Check that testPlayer is the owner
        * */
        assertEquals(1, comp.getOwners().size());
        assertEquals(testPlayer.getUniqueId(), comp.getOwners().get(0));

        boolean suc6 = server.execute("company", testPlayer, "support",
                "TCI", testPlayer2.getName()).hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bsupport changed to §fPlayer1");
        testPlayer.assertNoMoreSaid();

        /*
         * Check that testPlayer2 is the owner
         * */
        assertEquals(1, comp.getOwners().size());
        assertEquals(testPlayer2.getUniqueId(), comp.getOwners().get(0));

    }

    @Test
    void companySupportNeutral(){
        PlayerMock testPlayer2 = server.addPlayer();

        Company comp = Market.getBusiness("TCI");

        /*
         * Check that testPlayer is the owner
         * */
        assertEquals(1, comp.getOwners().size());
        assertEquals(testPlayer.getUniqueId(), comp.getOwners().get(0));

        Market.getAccount(testPlayer).sellShare("TCI", 5, testPlayer);
        Market.getAccount(testPlayer2).buyShare("TCI", 5, testPlayer2);

        testPlayer.assertSaid("§bsell: §aaccepted");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§bbuy: §aaccepted");
        testPlayer2.assertNoMoreSaid();

        /*
        *
        * */
        assertEquals(5, Market.getAccount(testPlayer2).shares.get("TCI"));
        assertEquals(1000000-5, Market.getAccount(testPlayer).shares.get("TCI"));

        boolean suc6 = server.execute("company", testPlayer, "support",
                "TCI", "neutral").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bsupport changed to §eneutral");
        testPlayer.assertNoMoreSaid();

        /*
        * When having minority of the support, but the remains is neutral support the person with the most support
        * */
        suc6 = server.execute("company", testPlayer2, "support",
                "TCI", testPlayer2.getName()).hasSucceeded();
        assertTrue(suc6);

        testPlayer2.assertSaid("§bsupport changed to §fPlayer1");
        testPlayer2.assertNoMoreSaid();

        /*
         * Check that testPlayer2 is the owner
         * */
        assertEquals(1, comp.getOwners().size());
        assertEquals(testPlayer2.getUniqueId(), comp.getOwners().get(0));

    }

    @Test
    void companySupportNeutral2(){
        PlayerMock testPlayer2 = server.addPlayer();

        Company comp = Market.getBusiness("TCI");

        /*
         * Check that testPlayer is the owner
         * */
        assertEquals(1, comp.getOwners().size());
        assertEquals(testPlayer.getUniqueId(), comp.getOwners().get(0));

        Market.getAccount(testPlayer).sellShare("TCI", 900000, testPlayer);
        Market.getAccount(testPlayer2).buyShare("TCI", 900000, testPlayer2);

        testPlayer.assertSaid("§bsell: §aaccepted");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§bbuy: §aaccepted");
        testPlayer2.assertNoMoreSaid();

        /*
         * change shares
         * */
        assertEquals(900000, Market.getAccount(testPlayer2).shares.get("TCI"));
        assertEquals(100000, Market.getAccount(testPlayer).shares.get("TCI"));

        /*
         * Check that testPlayer is the owner, because testPlayer2 its support is neutral
         * */
        assertEquals(1, comp.getOwners().size());
        assertEquals(testPlayer.getUniqueId(), comp.getOwners().get(0));

    }

    @Test
    void companyMarketAvailability(){
        Market.getAccount(testPlayer).sellShare("TCI", 900000, testPlayer);

        testPlayer.assertSaid("§bsell: §aaccepted");

        boolean suc6 = server.execute("company", testPlayer, "market").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§eMarket:\n§b1. §9TCI: §7900.000 (90,00%)");
        testPlayer.assertNoMoreSaid();

    }


}
