package com.sign;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Market;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignStockTests {
    private ServerMock server;
    private SignClick plugin;
    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        /*Create country*/
        testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Market.clear();
    }

    @Test
    public void setSignStock(){
        Market.addCompany("TCI", "TCI", Market.getAccount(testPlayer), 100000.0, "other");


        World world = server.addSimpleWorld("world");

        /*
         * Create sign
         * */
        Location signLocation = new Location(world, 20, 10, 10);
        BlockMock block = (BlockMock) signLocation.getBlock();
        block.setType(Material.OAK_SIGN);

        String[] lines = new String[]{"[stock]", "TCI", "", ""};

        SignChangeEvent signChangeEvent = new SignChangeEvent(block, testPlayer, lines);
        server.getPluginManager().callEvent(signChangeEvent);

        block = (BlockMock) signLocation.getBlock();

        Sign sign = (Sign) block.getState();

        assertEquals("§b[stock]", sign.getLine(0));
        assertEquals("TCI", sign.getLine(1));
        assertEquals("0,00", sign.getLine(2));
        assertEquals("", sign.getLine(3));

        testPlayer.assertSaid("§bStock sign is created and you have been charged 100k for making this sign");
        testPlayer.assertNoMoreSaid();
        assertEquals(1, Market.stockSigns.size());
    }

    @Test
    public void updateSignStock(){
        setSignStock();

        /*
        * Update sign stock positively
        * */
        server.getScheduler().performTicks(60*60*24*7*20L+1);

        Location signLocation = Market.stockSigns.get(0);
        BlockMock block = (BlockMock) signLocation.getBlock();

        Sign sign = (Sign) block.getState();

        assertEquals("§b[stock]", sign.getLine(0));
        assertEquals("TCI", sign.getLine(1));
        assertEquals("§a98,00", sign.getLine(2));
        assertEquals("§a$198.000", sign.getLine(3));

        /*
         * Update sign stock negatively
         * */
        server.getScheduler().performTicks(60*60*24*7*20L+1);

        signLocation = Market.stockSigns.get(0);
        block = (BlockMock) signLocation.getBlock();

        sign = (Sign) block.getState();

        assertEquals("§b[stock]", sign.getLine(0));
        assertEquals("TCI", sign.getLine(1));
        assertEquals("§c-1,00", sign.getLine(2));
        assertEquals("§c$196.020", sign.getLine(3));
    }

    @Test
    public void deleteSignStock(){
        setSignStock();
        Location signLocation = Market.stockSigns.get(0);
        BlockMock block = (BlockMock) signLocation.getBlock();

        BlockBreakEvent signBreakEvent = new BlockBreakEvent(block, testPlayer);
        server.getPluginManager().callEvent(signBreakEvent);

        assertEquals(0, Market.stockSigns.size());
    }

    @Test
    public void reloadSignStock(){
        setSignStock();

        /*
         * Restart Server, check persistence
         * */
        plugin = TestTools.reboot(server);

        assertEquals(1, Market.stockSigns.size());

    }
}