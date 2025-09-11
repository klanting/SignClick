package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Machine;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.Product;
import com.klanting.signclick.economy.companyUpgrades.Upgrade;
import com.klanting.signclick.economy.companyUpgrades.UpgradeBoardSize;
import com.klanting.signclick.economy.companyUpgrades.UpgradeProductModifier;
import com.klanting.signclick.utils.BlockPosKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DoubleBlockMock;
import tools.ExpandedServerMock;
import tools.TestTools;
import tools.WorldDoubleMock;

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

    @Test
    void productionModifierUpgrade(){
        CompanyI comp = Market.getCompany("TCI");
        comp.addBal(1000);
        comp.setSpendable(1000);

        BlockMock machineBlock = new DoubleBlockMock(Material.BLAST_FURNACE,
                new Location(new WorldDoubleMock(), 0, 1, 0));
        Machine m = new Machine(machineBlock, comp);
        comp.getMachines().put(BlockPosKey.from(machineBlock.getLocation()), m);

        assertEquals(0, m.getProductionProgress());

        m.changeProductionCount(2);
        m.setProduct(new Product(Material.RED_WOOL, 1, 10));

        for (int i=0; i<9;i++){
            m.productionUpdate();
        }

        assertEquals(9, m.getProductionProgress());
        m.productionUpdate();
        assertEquals(0, m.getProductionProgress());

        /*
        * see faster with modifier
        * */
        assertInstanceOf(UpgradeProductModifier.class, comp.getUpgrades().get(6));
        comp.getUpgrades().get(6).DoUpgrade();

        m.productionUpdate();
        assertEquals(1, m.getProductionProgress());

        for (int i=0; i<8;i++){
            m.productionUpdate();
        }
        assertEquals(0, m.getProductionProgress());
    }
}
