package tools;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.plugin.PluginManagerMock;
import com.klanting.signclick.SignClick;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.gradle.internal.impldep.org.testng.ITest;

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

        assertNotNull(SignClick.getEconomy());
        assertNotNull(plugin.getServer().getPluginManager().getPlugin("Vault"));

        return plugin;
    }

    public static PlayerMock addPermsPlayer(ServerMock server, SignClick plugin){

        PlayerMock testPlayer = server.addPlayer();
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

    public static void assertSaid(PlayerMock player, String expected){
        assertEquals(expected.replace("\n", "-"), player.nextMessage().replace("\n", "-"));
    }
}
