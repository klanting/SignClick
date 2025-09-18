package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.patent.Patent;
import com.klanting.signclick.logicLayer.companyLogic.patent.PatentUpgrade;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PatentDesignerMenu extends SelectionMenu {

    public CompanyI comp;

    public Patent patent;

    public PatentDesignerMenu(Patent patent, CompanyI comp){
        super(27, "Company Patent Designer", true, 17);
        this.comp = comp;
        this.patent = patent;
        init();
    }
    public void init(){
        ItemStack item = new ItemStack(patent.item, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(patent.getName());
        item.setItemMeta(meta);
        getInventory().setItem(4, item);
        int upgrades = comp.getUpgrades().get(1).getBonus();
        for (int i=0; i<upgrades; i++){

            ItemStack upgrade;
            if (patent.upgrades.size() > i){
                PatentUpgrade patentUpgrade = patent.upgrades.get(i);
                upgrade = new ItemStack(patentUpgrade.material, 1);

                ItemMeta m = upgrade.getItemMeta();
                m.setDisplayName(patentUpgrade.name + " "+patentUpgrade.level);
                upgrade.setItemMeta(m);

            }else{
                upgrade = new ItemStack(Material.LIGHT_GRAY_DYE, 1);

                ItemMeta m = upgrade.getItemMeta();
                m.setDisplayName("§6Empty Upgrade");
                upgrade.setItemMeta(m);
            }



            getInventory().setItem(18+i, upgrade);
        }

        ItemStack save_button = new ItemStack(Material.LIME_WOOL, 1);
        ItemMeta m = save_button.getItemMeta();
        m.setDisplayName("§aSave");
        save_button.setItemMeta(m);
        getInventory().setItem(8, save_button);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        Patent pat = patent;

        String option = event.getCurrentItem().getItemMeta().getDisplayName();
        if (option.equalsIgnoreCase("§aSave")){
            if (!comp.getPatent().contains(patent)){
                comp.getPatent().add(patent);
                patent.createCraft(comp);
            }

            PatentIDMenu new_screen = new PatentIDMenu(comp, true);
            player.openInventory(new_screen.getInventory());
            return false;
        }
        if (event.getCurrentItem().getType().equals(Material.NAME_TAG)){
            pat.setName(event.getCurrentItem().getItemMeta().getDisplayName());
            init();
            return false;
        }

        if (event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_DYE)){
            PatentDesignerUpgrade new_screen = new PatentDesignerUpgrade(pat, comp);
            player.openInventory(new_screen.getInventory());
        }

        return true;
    }
}
