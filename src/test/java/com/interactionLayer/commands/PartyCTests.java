package com.interactionLayer.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.countryLogic.parties.Party;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PartyCTests {
    private ServerMock server;
    private SignClick plugin;

    private PlayerMock testPlayer2;
    private PlayerMock testPlayer3;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        testPlayer3 = TestTools.addPermsPlayer(server, plugin);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
    }

    @Test
    void createParty(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        CountryManager.create("empire1", testPlayer);

        Country country = CountryManager.getCountry("empire1");
        country.addMember(testPlayer2);
        country.addMember(testPlayer3);

        assertEquals(1, country.getParties().size());

        boolean result = server.execute("party", testPlayer2, "create", "AAA").hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bParty created");
        testPlayer2.assertNoMoreSaid();

        assertEquals(2, country.getParties().size());
        assertEquals("AAA", country.getParties().get(1).name);
    }

    @Test
    void addParty(){
        createParty();

        Country country = CountryManager.getCountry("empire1");
        assertEquals(0, country.getParties().get(1).members.size());

        boolean result = server.execute("party", testPlayer2, "add", testPlayer3.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bPlayer added to the party");
        testPlayer2.assertNoMoreSaid();

        assertEquals(1, country.getParties().get(1).members.size());
    }

    @Test
    void kickParty(){
        addParty();

        Country country = CountryManager.getCountry("empire1");
        assertEquals(1, country.getParties().get(1).members.size());

        boolean result = server.execute("party", testPlayer2, "kick", testPlayer3.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bPlayer kicked to the party");
        testPlayer2.assertNoMoreSaid();

        assertEquals(0, country.getParties().get(1).members.size());
    }

    @Test
    void promoteParty(){
        addParty();

        Country country = CountryManager.getCountry("empire1");
        Party party = country.getParties().get(1);
        assertEquals(1, party.members.size());
        assertEquals(1, party.owners.size());
        assertFalse(party.isOwner(testPlayer3.getUniqueId()));

        boolean result = server.execute("party", testPlayer2, "promote", testPlayer3.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bPlayer is promoted");
        testPlayer2.assertNoMoreSaid();

        assertEquals(0, party.members.size());
        assertEquals(2, party.owners.size());
        assertTrue(party.isOwner(testPlayer3.getUniqueId()));
    }

    @Test
    void demoteParty(){
        promoteParty();

        Country country = CountryManager.getCountry("empire1");
        Party party = country.getParties().get(1);

        assertEquals(0, party.members.size());
        assertEquals(2, party.owners.size());
        assertTrue(party.isOwner(testPlayer3.getUniqueId()));

        boolean result = server.execute("party", testPlayer2, "demote", testPlayer3.getName()).hasSucceeded();
        assertTrue(result);

        testPlayer2.assertSaid("§bPlayer is demoted");
        testPlayer2.assertNoMoreSaid();

        assertEquals(1, party.members.size());
        assertEquals(1, party.owners.size());
        assertFalse(party.isOwner(testPlayer3.getUniqueId()));

    }

    @Test
    void leaveParty(){
        addParty();

        Country country = CountryManager.getCountry("empire1");
        Party party = country.getParties().get(1);

        assertEquals(1, party.members.size());
        assertEquals(1, party.owners.size());
        assertFalse(party.isOwner(testPlayer3.getUniqueId()));

        boolean result = server.execute("party", testPlayer3, "leave").hasSucceeded();
        assertTrue(result);

        testPlayer3.assertSaid("§bYou left the party");
        testPlayer3.assertNoMoreSaid();

        assertEquals(0, party.members.size());
        assertEquals(1, party.owners.size());

    }

    @Test
    void infoParty(){
        addParty();

        Country country = CountryManager.getCountry("empire1");
        Party party = country.getParties().get(1);

        boolean result = server.execute("party", testPlayer3, "info", party.name).hasSucceeded();
        assertTrue(result);

        testPlayer3.assertSaid("""
                §bName: §7AAA
                §bVotes: 0,00%
                Owners: [Player0]
                Members: [Player1]""");
        testPlayer3.assertNoMoreSaid();

    }

    @Test
    void partyTabComplete(){
        PlayerMock testPlayer2 = server.addPlayer();
        List<String> receivedAutoCompletes =  server.getCommandTabComplete(testPlayer2, "party ");

        List<String> autoCompletes = new ArrayList<>();
        autoCompletes.add("create");
        autoCompletes.add("add");
        autoCompletes.add("kick");
        autoCompletes.add("promote");
        autoCompletes.add("demote");
        autoCompletes.add("leave");
        autoCompletes.add("info");
        autoCompletes.add("vote");
        autoCompletes.add("coup");

        assertEquals(autoCompletes, receivedAutoCompletes);
    }
}
