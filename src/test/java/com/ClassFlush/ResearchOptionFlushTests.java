package com.ClassFlush;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyRef;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.companyLogic.research.ResearchOption;
import com.klanting.signclick.utils.statefulSQL.DatabaseSingleton;
import com.klanting.signclick.utils.statefulSQL.access.OrderedList;
import com.klanting.signclick.utils.statefulSQLSerializers.MaterialSerializer;
import com.klanting.signclick.utils.statefulSQLSerializers.PairSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DataBaseTest;
import tools.ExpandedServerMock;
import tools.TestTools;
import static org.gradle.internal.impldep.org.junit.Assert.*;

public class ResearchOptionFlushTests {
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
    void flushResearchOptionTest(){
        DatabaseSingleton.getInstance(DataBaseTest.getConnection());
        DatabaseSingleton.getInstance().registerSerializer(new MaterialSerializer(Material.class));
        DatabaseSingleton.getInstance().registerSerializer(new PairSerializer(Pair.class));

        OrderedList<ResearchOption> researchOptions = new OrderedList<>("a", ResearchOption.class);

        Player player = TestTools.addPermsPlayer(server, plugin);

        ResearchOption option = new ResearchOption(new CompanyRef("AA"), Material.TORCH);
        researchOptions.add(option);

        assertEquals(1, researchOptions.size());
    }


}
