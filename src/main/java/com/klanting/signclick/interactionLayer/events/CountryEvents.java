package com.klanting.signclick.interactionLayer.events;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.routines.WeeklyPay;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

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
        * add each player to the right scoreboard team for sorting
        * */
        for (Player player: Bukkit.getOnlinePlayers()){
            Country country = CountryManager.getCountry(player);

            Team team;
            if (country == null){
                team = SignClick.scoreboard.getTeam("zzz_default");
            }else{
                team = SignClick.scoreboard.getTeam(country.getName());
            }

            if(team == null){
                team = SignClick.scoreboard.registerNewTeam(country.getName());
            }

            team.addPlayer(player);
            player.setScoreboard(SignClick.scoreboard);
        }



    }
}
