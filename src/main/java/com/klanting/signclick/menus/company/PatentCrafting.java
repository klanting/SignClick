package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class PatentCrafting extends SelectionMenu {

    private ArrayList<Integer> indexes = new ArrayList<>();

    public Company comp;

    public Patent patent;

    public PatentCrafting(Company comp, Patent patent){
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
                "§6"+comp.getStockName() +":"+patent.getName()+":"+comp.patent.indexOf(patent));

        getInventory().setItem(13, gearItem);

        int counter = 0;
        for (PatentUpgrade up: patent.upgrades){
            ItemStack item = new ItemStack(up.material, 1);
            getInventory().setItem(indexes.get(counter), item);
            counter++;
        }

        ItemStack item = ItemFactory.create(Material.PAPER, "§6Get Patent Sheet");

        getInventory().setItem(8, item);
    }
}
