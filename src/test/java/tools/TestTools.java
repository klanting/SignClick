package tools;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.plugin.PluginManagerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.events.MenuEvents;
import com.klanting.signclick.routines.WeeklyPay;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.companyPatent.Auction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;

import java.util.*;

import static groovy.test.GroovyTestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestTools {
    public static SignClick setupPlugin(ServerMock server){
        PluginManagerMock pluginManager = server.getPluginManager();

        //dynmap mock
        Plugin dynmap = MockBukkit.createMockPlugin("dynmap");
        pluginManager.enablePlugin(dynmap);

        // Mock the Vault plugin
        Plugin vault = MockBukkit.createMockPlugin("Vault");
        pluginManager.enablePlugin(vault);
        MockEconomy mockEconomy = new MockEconomy();
        MockDynmap mockDynmap = new MockDynmap();

        // add Mock Vault to server
        server.getServicesManager().register(Economy.class, mockEconomy, vault, org.bukkit.plugin.ServicePriority.Highest);
        server.getServicesManager().register(DynmapAPI.class, mockDynmap, vault, org.bukkit.plugin.ServicePriority.Highest);


        SignClick plugin = MockBukkit.load(SignClick.class);
        SignClick.getPlugin().getConfig().set("autoSaveInterval", 0);

        assertNotNull(SignClick.getEconomy());
        assertNotNull(plugin.getServer().getPluginManager().getPlugin("Vault"));

        return plugin;
    }

    public static PlayerMock addPermsPlayer(ServerMock server, SignClick plugin){
        ExpandedServerMock server2 = (ExpandedServerMock) server;

        PlayerMock testPlayer = server2.addPlayer(true);
        testPlayer.addAttachment(plugin, "signclick.staff", true);
        return testPlayer;
    }

    public static ItemStack craftItemShapeless(ServerMock server, ItemStack[] items){

        List<ItemStack> itemChoices = new ArrayList<>();
        for (ItemStack item: items){
            itemChoices.add(item);
        }

        Iterator<Recipe> recipeIterator = server.recipeIterator();

        while (recipeIterator.hasNext()){
            Recipe r = recipeIterator.next();
            if (r instanceof ShapelessRecipe){
                ShapelessRecipe sr = (ShapelessRecipe) r;
                List<ItemStack> requirements = sr.getIngredientList();

                if (requirements.equals(itemChoices)){
                    return sr.getResult();
                }

            }
        }

        return null;
    }

    public static SignClick reboot(ServerMock serverMock) {
        disable(serverMock);
        return TestTools.setupPlugin(serverMock);
    }

    public static void disable(ServerMock serverMock) {
        Plugin plugin = serverMock.getPluginCommand("SignClick").getPlugin();
        plugin.onDisable();
        CountryManager.clear();
        Auction.clear();
        Market.clear();
        MenuEvents.activeMachines.clear();
        WeeklyPay.payments.clear();
    }


    public static void printInventory(InventoryView inv){
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

    public static void assertItem(ItemStack item, Material expectedMaterial, String expectedName){
        assertItem(item, expectedMaterial, expectedName, null);
    }

    public static void assertItem(ItemStack item, Material expectedMaterial, String expectedName,
                                  List<String> expectedLore){
        assertEquals(expectedMaterial, item.getType());
        assertEquals(expectedName, item.getItemMeta().getDisplayName());
        if (expectedLore != null){
            assertEquals(expectedLore, item.getItemMeta().getLore());
        }

    }

}
