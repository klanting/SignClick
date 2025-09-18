package com.klanting.signclick.interactionLayer.menus.company.logs;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.logs.PluginLogs;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LogList extends SelectionMenu {

    public CompanyI comp;

    public LogList(CompanyI company){
        super(18, "Company Logs", true);
        comp = company;

        init();
    }

    public void init(){
        for(PluginLogs pluginLogs : comp.getLogObservers()){
            ItemStack logItem = new ItemStack(Material.PAPER, 1);
            ItemMeta m = logItem.getItemMeta();
            m.setDisplayName("§b"+pluginLogs.getTitle());
            logItem.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), logItem);
        }
        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        LogMessages new_screen = new LogMessages(comp, comp.getLogObservers().get(event.getSlot()));
        player.openInventory(new_screen.getInventory());

        return true;
    }
}
