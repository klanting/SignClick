package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.companyUpgrades.Upgrade;
import com.klanting.signclick.economy.companyUpgrades.UpgradeBoardSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;

public class UpgradeTests {
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
        Market.clear();
    }

    @Test
    void boardSizeUpgrade(){
        /*
        * check that board upgrade upgrades the board size
        * check 2 -> 3
        * */
        CompanyI comp = Market.getCompany("TCI");
        assertEquals(2, comp.getCOM().getBoard().getBoardSeats());

        Upgrade upgrade = comp.getUpgrades().get(3);
        assertInstanceOf(UpgradeBoardSize.class, upgrade);
        upgrade.DoUpgrade();

        assertEquals(3, comp.getCOM().getBoard().getBoardSeats());


    }
}
