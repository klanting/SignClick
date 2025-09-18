package com.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.Market;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import static org.junit.Assert.assertEquals;

public class CraftingSimulation {
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
    public void simulateCrafting(){

        /*
        * Custom recipe (Shapeless)
        * */
        ShapelessRecipe recip = new ShapelessRecipe(NamespacedKey.minecraft("touch"), new ItemStack(Material.CRAFTING_TABLE));
        recip.addIngredient(new ItemStack(Material.OAK_SIGN));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        recip.addIngredient(new ItemStack(Material.OAK_PLANKS));
        server.addRecipe(recip);


        ItemStack[] matrix = new ItemStack[9];
        matrix[0] = new ItemStack(Material.OAK_PLANKS);
        matrix[1] = new ItemStack(Material.OAK_SIGN);
        matrix[3] = new ItemStack(Material.OAK_PLANKS);
        matrix[4] = new ItemStack(Material.OAK_PLANKS);


        ItemStack craftedItem = Utils.simulateCraft(matrix);
        assertEquals(Material.CRAFTING_TABLE, craftedItem.getType());

        /*
         * Custom recipe (Shaped)
         * */
        ShapedRecipe something = (new ShapedRecipe(NamespacedKey.minecraft("touch2"), new ItemStack(Material.DIRT)));
        something.shape("AAA", "BCB", "BD ");
        something.setIngredient('A', Material.IRON_INGOT);
        something.setIngredient('B', Material.IRON_BLOCK);
        something.setIngredient('C', Material.FURNACE);
        something.setIngredient('D', Material.IRON_BARS);
        server.addRecipe(something);

        matrix = new ItemStack[9];
        matrix[0] = new ItemStack(Material.IRON_INGOT);
        matrix[1] = new ItemStack(Material.IRON_INGOT);
        matrix[2] = new ItemStack(Material.IRON_INGOT);
        matrix[3] = new ItemStack(Material.IRON_BLOCK);
        matrix[4] = new ItemStack(Material.FURNACE);
        matrix[5] = new ItemStack(Material.IRON_BLOCK);
        matrix[6] = new ItemStack(Material.IRON_BLOCK);
        matrix[7] = new ItemStack(Material.IRON_BARS);

        craftedItem = Utils.simulateCraft(matrix);
        assertEquals(Material.DIRT, craftedItem.getType());

        /*
        * Custom recipe, like iron bars, with height independence
        * */
        something = (new ShapedRecipe(NamespacedKey.minecraft("touch3"), new ItemStack(Material.IRON_BARS)));
        something.shape("XXX", "XXX");
        something.setIngredient('X', Material.IRON_INGOT);
        server.addRecipe(something);

        matrix = new ItemStack[9];
        matrix[0] = new ItemStack(Material.IRON_INGOT);
        matrix[1] = new ItemStack(Material.IRON_INGOT);
        matrix[2] = new ItemStack(Material.IRON_INGOT);
        matrix[3] = new ItemStack(Material.IRON_INGOT);
        matrix[4] = new ItemStack(Material.IRON_INGOT);
        matrix[5] = new ItemStack(Material.IRON_INGOT);

        craftedItem = Utils.simulateCraft(matrix);
        assertEquals(Material.IRON_BARS, craftedItem.getType());
    }
}
