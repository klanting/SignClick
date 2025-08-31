package com.klanting.signclick.events;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.routines.WeeklyPay;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

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

        sortTab();

    }

    public static void sortTab(){
        /*
         * Sort player by country
         * */
        List<Player> sortedPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        sortedPlayers.sort((p1, p2) -> {
            // Example: sort by country name first, then by player name
            Country c1 = CountryManager.getCountry(p1);
            Country c2 = CountryManager.getCountry(p2);
            String name1 = p1.getName();
            String name2 = p2.getName();

            if (c1 != null && c2 != null) {
                int cmp = c1.getName().compareToIgnoreCase(c2.getName());

                if (cmp == 0){
                    return name1.compareToIgnoreCase(name2);
                }

                return cmp;
            }

            /*
             * put players with country first
             * */
            if (c1 != null) {return -1;}
            if (c2 != null) {return 1;}

            return name1.compareToIgnoreCase(name2);
        });

        // Update the player list order by resetting each player's list name
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Player sorted : sortedPlayers) {
                // This triggers the tab list to refresh in order
                p.showPlayer(SignClick.getPlugin(), sorted);
            }
        }
    }
}
