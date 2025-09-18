package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.CompanyI;
import com.klanting.signclick.logicLayer.Market;
import com.klanting.signclick.logicLayer.Research;
import com.klanting.signclick.logicLayer.ResearchOption;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchTests {
    private ServerMock server;
    private SignClick plugin;

    private CompanyI comp;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        boolean suc6 =  Market.addCompany("TCI", "TCI",
                Market.getAccount(TestTools.addPermsPlayer(server, plugin)), 0.0, "Decoration");

        assertTrue(suc6);

        this.comp = Market.getCompany("TCI");
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Market.clear();
    }

    @Test
    void initResearch(){
        /*
        * check research correctly initialized
        * */
        Research research = new Research(comp.getRef());

        assertEquals(SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                        getConfigurationSection("Decoration").getKeys(false).size(),
                research.getResearchOptions().size());

        assertEquals(Material.TORCH, research.getResearchOptions().get(0).getMaterial());
        assertEquals(0.0, research.getResearchOptions().get(0).getProgress());
        assertEquals(1800L, research.getResearchOptions().get(0).getCompleteTime());
    }

    @Test
    void doResearch(){
        /*
         * check research update
         * */
        Research research = new Research(comp.getRef());

        assertEquals(SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                        getConfigurationSection("Decoration").getKeys(false).size(),
                research.getResearchOptions().size());

        assertEquals(Material.TORCH, research.getResearchOptions().get(0).getMaterial());
        assertEquals(0.0, research.getResearchOptions().get(0).getProgress());
        assertEquals(1800L, research.getResearchOptions().get(0).getCompleteTime());

        research.getResearchOptions().get(0).setModifierIndex(1);
        Market.getCompany("TCI").addBal(1000);
        Market.getCompany("TCI").setSpendable(1000);

        /*
        * do 50% of the time
        * */
        server.getScheduler().performTicks(900*20L);
        research.checkProgress();

        /*
        * check 50% progress
        * */
        assertEquals(Material.TORCH, research.getResearchOptions().get(0).getMaterial());
        assertEquals(0.5, research.getResearchOptions().get(0).getProgress());
        assertEquals(750.0, Math.ceil(Market.getCompany("TCI").getBal()));

        assertEquals(0, Market.getCompany("TCI").getProducts().size());

        server.getScheduler().performTicks(600*20L + 6000);
        research.checkProgress();

        /*
         * check 100% progress
         * */
        assertEquals(Material.TORCH, research.getResearchOptions().get(0).getMaterial());
        assertEquals(1.0, research.getResearchOptions().get(0).getProgress());
        assertEquals(500.0, Math.ceil(Market.getCompany("TCI").getBal()));
        assertEquals(1, Market.getCompany("TCI").getProducts().size());

    }

    @Test
    void longResearchPositive(){
        /*
        * ensure research progression is always positive
        * */

        comp.addBal(10000);
        comp.setSpendable(10000);

        ResearchOption ro = comp.getResearch().getResearchOptions().get(0);
        ro.setModifierIndex(5);

        assertEquals(0.0, ro.getProgress());
        assertEquals(ro.getCompleteTime()/2L, ro.getRemainingTime());
        assertEquals(900, ro.getRemainingTime());

        server.getScheduler().performTicks(21L);
        comp.getResearch().checkProgress();

        server.getScheduler().performTicks(800L);
        comp.getResearch().checkProgress();

        assertEquals(859, ro.getRemainingTime());
    }
}

