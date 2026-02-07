package com.ClassFlush;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.Board;
import com.klanting.signclick.logicLayer.companyLogic.CompanyOwnerManager;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.utils.statefullSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefullSQL.access.OrderedList;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import tools.DataBaseTest;
import tools.ExpandedServerMock;
import tools.TestTools;
import static org.gradle.internal.impldep.org.junit.Assert.*;

public class COMFlushTests {
    private ServerMock server;
    private SignClick plugin;
    @BeforeEach
    public void setUp() throws Exception {
        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);
        DataBaseTest.initDb();
    }

    @AfterEach
    public void tearDown() throws Exception {
        DataBaseTest.shutdown();
        DatabaseSingleton.clear();
        MockBukkit.unmock();
        Market.clear();
    }

    @Test
    void flushComTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        OrderedList<CompanyOwnerManager> comms = new OrderedList<>("a", CompanyOwnerManager.class);
        Player player = TestTools.addPermsPlayer(server, plugin);

        CompanyOwnerManager preCom = new CompanyOwnerManager(player.getUniqueId());
        Board preBoard = preCom.getBoard();
        comms.add(preCom);

        assertEquals(1, comms.size());
        CompanyOwnerManager com = comms.get(0);
        Board board = com.getBoard();

        assertEquals(preCom.getSupport(player.getUniqueId()), com.getSupport(player.getUniqueId()));
        assertEquals(preBoard.getBoardSeats(), board.getBoardSeats());
    }


}
