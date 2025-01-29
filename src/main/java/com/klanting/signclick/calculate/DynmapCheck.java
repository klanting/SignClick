package com.klanting.signclick.calculate;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;


public class DynmapCheck {
    public static void Hide(){
        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {
            public void run() {
                for (Player player: Bukkit.getOnlinePlayers()){
                    if (!SignClick.getDynmap().getPlayerVisbility(player)){
                        int amount = 1000;
                        if (SignClick.getEconomy().getBalance(player) >= amount) {
                            SignClick.getEconomy().withdrawPlayer(player, amount);
                            Country country = CountryManager.getCountry(player);
                            if (country != null){
                                country.deposit(amount);
                            }else{
                                List<Country> list = CountryManager.getCountries();
                                if (list.size() == 0){
                                    continue;
                                }
                                Random rand = new Random();
                                list.get(rand.nextInt(list.size())).deposit(amount);
                            }



                        }else{
                            SignClick.getDynmap().setPlayerVisiblity(player, true);
                            player.sendMessage("§c you couldn't pay the money, so now you are visible on the dynmap");
                        }

                    }
                }

            }},60*10*20,60*10*20);


    }
}
