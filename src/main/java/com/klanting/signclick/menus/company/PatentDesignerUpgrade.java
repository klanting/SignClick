package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PatentDesignerUpgrade extends SelectionMenu {

    public CompanyI comp;

    public Patent patent;

    public ArrayList<PatentUpgrade> patentUpgradeList = new ArrayList<>();

    public PatentDesignerUpgrade(Patent patent, CompanyI comp){
        super(54, "Patent Designer Upgrade", true);
        this.comp = comp;
        this.patent = patent;
        init();

    }

    public void init(){
        int counter = 0;
        for (PatentUpgrade patent_up: comp.getPatentUpgrades()){

            if (!patent.upgrades.contains(patent_up)){
                patentUpgradeList.add(patent_up);

                ItemStack item = new ItemStack(patent_up.material, 1);
                ItemMeta m = item.getItemMeta();
                m.setDisplayName(patent_up.name+" "+patent_up.level);
                List<String> l = new ArrayList<>();
                l.addAll(patent_up.description());
                m.setLore(l);
                item.setItemMeta(m);
                getInventory().setItem(counter, item);
                counter++;
            }
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        event.setCancelled(true);
        PatentUpgrade pat_up = patentUpgradeList.get(event.getSlot());
        Patent pat = patent;
        pat.upgrades.add(pat_up);

        Player player = (Player) event.getWhoClicked();
        PatentDesignerMenu new_screen = new PatentDesignerMenu(pat, comp);
        player.openInventory(new_screen.getInventory());

        return true;
    }
}
