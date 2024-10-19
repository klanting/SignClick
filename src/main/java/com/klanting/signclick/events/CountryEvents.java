package com.klanting.signclick.events;

import com.klanting.signclick.calculate.WeeklyPay;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
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

        Country country = CountryManager.getCountry(player);
        if (country != null){
            player.setPlayerListName(country.getColor()+player.getName());
        }



    }
}
