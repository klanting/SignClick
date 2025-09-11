package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.Research;
import com.klanting.signclick.economy.ResearchOption;
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
                Market.getAccount(TestTools.addPermsPlayer(server, plugin)), 0.0, "bank");

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
                        getConfigurationSection("bank").getKeys(false).size(),
                research.getResearchOptions().size());

        assertEquals(Material.IRON_INGOT, research.getResearchOptions().get(0).getMaterial());
        assertEquals(0.0, research.getResearchOptions().get(0).getProgress());
        assertEquals(1200L, research.getResearchOptions().get(0).getCompleteTime());
    }

    @Test
    void doResearch(){
        /*
         * check research update
         * */
        Research research = new Research(comp.getRef());

        assertEquals(SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                        getConfigurationSection("bank").getKeys(false).size(),
                research.getResearchOptions().size());

        assertEquals(Material.IRON_INGOT, research.getResearchOptions().get(0).getMaterial());
        assertEquals(0.0, research.getResearchOptions().get(0).getProgress());
        assertEquals(1200L, research.getResearchOptions().get(0).getCompleteTime());

        research.getResearchOptions().get(0).setModifierIndex(1);
        Market.getCompany("TCI").addBal(1000);
        Market.getCompany("TCI").setSpendable(1000);

        /*
        * do 50% of the time
        * */
        server.getScheduler().performTicks(600*20L);
        research.checkProgress();

        /*
        * check 50% progress
        * */
        assertEquals(Material.IRON_INGOT, research.getResearchOptions().get(0).getMaterial());
        assertEquals(0.5, research.getResearchOptions().get(0).getProgress());
        assertEquals(Math.ceil(1000*(5.0/6)), Math.ceil(Market.getCompany("TCI").getBal()));

        assertEquals(0, Market.getCompany("TCI").getProducts().size());

        server.getScheduler().performTicks(600*20L + 6000);
        research.checkProgress();

        /*
         * check 100% progress
         * */
        assertEquals(Material.IRON_INGOT, research.getResearchOptions().get(0).getMaterial());
        assertEquals(1.0, research.getResearchOptions().get(0).getProgress());
        assertEquals(667, Math.ceil(Market.getCompany("TCI").getBal()));
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
        assertEquals(600, ro.getRemainingTime());

        server.getScheduler().performTicks(21L);
        comp.getResearch().checkProgress();

        server.getScheduler().performTicks(800L);
        comp.getResearch().checkProgress();

        assertEquals(559, ro.getRemainingTime());
    }
}

