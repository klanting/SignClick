package com.klanting.signclick.Economy.CompanyPatent;

import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class Patent {
    private String name;
    public Material item;

    public ArrayList<PatentUpgrade> upgrades;

    public String getName() {
        return name;
    }

    public Patent(String name, Material item, ArrayList<PatentUpgrade> upgrades){
        this.name = name;
        this.item = item;
        this.upgrades = upgrades;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void createCraft(Company comp){
        ItemStack pre_made_gear = new ItemStack(item, 1);
        ItemMeta m = pre_made_gear.getItemMeta();
        m.setDisplayName("§6"+comp.Sname+":"+name+":"+comp.patent.indexOf(this));
        pre_made_gear.setItemMeta(m);

        ShapelessRecipe pre_made_recip = new ShapelessRecipe(NamespacedKey.minecraft("primitive_patent-"+comp.Sname.toLowerCase()+"-"+name.toLowerCase()+"-"+comp.patent.indexOf(this)), pre_made_gear);

        ItemStack patent_paper = new ItemStack(Material.PAPER, 1);

        m = patent_paper.getItemMeta();
        m.setDisplayName("§6"+comp.Sname+":"+name+":"+comp.patent.indexOf(this));
        patent_paper.setItemMeta(m);

        RecipeChoice patent_paper_type = new RecipeChoice.ExactChoice(patent_paper);

        pre_made_recip.addIngredient(1, item);
        pre_made_recip.addIngredient(patent_paper_type);

        getServer().addRecipe(pre_made_recip);

        ItemStack final_item = new ItemStack(item, 1);
        ArrayList<String> upgrade_text = new ArrayList<>();

        double jumper_chance = 0;
        double evade_chance = 0;
        double refill_chance = 0;
        double cunning_chance = 0;
        for (PatentUpgrade up: upgrades){
            if (up.name.contains("Texture")){
                upgrade_text.add("§7"+up.name.substring(2));
            }else{
                upgrade_text.add("§7"+up.name.substring(2)+" "+up.level);
            }

            if (up.id == 0){
                jumper_chance += up.getBonus();
            }else if(up.id == 1){
                evade_chance += up.getBonus();
            }else if(up.id == 2){
                refill_chance += up.getBonus();
            }else if(up.id == 3){
                cunning_chance += up.getBonus();
            }
        }

        if (jumper_chance > 0.0){
            upgrade_text.add("§9JumpBonus: "+Math.round(jumper_chance*10.0)/10.0);
        }

        if (evade_chance > 0.0){

            upgrade_text.add("§9EvadeBonus: "+Math.round(evade_chance*10.0)/10.0);
        }

        if (refill_chance > 0.0){

            upgrade_text.add("§9RefillBonus: "+Math.round(refill_chance*10.0)/10.0);
        }

        if (cunning_chance > 0.0){

            upgrade_text.add("§9CunningBonus: "+Math.round(cunning_chance*10.0)/10.0);
        }


        m = final_item.getItemMeta();
        m.setDisplayName("§6"+name);
        m.setLore(upgrade_text);
        final_item.setItemMeta(m);

        ShapelessRecipe recip = new ShapelessRecipe(NamespacedKey.minecraft("patent-"+comp.Sname.toLowerCase()+"-"+name.toLowerCase()+"-"+comp.patent.indexOf(this)), final_item);

        RecipeChoice patent_gear_type = new RecipeChoice.ExactChoice(pre_made_gear);
        recip.addIngredient(patent_gear_type);
        for (PatentUpgrade up: upgrades){
            recip.addIngredient(up.material);
        }

        getServer().addRecipe(recip);
    }

    public void save(Company comp){
        String path = "company."+comp.Sname+".patent."+comp.patent.indexOf(this)+".";
        SignClick.getPlugin().getConfig().set(path+"name", name);
        SignClick.getPlugin().getConfig().set(path+"item", item.toString());

        List<String> index_list = new ArrayList<String>();
        for (PatentUpgrade up: upgrades){
            Integer index = comp.patentUpgrades.indexOf(up);
            index_list.add(index.toString());
        }

        SignClick.getPlugin().getConfig().set(path+"upgrades", index_list);
    }
}
