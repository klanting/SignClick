package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.companyPatent.PatentUpgradeCustom;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CompanyPatentDesignerUpgrade extends SelectionMenu {

    public Company comp;

    public Patent patent;

    public ArrayList<PatentUpgrade> pat_list = new ArrayList<>();

    public CompanyPatentDesignerUpgrade(Patent patent, Company comp){
        super(54, "Patent Designer Upgrade", true);
        this.comp = comp;
        this.patent = patent;
        init();

    }

    public void init(){
        int counter = 0;
        for (PatentUpgrade patent_up: comp.patentUpgrades){

            if (patent_up instanceof PatentUpgradeCustom){
                PatentUpgradeCustom p = (PatentUpgradeCustom) patent_up;
                if (patent.item != p.applied_item){
                    return;
                }

            }

            if (!patent.upgrades.contains(patent_up)){
                pat_list.add(patent_up);

                ItemStack item = new ItemStack(patent_up.material, 1);
                ItemMeta m = item.getItemMeta();
                m.setDisplayName(patent_up.name+" "+patent_up.level);
                item.setItemMeta(m);
                getInventory().setItem(counter, item);
                counter++;
            }
        }
    }
}
