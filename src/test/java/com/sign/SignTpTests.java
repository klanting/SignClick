package com.sign;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.TestTools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignTpTests {
    private ServerMock server;
    private SignClick plugin;
    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        /*Create country*/
        testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
    }

    @Test
    public void doSignTp(){
        boolean result = server.execute("signclickpos", testPlayer).hasSucceeded();
        assertTrue(result);

        /*
        * Save signclick position
        * */
        World world = server.addSimpleWorld("world");
        testPlayer.setLocation(new Location(world, 10, 10, 10));
        assertEquals("§bposition saved", testPlayer.nextMessage());
        testPlayer.assertNoMoreSaid();


        /*
        * Create sign
        * */
        Location signLocation = new Location(world, 20, 10, 10);
        BlockMock block = (BlockMock) signLocation.getBlock();
        block.setType(Material.OAK_SIGN);

        String[] lines = new String[]{"[sign_tp]", "", "", ""};


        SignChangeEvent signChangeEvent = new SignChangeEvent(block, testPlayer, lines);
        server.getPluginManager().callEvent(signChangeEvent);

        block = (BlockMock) signLocation.getBlock();

        Sign sign = (Sign) block.getState();

        assertEquals("§b[sign_tp]", sign.getLine(0));
        SignClick.getEconomy().depositPlayer(testPlayer, Double.parseDouble(sign.getLine(3)));

        /*
        * Click on sign
        * */
        PlayerInteractEvent interactEvent = new PlayerInteractEvent(
                testPlayer,
                Action.RIGHT_CLICK_BLOCK,
                new ItemStack(Material.AIR),
                block,
                null
        );

        // Trigger the event
        server.getPluginManager().callEvent(interactEvent);

        assertEquals("§bYou are now Teleported", testPlayer.nextMessage());
        testPlayer.assertNoMoreSaid();
        assertEquals(0, testPlayer.getLocation().getX());


    }
}
