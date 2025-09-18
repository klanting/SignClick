package com.klanting.signclick.interactionLayer.menus.company.logs;

import com.klanting.signclick.logicLayer.CompanyI;
import com.klanting.signclick.logicLayer.logs.PluginLogs;
import com.klanting.signclick.interactionLayer.menus.PagingMenu;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LogMessages extends PagingMenu {
    public CompanyI comp;
    private final PluginLogs pluginLog;

    public LogMessages(CompanyI company, PluginLogs pluginLog){
        super(54, "Company Log Messages", true);
        comp = company;
        this.pluginLog = pluginLog;

        init();
    }

    public void init(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        clearItems();

        for(MutableTriple<LocalDateTime, String, String> log : pluginLog.getLogs()){
            ItemStack logItem = new ItemStack(Material.PAPER, 1);
            ItemMeta m = logItem.getItemMeta();

            List<String> messages = new java.util.ArrayList<>(List.of(log.getRight().split(" ")));
            List<String> newMessages = new ArrayList<>();

            if(log.getRight().contains("\n")){
                newMessages = new java.util.ArrayList<>(List.of(log.getRight().split("\n")));
            }else{
                String prefix = "";
                if (messages.get(0).startsWith("§")){
                    prefix = messages.get(0).substring(0, 2);
                }

                int counter = 0;
                String current = "";
                for (String message: messages){
                    current += " ";
                    current += message;
                    counter += message.length();
                    if (counter >= 30){
                        newMessages.add(prefix+current);
                        current = "";
                        counter = 0;
                    }
                }

                if (current.length() > 0){
                    newMessages.add(prefix+current);
                }
            }

            m.setDisplayName("§7["+log.getLeft().format(formatter)+"] "+log.getMiddle());

            m.setLore(newMessages);

            logItem.setItemMeta(m);

            addItem(logItem);

        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }
        event.setCancelled(true);
        return false;
    }
}
