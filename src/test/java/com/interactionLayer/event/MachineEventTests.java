package com.interactionLayer.event;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.CreeperMock;
import be.seeseemelk.mockbukkit.entity.ItemEntityMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.events.MenuEvents;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.companyLogic.producible.LicenseSingleton;
import com.klanting.signclick.recipes.MachineRecipe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Creeper;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MachineEventTests {
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

        suc6 = Market.addCompany("TestCaseInc2", "TCI2", Market.getAccount(testPlayer));
        assertTrue(suc6);
        Market.getCompany("TCI2").setType("Nature");

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        Market.clear();
        LicenseSingleton.clear();
        MenuEvents.activeMachines.clear();
    }

    @Test
    void testCreeperExplosionEvent(){
        WorldDoubleMock world = new WorldDoubleMock();
        server.addWorld(world);

        Creeper creeper = new CreeperMock(server, UUID.randomUUID());

        Location location = new Location(world, 100, 64, 100);

        /*
        * place machine
        * */
        ItemStack machine = MachineRecipe.item();
        assertEquals(Material.BLAST_FURNACE, machine.getType());

        /*
        * setup all for the block
        * */
        Location loc = new Location(world, 0, 1, 0);
        BlockMock machineBlock = new DoubleBlockMock(machine.getType(), loc);
        FurnaceStateMock furnaceState = (new FurnaceStateMock(machineBlock));
        assertTrue(furnaceState instanceof TileState);
        machineBlock.setState(furnaceState);
        assertNotNull(machineBlock.getState());
        world.setBlock(machineBlock, loc);
        assertSame(machineBlock, loc.getBlock());

        BlockPlaceEvent event = new BlockPlaceEvent(
                machineBlock,
                machineBlock.getState(),
                null,
                machine,
                testPlayer,
                true
        );
        server.getPluginManager().callEvent(event);

        List<Block> explodedBlocks = new ArrayList<>();
        explodedBlocks.add(event.getBlock());
        explodedBlocks.add(new BlockMock(Material.DIRT, new Location(world, 0, 0, 0)));
        // Create an explosion event manually
        EntityExplodeEvent event2 = new EntityExplodeEvent(
                creeper,
                location,
                explodedBlocks, // blocks affected
                4.0F               // yield
        );

        server.getPluginManager().callEvent(event2);

        /*
        * check only the dirt block is in the removed list
        * */
        assertEquals(1, explodedBlocks.size());
        Collection<ItemEntityMock> droppedItems = world.getEntitiesByClass(ItemEntityMock.class);
        assertEquals(1, droppedItems.size());


    }
}
