package com.interactionLayer.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import com.klanting.signclick.interactionLayer.commands.CompanyCommands;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.events.MenuEvents;
import com.klanting.signclick.interactionLayer.routines.AutoSave;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.utils.BookParser;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import tools.ExpandedServerMock;
import tools.TestTools;


import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



class CompanyCTests {


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
    void companyCreate(){
        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm that you want to start a company and want to auto-transfer §64 thousand §bto your business from your account If you agree, enter: §c/company create TESTINGCOMP COMP");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);
        testPlayer.simulateInventoryClick(6);

        testPlayer.assertSaid("§byou succesfully founded TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();


    }

    @Test
    void companyPay(){
        CompanyI company = Market.getCompany("TCI");
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        assertNotNull(company);
        company.addBal(100000.0);
        company.setSpendable(1000000000.0);

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
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, companyOption2.getType());

        /*
        * Check first item is a company
        * */
        String companyName = companyOption.getItemMeta().getDisplayName();
        assertEquals("§6TestCaseInc [TCI]", companyName);
    }

    @Test
    void companyShareTop(){

        boolean suc6 = server.execute("company", testPlayer, "sharetop", "TCI").hasSucceeded();
        assertTrue(suc6);

        /*
        * Check correctly received sharetop
        * */
        testPlayer.assertSaid("§bsharetop:");
        testPlayer.assertSaid("§9Player0: §f1.000 (100,00%)");
        testPlayer.assertSaid("§eMarket: §f0 (0,00%)");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyShareTop2(){
        CompanyI comp = Market.getCompany("TCI");

        PlayerMock testPlayer2 = server.addPlayer();

        comp.getCOM().changeShareHolder(Market.getAccount(testPlayer2), 100);
        comp.getCOM().changeShareHolder(Market.getAccount(testPlayer), -100);



        boolean suc6 = server.execute("company", testPlayer, "sharetop", "TCI").hasSucceeded();
        assertTrue(suc6);

        /*
         * Check correctly received sharetop
         * */
        testPlayer.assertSaid("§bsharetop:");
        testPlayer.assertSaid("§9Player0: §f900 (90,00%)");
        testPlayer.assertSaid("§9Player1: §f100 (10,00%)");
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
                "§bCEO: §7Player0\n" +
                "§bCTO: §7NONE\n" +
                "§bCFO: §7NONE\n" +
                "§bbal: §70\n" +
                "§bshares: §71.000\n" +
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

        CompanyI comp = Market.getCompany("TCI");
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
        CompanyI comp = Market.getCompany("TCI");

        assertEquals(1000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));

        /*
        * Sell 1000 stocks
        * */
        boolean suc6 = server.execute("company", testPlayer, "sell", "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to sell §f1§b from §fTCI§b for a price of §60,95 \n" +
                "§c/company sell TCI 1");
        testPlayer.assertNoMoreSaid();
        suc6 = server.execute("company", testPlayer, "sell", "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bsell: §aaccepted");
        testPlayer.assertNoMoreSaid();

        assertEquals(999, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(1, comp.getMarketShares());
        assertEquals(999, Math.round(comp.getValue()));

        /*
         * Buy 1000 stocks
         * */
        SignClick.getEconomy().depositPlayer(testPlayer, 500);

        suc6 = server.execute("company", testPlayer, "buy", "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to buy §f1§b from §fTCI for a price of §61,16 \n" +
                "§c/company buy TCI 1");
        testPlayer.assertNoMoreSaid();
        suc6 = server.execute("company", testPlayer, "buy", "TCI", "1").hasSucceeded();
        assertTrue(suc6);
        testPlayer.assertSaid("§bbuy: §aaccepted");
        testPlayer.assertNoMoreSaid();

        assertEquals(1000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(0, comp.getMarketShares());
        assertEquals(1000, Math.round(comp.getValue()));

    }

    @Test
    void companyTransferShares(){

        PlayerMock testPlayer2 = server.addPlayer();

        assertEquals(1000, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(null, Market.getAccount(testPlayer2.getUniqueId()).shares.get("TCI"));

        boolean suc6 = server.execute("company", testPlayer, "transfer",
                "TCI", testPlayer2.getName(), "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to transfer §f1§b shares to §fPlayer1\n" +
                "§c/company transfer TCI Player1 1");
        testPlayer.assertNoMoreSaid();

        /*
        * confirm transfer
        * */
        suc6 = server.execute("company", testPlayer, "transfer",
                "TCI", testPlayer2.getName(), "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§btransfer: §aaccepted");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§breceived: 1 shares for TCI from Player0");
        testPlayer2.assertNoMoreSaid();

        assertEquals(999, Market.getAccount(testPlayer.getUniqueId()).shares.get("TCI"));
        assertEquals(1, Market.getAccount(testPlayer2.getUniqueId()).shares.get("TCI"));
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
        testPlayer.nextMessage();

        testPlayer.assertSaid("§b1. §3TCI: §70\n");
        testPlayer.assertNoMoreSaid();

        /*
         * Test when 2 company exists
         * */

        Market.addCompany("otherComp", "TCI2", Market.getAccount(testPlayer.getUniqueId()));
        Market.getCompany("TCI2").addBal(100.0);

        suc6 = server.execute("company", testPlayer, "baltop").hasSucceeded();
        assertTrue(suc6);
        testPlayer.nextMessage();

        testPlayer.assertSaid("§b1. §3TCI2: §7100\n");
        testPlayer.assertSaid("§b2. §3TCI: §70\n");
        testPlayer.assertNoMoreSaid();


    }

    @Test
    void createCompany2CompanyContract(){
        AutoSave.stop();
        MenuEvents.stopMachineCheck();

        Country c = CountryManager.create("C", testPlayer);
        testPlayer.nextMessage();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addCompany("TestCaseInc2",
                "TCI2", Market.getAccount(testPlayer2.getUniqueId()));
        assertTrue(suc6);


        Market.getCompany("TCI").setCountry(c);
        Market.getCompany("TCI2").setCountry(c);
        Market.getCompany("TCI2").setSpendable(1000000000.0);

        Market.getCompany("TCI2").addBal(200000.0);

        suc6 = server.execute("company", testPlayer, "send_contract_ctc",
                "TCI", "TCI2", "100", "2", "I am cold").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to send a contract request to §fTCI2" +
                "§b\nfor an amount of §f100.0" +
                "§b\nfor a time of §f2 weeks \n" +
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
        plugin = TestTools.reboot(server);
        AutoSave.stop();
        MenuEvents.stopMachineCheck();

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
        AutoSave.stop();
        MenuEvents.stopMachineCheck();

        Country c = CountryManager.create("C", testPlayer);
        testPlayer.nextMessage();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        Market.getCompany("TCI").setCountry(c);

        Market.getCompany("TCI").addBal(200000.0);
        Market.getCompany("TCI").setSpendable(1000000000.0);

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
        plugin = TestTools.reboot(server);
        AutoSave.stop();
        MenuEvents.stopMachineCheck();

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
        AutoSave.stop();
        MenuEvents.stopMachineCheck();

        Country c = CountryManager.create("C", testPlayer);
        testPlayer.nextMessage();

        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);

        Market.getCompany("TCI").setCountry(c);

        Market.getCompany("TCI").addBal(200000.0);

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
        plugin = TestTools.reboot(server);
        AutoSave.stop();
        MenuEvents.stopMachineCheck();

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

        server.getScheduler().performTicks(60*60*24*7*20L+2);

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

        SignClick.getEconomy().depositPlayer(testPlayer, 100);
        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to buy §f1§b from §fTCI for a price of §61,16 \n" +
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

        CompanyI comp = Market.getCompany("TCI");
        assertTrue(comp.getCOM().isOpenTrade());
        assertEquals(0, comp.getMarketShares());

    }

    @Test
    void companyBuyOpenTrade(){
        companyOpenTrade();
        boolean suc6 = server.execute("company", testPlayer, "buy",
                "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        SignClick.getEconomy().depositPlayer(testPlayer, 1000);
        testPlayer.assertSaid("§bplease re-enter your command to confirm\n" +
                "that you want to buy §f1§b from §fTCI for a price of §61,16 \n" +
                "§c/company buy TCI 1");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "buy",
                "TCI", "1").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bbuy: §aaccepted");
        testPlayer.assertNoMoreSaid();

        CompanyI comp = Market.getCompany("TCI");
        assertTrue(comp.getCOM().isOpenTrade());
        assertEquals(1001, comp.getTotalShares());


        assertEquals(-1, comp.getMarketShares());

    }

    @Test
    void companyTransact(){
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        Market.addCompany("TCI2", "TCI2", Market.getAccount(testPlayer2));

        Market.getCompany("TCI").addBal(100000.0);
        Market.getCompany("TCI").setSpendable(1000000000.0);

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

        assertEquals(100.0, Market.getCompany("TCI2").getBal());
        assertEquals(100000.0-100, Market.getCompany("TCI").getBal());

    }

    @Test
    void companyGetBuyPriceAfterReload(){
        Market.getCompany("TCI").addBal(1000.0);

        boolean suc6 = server.execute("company", testPlayer, "get_buy_price",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§f1§b share(s) costs §f1,16");
        testPlayer.assertNoMoreSaid();

        /*
         * Restart Server, check persistence
         * */
        plugin = TestTools.reboot(server);


        suc6 = server.execute("company", testPlayer, "get_buy_price",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§f1§b share(s) costs §f1,16");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companyGetSellPriceAfterReload(){
        Market.getCompany("TCI").addBal(1000.0);

        boolean suc6 = server.execute("company", testPlayer, "get_sell_price",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§f1§b share(s) costs §f0,95");
        testPlayer.assertNoMoreSaid();

        /*
         * Restart Server, check persistence
         * */
        plugin = TestTools.reboot(server);


        suc6 = server.execute("company", testPlayer, "get_sell_price",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§f1§b share(s) costs §f0,95");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void companySupport(){
        PlayerMock testPlayer2 = server.addPlayer();

        /*
        * fix has played before
        * */
        PlayerMock spyPlayer = Mockito.spy(testPlayer2);
        Mockito.when(spyPlayer.hasPlayedBefore()).thenReturn(true);
        assertTrue(spyPlayer.hasPlayedBefore());

        /*
        * add the spy player to player list, to ensure ahsPlayerBefore
        * */
        server.getPlayerList().addPlayer(spyPlayer);


        CompanyI comp = Market.getCompany("TCI");

        /*
        * Check that testPlayer is the owner
        * */
        assertEquals(testPlayer.getUniqueId(), comp.getCOM().getBoard().getChief("CEO"));

        boolean suc6 = server.execute("company", testPlayer, "support",
                "TCI", testPlayer2.getName()).hasSucceeded();
        assertTrue(suc6);

        /*
        * Select first company
        * */
        testPlayer.simulateInventoryClick(0);

        /*
        * Click assign board button
        * */
        assertEquals(Material.ITEM_FRAME, testPlayer.getOpenInventory().getItem(45).getType());
        testPlayer.simulateInventoryClick(45);

        /*
        * Remove current player
        * */
        assertEquals(Material.PLAYER_HEAD, testPlayer.getOpenInventory().getItem(0).getType());
        testPlayer.simulateInventoryClick(0);

        /*
        * Add new player
        * */
        assertEquals(Material.WHITE_WOOL, testPlayer.getOpenInventory().getItem(0).getType());
        testPlayer.simulateInventoryClick(0);

        PlayerChatEvent chatEvent = new PlayerChatEvent(testPlayer, testPlayer2.getName());
        server.getPluginManager().callEvent(chatEvent);

        assertEquals(testPlayer2.getName(),
                testPlayer.getOpenInventory().getItem(0).getItemMeta().getDisplayName().substring(2));

        /*
         * Check that testPlayer2 is now board member
         * */
        assertEquals(1, comp.getCOM().getBoard().getBoardMembers().size());
        assertEquals(testPlayer2.getUniqueId(), comp.getCOM().getBoard().getBoardMembers().get(0));

    }

    @Test
    void companySupportOtherCompanyUI(){
        /*
        * check that you can see companies if you have at least 1 share
        * */
        PlayerMock testPlayer2 = server.addPlayer();

        CompanyI comp = Market.getCompany("TCI");
        Market.sell("TCI", 1, Market.getAccount(testPlayer));
        SignClick.getEconomy().depositPlayer(testPlayer2, 1000);
        Market.buy("TCI", 1, Market.getAccount(testPlayer2));
        assertEquals(2, comp.getCOM().getShareHolders().keySet().size());

        /*
         * Check that testPlayer is the owner
         * */
        assertEquals(testPlayer.getUniqueId(), comp.getCOM().getBoard().getChief("CEO"));

        boolean suc6 = server.execute("company", testPlayer2, "support").hasSucceeded();
        assertTrue(suc6);

        assertEquals(Material.LANTERN, testPlayer2.getOpenInventory().getItem(0).getType());

    }

    @Test
    void companyShareBalance(){
        /*
        * Check company shareBalance value
        * */

        CompanyI comp = Market.getCompany("TCI");
        Market.getAccount(testPlayer).sellShare("TCI", 100, testPlayer);

        comp.addBal(100);
        SignClick.getEconomy().depositPlayer(testPlayer, 10000);
        Market.getAccount(testPlayer).buyShare("TCI", 100, testPlayer);

        testPlayer.nextMessage();
        testPlayer.nextMessage();

        /*
         * Check that testPlayer is the owner
         * */
        assertEquals(testPlayer.getUniqueId(), comp.getCOM().getBoard().getChief("CEO"));

        boolean suc6 = server.execute("company", testPlayer, "sharebal",
                "TCI", testPlayer.getName()).hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§b shareBal money: 11,88");
        testPlayer.assertNoMoreSaid();

    }

    @Test
    void companyGetContracts(){
        PlayerMock testPlayer2 = server.addPlayer();

        Market.addCompany("TCI2", "TCI2", Market.getAccount(testPlayer2));

        Market.setContractComptoComp("TCI", "TCI2", 1, 1, "a");
        Market.setContractServertoComp("TCI", 2, 2, "b", 1);
        Market.setContractComptoPlayer("TCI", testPlayer2.getUniqueId().toString(), 3, 3, "c");
        Market.setContractPlayertoComp(testPlayer2.getUniqueId().toString(), "TCI", 4, 4, "d");

        boolean suc6 = server.execute("company", testPlayer, "get_contracts",
                "TCI").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("""
                §aincome:§0
                §aContract: from Player1(P) to TCI(C) amount: 4,0 for 4 weeks, reason: d§0
                §aContract: from SERVER(S) to TCI(C) amount: 2,0 for 2 weeks, reason: b delay: 1§0
                §coutgoing:§0
                §cContract: from TCI(C) to TCI2(C) amount: 1,0 for 1 weeks, reason: a§0
                §cContract: from TCI(C) to Player1(P) amount: 3,0 for 3 weeks, reason: c""");

        testPlayer.assertNoMoreSaid();

    }

    @Test
    void companySupportNeutral2(){
        PlayerMock testPlayer2 = server.addPlayer();

        CompanyI comp = Market.getCompany("TCI");

        /*
         * Check that testPlayer is the owner
         * */
        assertEquals(testPlayer.getUniqueId(), comp.getCOM().getBoard().getChief("CEO"));

        Market.getAccount(testPlayer).sellShare("TCI", 900, testPlayer);

        SignClick.getEconomy().depositPlayer(testPlayer2, 1000);
        Market.getAccount(testPlayer2).buyShare("TCI", 900, testPlayer2);

        testPlayer.assertSaid("§bsell: §aaccepted");
        testPlayer.assertNoMoreSaid();

        testPlayer2.assertSaid("§bbuy: §aaccepted");
        testPlayer2.assertNoMoreSaid();

        /*
         * change shares
         * */
        assertEquals(900, Market.getAccount(testPlayer2).shares.get("TCI"));
        assertEquals(100, Market.getAccount(testPlayer).shares.get("TCI"));

        /*
         * Check that testPlayer is the owner, because testPlayer2 its support is neutral
         * */
        assertEquals(testPlayer.getUniqueId(), comp.getCOM().getBoard().getChief("CEO"));

    }

    @Test
    void companyMarketAvailability(){
        Market.getAccount(testPlayer).sellShare("TCI", 900, testPlayer);

        testPlayer.assertSaid("§bsell: §aaccepted");

        boolean suc6 = server.execute("company", testPlayer, "markettop").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§eMarket:\n§b1. §9TCI: §7900 (90,00%)");
        testPlayer.assertNoMoreSaid();

    }

    @Test
    void companyGuide(){
        assertNull(testPlayer.getInventory().getItem(0));
        boolean suc6 = server.execute("company", testPlayer, "guide").hasSucceeded();
        assertTrue(suc6);

        assertNotNull(testPlayer.getInventory().getItem(0));
        ItemStack book = testPlayer.getInventory().getItem(0);
        assertEquals(Material.WRITTEN_BOOK, book.getType());
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        List<String> pages = BookParser.getPages("companyGuide.book", testPlayer);
        assertEquals(pages, bookMeta.getPages());
    }

    @Test
    void companyTabComplete(){
        PlayerMock testPlayer2 = server.addPlayer();
        List<String> receivedAutoCompletes =  server.getCommandTabComplete(testPlayer2, "company ");

        List<String> autoCompletes = new ArrayList<>();
        autoCompletes.add("create");
        autoCompletes.add("info");
        autoCompletes.add("sharetop");
        autoCompletes.add("give");
        autoCompletes.add("baltop");
        autoCompletes.add("buy");
        autoCompletes.add("sell");
        autoCompletes.add("pay");
        autoCompletes.add("spendable");
        autoCompletes.add("support");
        autoCompletes.add("transfer");
        autoCompletes.add("transact");
        autoCompletes.add("portfolio");
        autoCompletes.add("markettop");
        autoCompletes.add("market");
        autoCompletes.add("guide");
        autoCompletes.add("get_support");
        autoCompletes.add("send_contract_ctc");
        autoCompletes.add("sign_contract_ctc");
        autoCompletes.add("send_contract_ctp");
        autoCompletes.add("sign_contract_ctp");
        autoCompletes.add("send_contract_ptc");
        autoCompletes.add("sign_contract_ptc");
        autoCompletes.add("get_buy_price");
        autoCompletes.add("get_sell_price");
        autoCompletes.add("get_contracts");
        autoCompletes.add("open_trade");

        assertEquals(autoCompletes, receivedAutoCompletes);
    }

    @Test
    void companyTabCompleteCompany(){
        /*
        * Test Autocomplete providing company names
        * */
        Market.addCompany("TestCaseInc2", "TCI2", Market.getAccount(testPlayer));

        for (String item: CompanyCommands.whitelist){
            List<String> receivedAutoCompletes =  server.getCommandTabComplete(testPlayer, "company "+item+" ");
            assertEquals(Arrays.asList("TCI", "TCI2"), receivedAutoCompletes);
        }
    }

    @Test
    void companyTabCompleteStaff(){
        List<String> receivedAutoCompletes =  server.getCommandTabComplete(testPlayer, "company ");

        List<String> autoCompletes = new ArrayList<>();
        autoCompletes.add("sharebal");
        autoCompletes.add("create");
        autoCompletes.add("info");
        autoCompletes.add("sharetop");
        autoCompletes.add("give");
        autoCompletes.add("baltop");
        autoCompletes.add("buy");
        autoCompletes.add("sell");
        autoCompletes.add("pay");
        autoCompletes.add("spendable");
        autoCompletes.add("support");
        autoCompletes.add("transfer");
        autoCompletes.add("transact");
        autoCompletes.add("portfolio");
        autoCompletes.add("markettop");
        autoCompletes.add("market");
        autoCompletes.add("guide");
        autoCompletes.add("get_support");
        autoCompletes.add("send_contract_ctc");
        autoCompletes.add("sign_contract_ctc");
        autoCompletes.add("send_contract_ctp");
        autoCompletes.add("sign_contract_ctp");
        autoCompletes.add("send_contract_ptc");
        autoCompletes.add("sign_contract_ptc");
        autoCompletes.add("get_buy_price");
        autoCompletes.add("get_sell_price");
        autoCompletes.add("get_contracts");
        autoCompletes.add("open_trade");

        assertEquals(autoCompletes, receivedAutoCompletes);
    }

    @Test
    void companyNotCreated(){
        /*
        * Test that the company is not created if the user closes the type menu
        * */

        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm that you want to start a company and want to auto-transfer §64 thousand §bto your business from your account If you agree, enter: §c/company create TESTINGCOMP COMP");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);
        testPlayer.closeInventory();

        assertNull(Market.getCompany("COMP"));
        assertEquals(40000000, SignClick.getEconomy().getBalance(testPlayer));
    }

    @Test
    void companySharesBrokenBug(){
        /*
        * Test the following scenario that caused a bug
        * 1. players buys many shares
        * 2. player spends money, so bal is negative but shares bal is not.
        * 3. the buy base value < 0, because we were able to remove money, when only checking the value
        * */

        SignClick.getEconomy().depositPlayer(testPlayer, 12830);
        CompanyI company = Market.getCompany("TCI");
        company.addBal(1000.0);

        assertNotNull(company);
        company.getCOM().setOpenTrade(true);

        assertEquals(1000, company.getBal());
        boolean  suc6 = server.execute("company", testPlayer, "buy", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);
        testPlayer.nextMessage();

        suc6 = server.execute("company", testPlayer, "buy", "TCI", "1000").hasSucceeded();
        assertTrue(suc6);
        testPlayer.assertSaid("§bbuy: §aaccepted");

        assertEquals(2027, (int) company.getBal());

        assertNotEquals(12830, SignClick.getEconomy().getBalance(testPlayer));
        double oldShares = company.getShareBalance();
        assertEquals(Math.round(SignClick.getEconomy().getBalance(testPlayer)), Math.round(12830-(company.getValue()-1000)));

        /*
        * check if money removed from both sharebal and bal
        * */
        assertTrue(company.removeBal(1000, true));
        assertNotEquals(oldShares, company.getShareBalance());

        /*
        * check balance always positive
        * */
        assertTrue(company.removeBal(1000, true));
    }

    @Test
    void companyDirectSellBug(){
        /*
        * Selling company after directly creating, should not create negative value, but should be 0
        * */
        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);

        CountryManager.create("CO", testPlayer);
        testPlayer.nextMessage();

        boolean suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);

        testPlayer.assertSaid("§bplease re-enter your command to confirm that you want to start a company and want to auto-transfer §64 thousand §bto your business from your account If you agree, enter: §c/company create TESTINGCOMP COMP");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "create", "TESTINGCOMP", "COMP").hasSucceeded();
        assertTrue(suc6);
        testPlayer.simulateInventoryClick(6);

        testPlayer.assertSaid("§byou succesfully founded TESTINGCOMP good luck CEO Player0");
        testPlayer.assertNoMoreSaid();

        suc6 = server.execute("company", testPlayer, "sell", "COMP", "1000").hasSucceeded();
        assertTrue(suc6);
        testPlayer.nextMessage();

        suc6 = server.execute("company", testPlayer, "sell", "COMP", "1000").hasSucceeded();
        assertTrue(suc6);
        testPlayer.assertSaid("§bsell: §aaccepted");

        CompanyI company = Market.getCompany("COMP");
        assertTrue(company.getValue() >= 0.0);
    }


}
