package com.klanting.signclick.events;

import com.klanting.signclick.Calculate.WeeklyPay;
import com.klanting.signclick.Economy.Banking;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CountryEvents implements Listener {
    @EventHandler
    public static void OnJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (WeeklyPay.offcheck.containsKey(player.getUniqueId())){
            if (WeeklyPay.offcheck.get(player.getUniqueId()).size() < 20){
                for (String s : WeeklyPay.offcheck.get(player.getUniqueId())){
                    player.sendMessage(s);
                }
                WeeklyPay.offcheck.remove(player.getUniqueId());
            }
        }
        if (Banking.Element(player) != null){
            String bank = Banking.Element(player);
            player.setPlayerListName(Banking.GetColor(bank)+player.getName());
        }



    }
}
