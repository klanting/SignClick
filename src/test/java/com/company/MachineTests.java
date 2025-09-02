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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import tools.*;

import static org.junit.jupiter.api.Assertions.*;

public class MachineTests {
    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());
        server.addSimpleWorld("world");

        plugin = TestTools.setupPlugin(server);
    }

    @AfterEach
    public void tearDown() {
        if (!MenuEvents.activeMachines.isEmpty()){
            MenuEvents.activeMachines.get(0).getBlock().getLocation().setWorld(server.getWorld("world"));
        }

        MockBukkit.unmock();
        Market.clear();
        LicenseSingleton.clear();
        MenuEvents.activeMachines.clear();
    }

    @Test
    void basicMachineProduction(){
        server.addSimpleWorld("world");
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

        BlockMock blockClicked = new DoubleBlockMock(Material.DIRT,
                new Location(new WorldDoubleMock(), 0, 1, 0));
        BlockMock machineBlock = new DoubleBlockMock(machine.getType(),
                new Location(new WorldDoubleMock(), 0, 1, 0));
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
        assertEquals(0, MenuEvents.activeMachines.get(0).getProductionProgress());
        MenuEvents.activeMachines.get(0).getBlock().getLocation().setWorld(new WorldDoubleMock());
        MenuEvents.activeMachines.get(0).changeProductionLoop();

        server.getScheduler().performTicks(220);
        assertEquals(1, MenuEvents.activeMachines.get(0).getProductionProgress());

        assertEquals(new ItemStack(Material.DIRT, 5), MenuEvents.activeMachines.get(0).results[0]);

    }

    @Test
    void MachineItemOut(){
        /*
         * take item out of the machine
         * */
        basicMachineProduction();

        PlayerMock testPlayer = server.getPlayer(0);

        testPlayer.simulateInventoryClick(16);
        assertEquals(new ItemStack(Material.DIRT, 5), testPlayer.getItemOnCursor());
    }

    @Test
    void MachineItemOutFail(){
        /*
         * take item out of the machine, but have an item on your cursor, in this case you can't take the item
         * */
        basicMachineProduction();

        PlayerMock testPlayer = server.getPlayer(0);
        testPlayer.setItemOnCursor(new ItemStack(Material.PAPER, 1));

        testPlayer.simulateInventoryClick(16);
        assertEquals(new ItemStack(Material.DIRT, 5), MenuEvents.activeMachines.get(0).results[0]);
        assertEquals(new ItemStack(Material.PAPER, 1), testPlayer.getItemOnCursor());
    }

    @Test
    void MachineStopProducing(){
        /*
         * When the item is taken out, it machine should stop producing
         * */
        basicMachineProduction();

        PlayerMock testPlayer = server.getPlayer(0);
        Machine machine = MenuEvents.activeMachines.get(0);
        assertEquals(new ItemStack(Material.DIRT, 5), machine.results[0]);

        /*
        * remove selection
        * */
        assertEquals(Material.DIRT, testPlayer.getOpenInventory().getItem(10).getType());
        testPlayer.simulateInventoryClick(10);
        assertEquals(Material.LIGHT_GRAY_DYE, testPlayer.getOpenInventory().getItem(10).getType());

        CompanyI comp = Market.getCompany("TCI");
        double balBefore = comp.getBal();

        /*
        * Perform some time to produce some more items, which should be produced
        * */
        assertEquals(new ItemStack(Material.DIRT, 5), machine.results[0]);
        server.getScheduler().performTicks(220);

        assertEquals(new ItemStack(Material.DIRT, 5), machine.results[0]);
        assertEquals(balBefore, comp.getBal());
        assertEquals(0, MenuEvents.activeMachines.size());

    }

    @Test
    void MachineProductionSaveLoad(){
        server.addSimpleWorld("world");
        basicMachineProduction();

        plugin = TestTools.reboot(server);
        MenuEvents.activeMachines.get(0).getBlock().getLocation().setWorld(new WorldDoubleMock());

        assertEquals(1, Market.getCompany("TCI").getMachines().values().size());
        assertEquals(1, MenuEvents.activeMachines.size());

        assertEquals(1, MenuEvents.activeMachines.get(0).getProductionProgress());
        assertEquals(new ItemStack(Material.DIRT, 5), MenuEvents.activeMachines.get(0).results[0]);

        server.getScheduler().performTicks(180);
        assertEquals(0, MenuEvents.activeMachines.get(0).getProductionProgress());
        assertEquals(new ItemStack(Material.DIRT, 10), MenuEvents.activeMachines.get(0).results[0]);
    }

    @Test
    void licenseMachineProduction(){
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);
        server.addSimpleWorld("world");

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
                1.0, 0.0, 0.1);
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

        BlockMock blockClicked = new DoubleBlockMock(Material.DIRT,
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
        assertEquals(0, MenuEvents.activeMachines.get(0).getProductionProgress());
        MenuEvents.activeMachines.get(0).getBlock().getLocation().setWorld(new WorldDoubleMock());
        MenuEvents.activeMachines.get(0).changeProductionLoop();

        server.getScheduler().performTicks(220);
        assertEquals(1, MenuEvents.activeMachines.get(0).getProductionProgress());

        assertEquals(new ItemStack(Material.DIRT, 5), MenuEvents.activeMachines.get(0).results[0]);
        assertEquals(Math.round((1995-(5*0.1))*1000), Math.round(comp.getBal()*1000));
        assertEquals(Math.round((5*0.1)*1000), Math.round(comp2.getBal()*1000));

        /*
        * Remove the license
        * */
        testPlayer.openInventory((new LicenseInfoMenu(license)).getInventory());
        assertEquals(1, LicenseSingleton.getInstance().getCurrentLicenses().getLicensesFrom(comp2).size());
        testPlayer.simulateInventoryClick(7);
        assertEquals(0, LicenseSingleton.getInstance().getCurrentLicenses().getLicensesFrom(comp2).size());

        assertEquals(0, MenuEvents.activeMachines.size());

        event2 = new InventoryOpenEvent(
                testPlayer.openInventory(furnaceState.getInventory())
        );

        server.getPluginManager().callEvent(event2);
        inv = testPlayer.getOpenInventory();
        assertEquals(Material.LIGHT_GRAY_DYE, inv.getItem(10).getType());

    }

    @Test
    void machineProductionHopper(){
        server.addSimpleWorld("world");
        /*
        * test that the machine produced items go into a hopper.
        **/
        basicMachineProduction();
        Machine activeMachine = MenuEvents.activeMachines.get(0);
        activeMachine.hopperAllowed = true;
        Block blockBelow = activeMachine.getBlock().getRelative(BlockFace.DOWN);
        blockBelow.setType(Material.HOPPER);
        server.getScheduler().performTicks(1);

        /*
        * check item moved into the hopper
        * */
        assertNull(activeMachine.results[0]);
        assertEquals(new ItemStack(Material.DIRT, 6),
                ((Hopper) blockBelow.getState()).getInventory().getItem(0));
        assertNull(((Hopper) blockBelow.getState()).getInventory().getItem(8));
    }

    @ParameterizedTest
    @EnumSource(value = Material.class, names = { "STONE", "DIRT" })
    void machineProductionHopperFull(Material hopperItemName){
        machineProductionHopper();

        /*
        * Test that items don't disappear when the hopper is full
        * */
        Machine activeMachine = MenuEvents.activeMachines.get(0);
        assertTrue(activeMachine.hopperAllowed);

        Block blockBelow = activeMachine.getBlock().getRelative(BlockFace.DOWN);
        Hopper hopper = ((Hopper) blockBelow.getState());

        assertEquals(9, hopper.getInventory().getSize());

        /*
        * check hopper only has 6 dirt
        * */
        assertEquals(new ItemStack(Material.DIRT, 6), hopper.getInventory().getItem(0));
        for (int i=1; i<9; i++){
            assertEquals(null, hopper.getInventory().getItem(i));
        }

        /*
        * Make hopper full, with other items
        * */
        for (int i=0; i<9; i++){
            hopper.getInventory().setItem(i, new ItemStack(hopperItemName, 64));
        }

        /*
        * add dirt to machine result
        * */
        activeMachine.results[0] = new ItemStack(Material.DIRT, 1);
        server.getScheduler().performTicks(1);

        /*
        * Check item has not been moved
        * */
        hopper = ((Hopper) blockBelow.getState());
        assertNotNull(activeMachine.results[0]);
        for (int i=0; i<9; i++){
            assertEquals(new ItemStack(hopperItemName, 64), hopper.getInventory().getItem(i));
        }
    }
}
