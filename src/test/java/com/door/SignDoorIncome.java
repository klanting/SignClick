package com.door;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Market;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.DoorMock;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.gradle.internal.impldep.org.junit.Assert.assertFalse;
import static org.gradle.internal.impldep.org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignDoorIncome {
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
    public void createSignIncomeDoor(){
        World world = server.addSimpleWorld("world");

        /*
         * Create door
         * */
        Location doorLocation = new Location(world, 20, 11, 10);
        BlockMock block = (BlockMock) doorLocation.getBlock();
        block.setType(Material.OAK_DOOR);
        block.setBlockData(new DoorMock(Material.OAK_DOOR));

        doorLocation = new Location(world, 20, 10, 10);
        block = (BlockMock) doorLocation.getBlock();
        block.setType(Material.OAK_DOOR);
        block.setBlockData(new DoorMock(Material.OAK_DOOR));


        /*
         * Create sign
         * */
        Location signLocation = new Location(world, 20, 11, 9);
        BlockMock signBlock = (BlockMock) signLocation.getBlock();
        signBlock.setType(Material.OAK_WALL_SIGN);

        WallSign sign = (WallSign) signBlock.getBlockData();
        sign.setFacing(BlockFace.NORTH);
        signBlock.setBlockData(sign);

        String[] lines = new String[]{"[sign_in]", "100", "", ""};

        SignChangeEvent signChangeEvent = new SignChangeEvent(signBlock, testPlayer, lines);
        server.getPluginManager().callEvent(signChangeEvent);

        /*
        * check sign updated
        * */

        Sign signData = (Sign) signBlock.getState();

        assertEquals("§b[sign_in]", signData.getLine(0));
        assertEquals("100", signData.getLine(1));
        assertEquals(testPlayer.getName(), signData.getLine(2));
        assertEquals("", signData.getLine(3));

        /*
        * Click sign
        * */
        PlayerMock testPlayer2 = server.addPlayer();
        SignClick.getEconomy().depositPlayer(testPlayer2, 100);
        assertEquals(100, SignClick.getEconomy().getBalance(testPlayer2));
        Door door = (Door) block.getBlockData();
        assertFalse(door.isOpen());

        PlayerInteractEvent interactEvent = new PlayerInteractEvent(
                testPlayer2,
                Action.RIGHT_CLICK_BLOCK,
                new ItemStack(Material.AIR),
                signBlock,
                null
        );
        server.getPluginManager().callEvent(interactEvent);

        /*
        * Check door opened
        * */
        assertTrue(door.isOpen());
        assertEquals(0, SignClick.getEconomy().getBalance(testPlayer2));

        /*
        * Check door automatically closed
        * */
        server.getScheduler().performTicks(5*20L+1);
        assertFalse(door.isOpen());
    }

    @Test
    public void createSignIncomeDoorNoPrice(){
        World world = server.addSimpleWorld("world");

        /*
         * Create door
         * */
        Location doorLocation = new Location(world, 20, 10, 10);
        BlockMock block = (BlockMock) doorLocation.getBlock();
        block.setType(Material.OAK_DOOR);

        /*
         * Create sign
         * */
        Location signLocation = new Location(world, 20, 11, 9);
        BlockMock signBlock = (BlockMock) signLocation.getBlock();
        signBlock.setType(Material.OAK_WALL_SIGN);

        WallSign sign = (WallSign) signBlock.getBlockData();
        sign.setFacing(BlockFace.NORTH);
        signBlock.setBlockData(sign);

        String[] lines = new String[]{"[sign_in]", "", "", ""};

        SignChangeEvent signChangeEvent = new SignChangeEvent(signBlock, testPlayer, lines);
        server.getPluginManager().callEvent(signChangeEvent);

        /*
         * check sign updated
         * */

        Sign signData = (Sign) signBlock.getState();

        testPlayer.assertSaid("§bThe 2nd line needs a price");
        testPlayer.assertNoMoreSaid();

        assertEquals("", signData.getLine(0));
        assertEquals("", signData.getLine(1));
        assertEquals("", signData.getLine(2));
        assertEquals("", signData.getLine(3));

    }

}
