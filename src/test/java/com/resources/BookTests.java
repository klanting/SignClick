package com.resources;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.utils.BookParser;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookTests {
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
    void loadCompanyGuide(){
        List<String> pages = BookParser.getPages("companyGuide.book", testPlayer);
        assertEquals(14, pages.size());
    }
}
