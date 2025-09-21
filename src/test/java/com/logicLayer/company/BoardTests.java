package com.logicLayer.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.Board;
import com.klanting.signclick.logicLayer.companyLogic.CompanyOwnerManager;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTests {
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
    void boardCreate(){
        /*
        * Create a default board
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        CompanyOwnerManager companyOwnerManager = new CompanyOwnerManager(testPlayer.getUniqueId());

        Board board = new Board(companyOwnerManager);

        assertEquals(testPlayer.getUniqueId(), board.getChief("CEO"));
        assertEquals(1, board.getBoardMembers().size());

        assertEquals(testPlayer.getUniqueId(), board.getBoardMembers().get(0));
    }

    @Test
    void boardCEOChange(){
        /*
        * 2 steps
        * 1. Add board member but keep in charge
        * 2. Remove board member of CEO, and see change of power
        * */

        /*
         * when another user gets 1 share, this person can get 1 of the 2 board seats, but the old CEO must remain
         * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer3 = TestTools.addPermsPlayer(server, plugin);

        CompanyOwnerManager companyOwnerManager = new CompanyOwnerManager(testPlayer.getUniqueId());
        Board board = new Board(companyOwnerManager);

        companyOwnerManager.getShareHolders().put(testPlayer.getUniqueId(), 999999);
        companyOwnerManager.getShareHolders().put(testPlayer2.getUniqueId(), 1);

        board.addBoardSupport(testPlayer2.getUniqueId(), testPlayer2.getUniqueId());
        board.boardChiefVote(testPlayer2.getUniqueId(), "CEO", testPlayer2.getUniqueId());

        assertEquals(testPlayer.getUniqueId(), board.getChief("CEO"));
        assertEquals(2, board.getBoardMembers().size());

        assertEquals(testPlayer.getUniqueId(), board.getBoardMembers().get(0));
        assertEquals(testPlayer2.getUniqueId(), board.getBoardMembers().get(1));

        /*
        * Remove board seat of current CEO
        * */
        board.removeBoardSupport(testPlayer.getUniqueId(), testPlayer.getUniqueId());

        assertEquals(testPlayer2.getUniqueId(), board.getChief("CEO"));
        assertEquals(1, board.getBoardMembers().size());

        assertEquals(testPlayer2.getUniqueId(), board.getBoardMembers().get(0));

        /*
        * Add third board seat and support testPlayer3
        * */
        board.setBoardSeats(3);
        board.addBoardSupport(testPlayer.getUniqueId(), testPlayer.getUniqueId());
        board.addBoardSupport(testPlayer.getUniqueId(), testPlayer3.getUniqueId());

        board.boardChiefVote(testPlayer.getUniqueId(), "CEO", testPlayer3.getUniqueId());
        board.boardChiefVote(testPlayer3.getUniqueId(), "CEO", testPlayer3.getUniqueId());

        /*
        * check testPlayer3 is CEO
        * */
        assertEquals(testPlayer3.getUniqueId(), board.getChief("CEO"));
        assertEquals(3, board.getBoardMembers().size());
    }

    @Test
    void checkOnlyOneRole(){
        /*
        * Check that if the CFO is promoted to the CEO, this user is not the CFO anymore
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer3 = TestTools.addPermsPlayer(server, plugin);

        CompanyOwnerManager companyOwnerManager = new CompanyOwnerManager(testPlayer.getUniqueId());
        Board board = new Board(companyOwnerManager);
        board.setBoardSeats(3);

        companyOwnerManager.getShareHolders().put(testPlayer.getUniqueId(), 1000000);
        board.addBoardSupport(testPlayer.getUniqueId(), testPlayer.getUniqueId());
        board.addBoardSupport(testPlayer.getUniqueId(), testPlayer2.getUniqueId());
        board.addBoardSupport(testPlayer.getUniqueId(), testPlayer3.getUniqueId());

        board.boardChiefVote(testPlayer.getUniqueId(), "CEO", testPlayer2.getUniqueId());
        board.boardChiefVote(testPlayer2.getUniqueId(), "CEO", testPlayer2.getUniqueId());
        board.boardChiefVote(testPlayer3.getUniqueId(), "CEO", testPlayer2.getUniqueId());

        board.boardChiefVote(testPlayer.getUniqueId(), "CFO", testPlayer.getUniqueId());
        board.boardChiefVote(testPlayer2.getUniqueId(), "CFO", testPlayer.getUniqueId());
        board.boardChiefVote(testPlayer3.getUniqueId(), "CFO", testPlayer2.getUniqueId());

        /*
        * Check testPlayer 2 is CEO
        * check testPlayer 1 is CFO
        * */
        assertEquals(testPlayer2.getUniqueId(), board.getChief("CEO"));
        assertEquals(testPlayer.getUniqueId(), board.getChief("CFO"));

        board.boardChiefVote(testPlayer.getUniqueId(), "CEO", testPlayer.getUniqueId());
        board.boardChiefVote(testPlayer2.getUniqueId(), "CEO", testPlayer.getUniqueId());
        board.boardChiefVote(testPlayer3.getUniqueId(), "CEO", testPlayer.getUniqueId());

        /*
         * Check testPlayer 1 is CEO
         * check testPlayer 2 is CFO
         * */
        assertEquals(testPlayer.getUniqueId(), board.getChief("CEO"));
        assertEquals(testPlayer2.getUniqueId(), board.getChief("CFO"));

    }

    @Test
    void checkOnlyOneChief(){
        /*
        * ensure player not CFO and CTO at the same time
        * */

        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer2 = TestTools.addPermsPlayer(server, plugin);
        PlayerMock testPlayer3 = TestTools.addPermsPlayer(server, plugin);

        CompanyOwnerManager companyOwnerManager = new CompanyOwnerManager(testPlayer.getUniqueId());
        Board board = new Board(companyOwnerManager);
        board.setBoardSeats(3);

        companyOwnerManager.getShareHolders().put(testPlayer.getUniqueId(), 1000000);
        board.addBoardSupport(testPlayer.getUniqueId(), testPlayer.getUniqueId());
        board.addBoardSupport(testPlayer.getUniqueId(), testPlayer2.getUniqueId());
        board.addBoardSupport(testPlayer.getUniqueId(), testPlayer3.getUniqueId());

        board.boardChiefVote(testPlayer.getUniqueId(), "CEO", testPlayer.getUniqueId());

        board.boardChiefVote(testPlayer2.getUniqueId(), "CTO", testPlayer2.getUniqueId());
        board.boardChiefVote(testPlayer2.getUniqueId(), "CFO", testPlayer2.getUniqueId());

        /*
        * check player is CFO
        * */
        assertEquals(testPlayer.getUniqueId(), board.getChief("CEO"));
        assertEquals(testPlayer2.getUniqueId(), board.getChief("CFO"));
        assertNull(board.getChief("CTO"));

        /*
        * Support player as CTO
        * */
        board.boardChiefVote(testPlayer3.getUniqueId(), "CTO", testPlayer2.getUniqueId());

        /*
         * check player is CFO
         * */
        assertEquals(testPlayer.getUniqueId(), board.getChief("CEO"));
        assertEquals(testPlayer2.getUniqueId(), board.getChief("CFO"));
        assertNull(board.getChief("CTO"));

        TestTools.reboot(server);

        /*
         * check player is CFO
         * */
        assertEquals(testPlayer.getUniqueId(), board.getChief("CEO"));
        assertEquals(testPlayer2.getUniqueId(), board.getChief("CFO"));
        assertNull(board.getChief("CTO"));

    }
}
