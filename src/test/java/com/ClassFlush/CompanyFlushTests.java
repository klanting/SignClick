package com.ClassFlush;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.Company;
import com.klanting.signclick.logicLayer.companyLogic.Machine;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.companyLogic.research.Research;
import com.klanting.signclick.utils.BlockPosKey;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.OrderedList;
import com.klanting.signclick.utils.statefulSQLSerializers.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;
import tools.ExpandedServerMock;
import tools.TestTools;
import static org.gradle.internal.impldep.org.junit.Assert.*;

public class CompanyFlushTests {
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
        DatabaseSingleton.getInstance().registerSerializer(new MaterialSerializer(Material.class));
        DatabaseSingleton.getInstance().registerSerializer(new PairSerializer(Pair.class));
        DatabaseSingleton.getInstance().registerSerializer(new ItemStackSerializer(ItemStack.class));
        DatabaseSingleton.getInstance().registerSerializer(new BlockPosKeySerializer(BlockPosKey.class));
        DatabaseSingleton.getInstance().registerSerializer(new ChatColorSerializer(ChatColor.class));
        DatabaseSingleton.getInstance().registerSerializer(new LocationSerializer(Location.class));

        OrderedList<Company> companies = new OrderedList<>("a", Company.class);

        Player player = TestTools.addPermsPlayer(server, plugin);

        Company preComp = new Company("AAA", "AAA", Market.getAccount(player.getUniqueId()),
                1000, "Miscellaneous");
        companies.add(preComp);

        assertEquals(1, companies.size());

        Company company = companies.get(0);

        assertEquals(1000.0, company.getBal(), 0.01);
        company.getCOM().getBoard().setBoardSeats(10);
        assertEquals(10, company.getCOM().getBoard().getBoardSeats());

        /*
        * Add company balance of 1000
        * */
        company.addBal(1000);
        assertEquals(2000.0, company.getBal(), 0.01);
        assertEquals(2000.0, companies.get(0).getBal(), 0.01);

        /*
        * Do some research
        * */
        assertEquals(22, company.getResearch().getResearchOptions().size());
        Research research = company.getResearch();
        assertEquals(0.0, research.getResearchOptions().get(0).getProgress(), 0.001);
        research.getResearchOptions().get(0).setModifierIndex(2);
        company.setSpendable(2000);

        server.getScheduler().performTicks(1000);

        research.checkProgress();

        assertNotEquals(0.0, research.getResearchOptions().get(0).getProgress(), 0.001);

        /*
        * test company machine added
        * */
        assertEquals(0, company.getMachines().size());
        Location loc = new Location(server.addSimpleWorld("default"), 1, 2, 3);
        BlockPosKey bps = BlockPosKey.from(loc);
        company.getMachines().put(bps, new Machine(loc.getBlock(), company));

        assertEquals(1, company.getMachines().size());

    }


}
