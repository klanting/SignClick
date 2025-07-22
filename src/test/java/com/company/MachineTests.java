package com.company;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.*;
import com.klanting.signclick.events.MenuEvents;
import com.klanting.signclick.menus.company.LicenseInfoMenu;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.TileState;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
        if (!MenuEvents.furnaces.isEmpty()){
            MenuEvents.furnaces.get(0).getBlock().getLocation().setWorld(server.getWorld("world"));
        }

        MockBukkit.unmock();
        Market.clear();
        LicenseSingleton.clear();
        MenuEvents.furnaces.clear();
    }

    @Test
    void basicMachineProduction(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        /*
        * Create machine and produce an item
        * */
        Market.addCompany("TCI", "TCI", Market.getAccount(testPlayer));
        CompanyI comp = Market.getCompany("TCI");
        comp.addBal(2000);
        comp.setSpendable(2000);
        comp.addProduct(new Product(Material.DIRT, 1, 2));

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
        assertEquals(0, MenuEvents.furnaces.get(0).getProductionProgress());
        MenuEvents.furnaces.get(0).getBlock().getLocation().setWorld(new WorldDoubleMock());
        MenuEvents.furnaces.get(0).changeProductionLoop();

        server.getScheduler().performTicks(220);
        assertEquals(1, MenuEvents.furnaces.get(0).getProductionProgress());

        assertEquals(new ItemStack(Material.DIRT, 5), MenuEvents.furnaces.get(0).results[0]);

    }

    @Test
    void MachineProductionSaveLoad(){
        basicMachineProduction();

        plugin = TestTools.reboot(server);
        MenuEvents.furnaces.get(0).getBlock().getLocation().setWorld(new WorldDoubleMock());

        assertEquals(1, Market.getCompany("TCI").getMachines().values().size());
        assertEquals(1, MenuEvents.furnaces.size());

        assertEquals(1, MenuEvents.furnaces.get(0).getProductionProgress());
        assertEquals(new ItemStack(Material.DIRT, 5), MenuEvents.furnaces.get(0).results[0]);

        server.getScheduler().performTicks(180);
        assertEquals(0, MenuEvents.furnaces.get(0).getProductionProgress());
        assertEquals(new ItemStack(Material.DIRT, 10), MenuEvents.furnaces.get(0).results[0]);
    }

    @Test
    void licenseMachineProduction(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        /*
         * Create machine and produce an licensed item
         * */
        Market.addCompany("TCI", "TCI", Market.getAccount(testPlayer));
        Market.addCompany("TCI2", "TCI2", Market.getAccount(testPlayer));
        CompanyI comp = Market.getCompany("TCI");
        comp.addBal(2000);
        comp.setSpendable(2000);

        CompanyI comp2 = Market.getCompany("TCI2");
        Product product = new Product(Material.DIRT, 1, 2);
        comp2.addProduct(product);
        License license = new License(comp2, comp, product,
                1.0, 0.0, 0.0);
        LicenseSingleton.getInstance().getCurrentLicenses().addLicense(license);

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
        assertEquals("§6TCI [TCI]", inv.getItem(0).getItemMeta().getDisplayName());
        assertEquals(Material.SUNFLOWER, inv.getItem(1).getType());
        assertEquals(Material.LIGHT_GRAY_STAINED_GLASS_PANE, inv.getItem(2).getType());

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
        assertTrue(testPlayer.getOpenInventory().getItem(0).getItemMeta().getLore().contains("§cThis Product is Licensed"));
        testPlayer.simulateInventoryClick(0);

        inv = testPlayer.getOpenInventory();
        assertEquals(Material.DIRT, inv.getItem(10).getType());
        assertTrue(inv.getItem(10).getItemMeta().getLore().contains("§cThis Product is Licensed"));


        /*
         * Check production is working
         * */
        assertEquals(0, MenuEvents.furnaces.get(0).getProductionProgress());
        MenuEvents.furnaces.get(0).getBlock().getLocation().setWorld(new WorldDoubleMock());
        MenuEvents.furnaces.get(0).changeProductionLoop();

        server.getScheduler().performTicks(220);
        assertEquals(1, MenuEvents.furnaces.get(0).getProductionProgress());

        assertEquals(new ItemStack(Material.DIRT, 5), MenuEvents.furnaces.get(0).results[0]);

        /*
        * Remove the license
        * */
        testPlayer.openInventory((new LicenseInfoMenu(license)).getInventory());
        assertEquals(1, LicenseSingleton.getInstance().getCurrentLicenses().getLicensesFrom(comp2).size());
        testPlayer.simulateInventoryClick(7);
        assertEquals(0, LicenseSingleton.getInstance().getCurrentLicenses().getLicensesFrom(comp2).size());

        assertEquals(0, MenuEvents.furnaces.size());

        event2 = new InventoryOpenEvent(
                testPlayer.openInventory(furnaceState.getInventory())
        );

        server.getPluginManager().callEvent(event2);
        inv = testPlayer.getOpenInventory();
        assertEquals(Material.LIGHT_GRAY_DYE, inv.getItem(10).getType());

    }
}
