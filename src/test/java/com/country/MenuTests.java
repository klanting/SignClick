package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenuTests {
    private ServerMock server;
    private SignClick plugin;

    private PlayerMock testPlayer;
    private InventoryView inventoryMenu;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

        Country c = CountryManager.create("empire1", testPlayer);
        assertNotNull(c);

        boolean suc6 = server.execute("country", testPlayer, "menu").hasSucceeded();
        assertTrue(suc6);

        inventoryMenu = testPlayer.getOpenInventory();
        assertNotNull(inventoryMenu);

        testPlayer.nextMessage();

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
    }

    void printInventory(InventoryView inv){
        /*
         * Menu testing debug tool
         * */
        for (int i=0; i<inv.countSlots(); i++){
            if (inv.getItem(i) == null){
                continue;
            }
            System.out.print(i+": "+inv.getItem(i).getType().toString()+"\n");
        }
    }

    private InventoryView openMenu(InventoryView inv, int slot){
        ItemStack option = inv.getItem(slot);
        assertNotNull(option);

        testPlayer.simulateInventoryClick(inv, slot);

        return testPlayer.getOpenInventory();
    }

    @Test
    void countryOpenMenu(){

        assertEquals("§6empire1", inventoryMenu.getItem(13).getItemMeta().getDisplayName());
        assertEquals(Material.GOLD_BLOCK, inventoryMenu.getItem(13).getType());

        assertEquals("§6Decisions", inventoryMenu.getItem(21).getItemMeta().getDisplayName());
        assertEquals(Material.PAPER, inventoryMenu.getItem(21).getType());

        assertEquals("§6Policy", inventoryMenu.getItem(22).getItemMeta().getDisplayName());
        assertEquals(Material.ANVIL, inventoryMenu.getItem(22).getType());

    }

    public void assertPolicy(InventoryView policyMenu, int[] setting){
        /*
         * Categories
         * */

        List<Object[]> policies = new ArrayList<>();

        policies.add(new Object[]{"§6Economical Policy", Material.GOLD_INGOT, 10});
        policies.add(new Object[]{"§6Market Policy", Material.EMERALD, 19});
        policies.add(new Object[]{"§6Military Policy", Material.IRON_SWORD, 28});
        policies.add(new Object[]{"§6Tourism Policy", Material.OAK_BOAT, 37});
        policies.add(new Object[]{"§6Taxation Policy", Material.GOLD_BLOCK, 46});


        for (int j=0; j<policies.size(); j++){
            Object[] o = policies.get(j);
            assertEquals(o[0], policyMenu.getItem((Integer) o[2]).getItemMeta().getDisplayName());
            assertEquals(o[1], policyMenu.getItem((Integer) o[2]).getType());

            for (int i=0; i<5; i++){

                Material glass;
                if (i == setting[j]){
                    glass = Material.LIME_STAINED_GLASS_PANE;
                }else{
                    glass = Material.RED_STAINED_GLASS_PANE;
                }

                assertEquals(glass, policyMenu.getItem((Integer) o[2]+i+1).getType());
            }
        }
    }

    @Test
    void countryOpenPolicy(){
        InventoryView policyMenu = openMenu(inventoryMenu, 22);
        assertNotNull(policyMenu);

        assertPolicy(policyMenu, new int[]{2, 2, 2, 2, 2});

        testPlayer.simulateInventoryClick(policyMenu, 14);

        testPlayer.assertSaid("§bPolicy change Decision has been passed on");
        testPlayer.assertNoMoreSaid();
    }

    @Test
    void countryChangePolicy(){
        countryOpenPolicy();

        boolean suc6 = server.execute("party", testPlayer, "vote").hasSucceeded();
        assertTrue(suc6);

        InventoryView voteMenu = testPlayer.getOpenInventory();
        assertNotNull(voteMenu);

        assertEquals("§6Policy §9Normal§6 to §9Invester", voteMenu.getItem((0)).getItemMeta().getDisplayName());
        assertEquals(Arrays.asList(
                "§7current approved: 0,00%",
                "§7current disapproved: 0,00%",
                "§7needed approved: 50,00%"), voteMenu.getItem((0)).getItemMeta().getLore());

        assertEquals(Material.PAPER, voteMenu.getItem(0).getType());
        testPlayer.simulateInventoryClick(voteMenu,  0);

        InventoryView voteMenuChoice = testPlayer.getOpenInventory();
        assertNotNull(voteMenuChoice);

        assertEquals(Material.LIME_WOOL, voteMenuChoice.getItem(11).getType());
        assertEquals(Material.RED_WOOL, voteMenuChoice.getItem(15).getType());

        testPlayer.simulateInventoryClick(voteMenuChoice, 11);

        /*
        * Check policy changed
        * */
        InventoryView policyMenu = openMenu(inventoryMenu, 22);
        assertNotNull(policyMenu);

        assertPolicy(policyMenu, new int[]{3, 2, 2, 2, 2});
    }
}