package com.klanting.signclick.menus.company.logs;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.logs.PluginLogs;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LogList extends SelectionMenu {

    public CompanyI comp;

    public LogList(CompanyI company){
        super(9, "Company Logs", true);
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
}
