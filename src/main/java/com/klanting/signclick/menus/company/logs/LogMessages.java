package com.klanting.signclick.menus.company.logs;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.logs.PluginLogs;
import com.klanting.signclick.menus.SelectionMenu;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LogMessages extends SelectionMenu {
    public Company comp;
    private final PluginLogs pluginLog;

    public LogMessages(Company company, PluginLogs pluginLog){
        super(54, "Company Log Messages", true);
        comp = company;
        this.pluginLog = pluginLog;

        init();
    }

    public void init(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for(ImmutablePair<LocalDateTime, String> log : pluginLog.getLogs()){
            ItemStack logItem = new ItemStack(Material.PAPER, 1);
            ItemMeta m = logItem.getItemMeta();

            List<String> messages = List.of(log.getRight().split("\n"));

            m.setDisplayName("§7["+log.getLeft().format(formatter)+"] "+messages.get(0));

            messages.replaceAll(s -> "§7" + s);

            m.setLore(messages.subList(1, messages.size()));

            logItem.setItemMeta(m);
            getInventory().setItem(getInventory().firstEmpty(), logItem);
        }
    }
}
