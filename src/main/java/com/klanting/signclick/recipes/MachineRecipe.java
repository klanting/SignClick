package com.klanting.signclick.recipes;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class MachineRecipe {

    public static ItemStack item(){
        NamespacedKey key = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine");
        NamespacedKey compKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_company");

        List<String> l = new ArrayList<>();
        l.add("§7Use Machine to produce items as a company");
        ItemStack machine = ItemFactory.create(Material.BLAST_FURNACE, "§bProduction Machine", l);

        ItemMeta meta = machine.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(compKey, PersistentDataType.STRING, "");
        machine.setItemMeta(meta);

        return machine;
    }

    public static void create(){

        NamespacedKey key = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine");
        ItemStack machine = item();


        ShapedRecipe something = (new ShapedRecipe(key, machine));
        something.shape("AAA", "BCB", "BDB");
        something.setIngredient('A', Material.IRON_INGOT);
        something.setIngredient('B', Material.IRON_BLOCK);
        something.setIngredient('C', Material.FURNACE);
        something.setIngredient('D', Material.IRON_BARS);


        getServer().addRecipe(something);
    }
}
