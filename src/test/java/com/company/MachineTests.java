package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.Product;
import com.klanting.signclick.events.MenuEvents;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.block.TileState;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.*;

import static org.junit.jupiter.api.Assertions.*;

public class MachineTests {
    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Market.clear();
    }

    @Test
    void basicMachineProduction(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        /*
        * Create machine and produce an item
        * */
        Market.addCompany("TCI", "TCI", Market.getAccount(testPlayer));
        Company comp = Market.getCompany("TCI");
        comp.setSpendable(2000);
        comp.addProduct(new Product(Material.DIRT, 1, 1));

        /*
        * Build the Machine
        * */

        ItemStack[] matrix = new ItemStack[9];

        matrix[0] = new ItemStack(Material.IRON_INGOT);
        matrix[1] = new ItemStack(Material.IRON_INGOT);
        matrix[2] = new ItemStack(Material.IRON_INGOT);
        matrix[3] = new ItemStack(Material.IRON_BLOCK);
        matrix[4] = new ItemStack(Material.FURNACE);
        matrix[5] = new ItemStack(Material.IRON_BLOCK);
        matrix[6] = new ItemStack(Material.IRON_BLOCK);
        matrix[7] = new ItemStack(Material.IRON_BARS);
        matrix[8] = new ItemStack(Material.IRON_BLOCK);

        ItemStack machine = Utils.simulateCraft(matrix);

        assertEquals(Material.BLAST_FURNACE, machine.getType());

        testPlayer.setItemInHand(machine);

        BlockMock blockClicked = new BlockMock(Material.DIRT,
                new Location(server.getWorld("world"), 0, 0, 0));
        BlockMock machineBlock = new DoubleBlockMock(machine.getType(),
                new Location(new WorldDoubleMock(), 0, 0, 0));
        FurnaceStateMock furnaceState = (new FurnaceStateMock(machineBlock));
        assertTrue(furnaceState instanceof TileState);
        machineBlock.setState(furnaceState);
        assertNotNull(machineBlock.getState());

        BlockPlaceEvent event = new BlockPlaceEvent(
                machineBlock,
                machineBlock.getState(),
                blockClicked,
                testPlayer.getInventory().getItemInMainHand(),
                testPlayer,
                true
        );

        server.getPluginManager().callEvent(event);

        assertEquals(Material.BLAST_FURNACE, machineBlock.getType());

        InventoryOpenEvent event2 = new InventoryOpenEvent(
                testPlayer.openInventory(furnaceState.getInventory())
        );

        server.getPluginManager().callEvent(event2);

        /*
        * Have list of companies
        * */
        InventoryView inv = testPlayer.getOpenInventory();
        assertEquals(Material.SUNFLOWER, inv.getItem(0).getType());
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, inv.getItem(1).getType());

        testPlayer.simulateInventoryClick(0);

        /*
        * See the machine view
        * */
        inv = testPlayer.getOpenInventory();

        /*
        * Check no product assigned yet
        * */
        assertEquals(Material.LIGHT_GRAY_DYE, inv.getItem(10).getType());

        /*
        * Assign dirt as item
        * */
        testPlayer.simulateInventoryClick(10);
        assertEquals(Material.DIRT, testPlayer.getOpenInventory().getItem(0).getType());
        testPlayer.simulateInventoryClick(0);

        inv = testPlayer.getOpenInventory();
        assertEquals(Material.DIRT, inv.getItem(10).getType());

        /*
        * Check production is working
        * */
        assertEquals(0, furnaceState.getCookTime());

        server.getScheduler().performTicks(200);
        assertEquals(new ItemStack(Material.DIRT, 10), furnaceState.getInventory().getResult());

        MenuEvents.furnaces.clear();

    }
}
