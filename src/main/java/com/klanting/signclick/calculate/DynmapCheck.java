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
        long delay = SignClick.getPlugin().getConfig().getLong("dynmapTaxPeriod");
        int amount = SignClick.getPlugin().getConfig().getInt("dynmapTaxAmount");

        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {
            public void run() {
                for (Player player: Bukkit.getOnlinePlayers()){
                    if (SignClick.getDynmap().getPlayerVisbility(player)){
                        continue;
                    }

                    if (SignClick.getEconomy().getBalance(player) < amount) {
                        SignClick.getDynmap().setPlayerVisiblity(player, true);
                        player.sendMessage("§cYou couldn't pay the money, so now you are visible on the dynmap");
                        continue;
                    }

                    SignClick.getEconomy().withdrawPlayer(player, amount);
                    Country country = CountryManager.getCountry(player);
                    if (country != null){
                        country.deposit(amount);
                    }else{
                        List<Country> list = CountryManager.getCountries();
                        if (list.isEmpty()){
                            continue;
                        }
                        Random rand = new Random();
                        list.get(rand.nextInt(list.size())).deposit(amount);
                    }
                }

            }},delay*20,delay*20);


    }
}
