package com.klanting.signclick.interactionLayer.menus.company.patent;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.patent.Patent;
import com.klanting.signclick.logicLayer.companyLogic.patent.PatentUpgrade;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class PatentCrafting extends SelectionMenu {

    private ArrayList<Integer> indexes = new ArrayList<>();

    public CompanyI comp;

    public Patent patent;

    public PatentCrafting(CompanyI comp, Patent patent){
        super(27, "Company Upgrade Menu", true);

        this.comp = comp;
        this.patent = patent;
        indexes.add(3);
        indexes.add(4);
        indexes.add(5);
        indexes.add(12);
        indexes.add(14);
        indexes.add(21);
        indexes.add(22);
        indexes.add(23);

        init();
    }

    @Override
    public void init() {
        ItemStack gearItem = ItemFactory.create(patent.item,
                "§6"+comp.getStockName() +":"+patent.getName()+":"+comp.getPatent().indexOf(patent));

        getInventory().setItem(13, gearItem);

        int counter = 0;
        for (PatentUpgrade up: patent.upgrades){
            ItemStack item = new ItemStack(up.material, 1);
            getInventory().setItem(indexes.get(counter), item);
            counter++;
        }

        ItemStack item = ItemFactory.create(Material.PAPER, "§6Get Patent Sheet");

        getInventory().setItem(8, item);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        event.setCancelled(true);
        String option = event.getCurrentItem().getItemMeta().getDisplayName();
        if (option.equalsIgnoreCase("§6Get Patent Sheet")){
            Player player = (Player) event.getWhoClicked();

            ItemStack item = new ItemStack(Material.PAPER, 1);

            ItemMeta m = item.getItemMeta();
            m.setDisplayName("§6"+comp.getStockName() +":"+patent.getName()+":"+comp.getPatent().indexOf(patent));
            item.setItemMeta(m);

            player.getInventory().setItem(player.getInventory().firstEmpty(), item);
        }
        return true;
    }
}
