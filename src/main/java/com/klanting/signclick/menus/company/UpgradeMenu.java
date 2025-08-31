package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.companyUpgrades.Upgrade;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class UpgradeMenu extends SelectionMenu {

    public CompanyI comp;

    public UpgradeMenu(UUID uuid, CompanyI company){
        super(27, "Company Upgrade Menu", true);
        comp = company;

        init();
    }

    public void init(){
        int counter = 10;
        for (Upgrade up: comp.getUpgrades()){
            ArrayList<String> l = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###,###,###");
            Country country = CountryManager.getCountry(comp.getCountry());

            if (up.getUpgradeCost() != -1){
                l.add("§6Cost: §f§n"+ df.format((double) up.getUpgradeCost()*comp.getUpgradeModifier()));
            }

            l.addAll(up.description());

            ItemStack value = ItemFactory.create(up.material, "§6§l"+up.name +"§r§6 Lvl. §c"+ up.level, l);

            getInventory().setItem(counter, value);
            counter ++;
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        boolean chief = comp.getCOM().getBoard().getChiefPermission("CEO").equals(player.getUniqueId());

        if (!chief){
            player.sendMessage("§cOnly the CEO has the permissions for this");
            return false;
        }

        int id = event.getSlot()-10;
        boolean suc6 = comp.doUpgrade(id);

        init();

        if (!suc6){
            player.sendMessage("§bNot enough Money or Points to do the upgrade");
        }
        return true;
    }
}
