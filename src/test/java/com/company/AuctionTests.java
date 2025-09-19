package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.companyLogic.patent.Auction;
import com.klanting.signclick.interactionLayer.routines.AutoSave;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;
import static org.gradle.internal.impldep.org.junit.Assert.assertNotEquals;

public class AuctionTests {
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
    void auctionUpdate(){
        Auction auction = Auction.getInstance();
        AutoSave.stop();

        PlayerMock testPlayer = server.addPlayer();

        Market.addCompany("A", "A", Market.getAccount(testPlayer));
        auction.setBit(0, 100, "A");
        assertEquals(100, auction.getBit(0));

        server.getScheduler().performTicks(60*60*24*7*20L+1);
        assertNotEquals(100, auction.getBit(0));

        auction.setBit(0, 100, "A");
        assertEquals(100, auction.getBit(0));

        server.getScheduler().performTicks(60*60*24*7*20L+1);
        assertNotEquals(100, auction.getBit(0));

        auction.getBit(0);
    }
}
