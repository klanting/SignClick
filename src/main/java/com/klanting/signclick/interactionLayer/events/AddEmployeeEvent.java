package com.klanting.signclick.interactionLayer.events;

import com.klanting.signclick.logicLayer.CompanyOwnerManager;
import com.klanting.signclick.interactionLayer.menus.company.EmployeesList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class AddEmployeeEvent implements Listener {
    public static Map<Player, EmployeesList> waitForMessage = new HashMap<>();
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {

        if (waitForMessage.containsKey(event.getPlayer())){
            String name = event.getMessage();

            CompanyOwnerManager com  = waitForMessage.get(event.getPlayer()).comp.getCOM();

            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            if (target != null && target.getUniqueId() != null && !com.isEmployee(target.getUniqueId())){
                com.addEmployee(target.getUniqueId());
                waitForMessage.get(event.getPlayer()).init();
                event.getPlayer().openInventory(waitForMessage.get(event.getPlayer()).getInventory());

            }else{
                event.getPlayer().sendMessage("§bPlayer does not exist or is already an employee");
            }


            event.setCancelled(true);
            waitForMessage.remove(event.getPlayer());
        }

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        waitForMessage.remove(event.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        waitForMessage.remove(event.getPlayer());
    }
}

