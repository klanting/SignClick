package com.storage;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.google.common.reflect.TypeToken;
import com.klanting.signclick.logicLayer.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyPatent.Auction;
import com.klanting.signclick.logicLayer.companyPatent.PatentUpgrade;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.utils.Utils;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GsonTests {

    private ServerMock server;
    private SignClick plugin;

    private Player testPlayer;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        testPlayer = server.addPlayer();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        Auction.clear();
    }

    @Test
    void saveLoadUUID(){
        Utils.writeSave("uuid", testPlayer.getUniqueId());

        assertEquals(testPlayer.getUniqueId(), Utils.readSave("uuid", UUID.class, new UUID(1, 1)));
    }

    @Test
    void saveLoadAccount(){

        Map<UUID, Account> accountsPreSave = new HashMap<>();
        accountsPreSave.put(testPlayer.getUniqueId(), new Account(testPlayer.getUniqueId()));

        Utils.writeSave("accounts", accountsPreSave);

        Map<UUID, Account> accounts = Utils.readSave("accounts", new TypeToken<HashMap<UUID, Account>>(){}.getType(), new HashMap<>());
        assertEquals(1, accounts.size());

        assertEquals(testPlayer.getUniqueId(), accounts.keySet().iterator().next());
        assertEquals(testPlayer.getUniqueId(), accounts.values().iterator().next().getUuid());
    }

    @Test
    void saveLoadCompanies(){
        Map<String, CompanyI> accountsPreSave = new HashMap<>();
        accountsPreSave.put("A", new Company("AA", "A", Market.getAccount(testPlayer), 0, "Decoration"));
        accountsPreSave.get("A").getCOM().getShareHolders().put(testPlayer.getUniqueId(), 10);
        accountsPreSave.get("A").setTotalShares(100);

        Utils.writeSave("companies", accountsPreSave);

        Map<String, CompanyI> companies = Utils.readSave("companies", new TypeToken<HashMap<String, CompanyI>>(){}.getType(), new HashMap<>());
        assertEquals(1, companies.size());

        CompanyI comp = companies.values().iterator().next();

        assertEquals("A", comp.getStockName());
        assertEquals("AA", comp.getName());
        assertEquals(0, comp.getBal());
        assertEquals(7, comp.getUpgrades().size());
        assertEquals(100, comp.getTotalShares());

        assertEquals(7, comp.getLogObservers().size());

        assertEquals(testPlayer.getUniqueId(), comp.getCOM().getShareHolders().keySet().stream().iterator().next());
    }

    @Test
    void saveLoadCountries(){
        Map<String, Country> countries = new HashMap<>();

        String countryName = "A";
        Country newCountry = new Country(countryName, testPlayer);

        Player testPlayer2 = server.addPlayer();
        newCountry.addMember(testPlayer2);

        countries.put(countryName, newCountry);

        Utils.writeSave("countries", countries);

        countries = Utils.readSave("countries", new TypeToken<HashMap<String, Country>>(){}.getType(), new HashMap<>());
        assertEquals(1, countries.size());

        Country country = countries.values().iterator().next();

        assertEquals(countryName, country.getName());
        assertTrue(country.isOwner(testPlayer));
        assertEquals(1, country.getMembers().size());
        assertEquals(testPlayer2.getUniqueId(), country.getMembers().get(0));

    }

    @Test
    void saveLoadAuction(){
        Auction auction = Auction.getInstance();

        auction.setBit(0, 100, "A");

        assertEquals(100, auction.getBit(0));
        PatentUpgrade up = auction.toBuy.get(0);

        Utils.writeSave("auction", auction);

        auction = Utils.readSave("auction", new TypeToken<Auction>(){}.getType(), new Auction());

        assertEquals(100, auction.getBit(0));

        assertEquals(up, auction.toBuy.get(0));

    }

    @Test
    void saveLoadBoard(){

        /*
         * Create a default board
         * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        CompanyOwnerManager companyOwnerManager = new CompanyOwnerManager(testPlayer.getUniqueId());

        Board board = new Board(companyOwnerManager);

        assertEquals(testPlayer.getUniqueId(), board.getChief("CEO"));
        assertEquals(1, board.getBoardMembers().size());

        assertEquals(testPlayer.getUniqueId(), board.getBoardMembers().get(0));

        Utils.writeSave("board", board);

        board = Utils.readSave("board", new TypeToken<Board>(){}.getType(), new Board());
        board.setCompanyOwnerManager(companyOwnerManager);

        assertEquals(testPlayer.getUniqueId(), board.getChief("CEO"));
        assertEquals(1, board.getBoardMembers().size());

        assertEquals(testPlayer.getUniqueId(), board.getBoardMembers().get(0));

    }

    @Test
    void saveLoadCompanyOwnerManager(){

        /*
         * Create a default board
         * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        CompanyOwnerManager companyOwnerManager = new CompanyOwnerManager(testPlayer.getUniqueId());

        Utils.writeSave("companyOwnerManager", companyOwnerManager);

        companyOwnerManager = Utils.readSave("companyOwnerManager", new TypeToken<CompanyOwnerManager>(){}.getType(), new CompanyOwnerManager());
        companyOwnerManager.fixBoard();


    }
}
