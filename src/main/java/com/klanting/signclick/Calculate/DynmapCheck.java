package com.klanting.signclick.Calculate;

import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;


public class DynmapCheck {
    public static void Hide(){
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(SignClick.getPlugin(), new Runnable() {
            public void run() {
                for (Player player: Bukkit.getOnlinePlayers()){
                    if (!SignClick.getDynmap().getPlayerVisbility(player)){
                        int amount = 1000;
                        if (SignClick.getEconomy().getBalance(player) >= amount) {
                            SignClick.getEconomy().withdrawPlayer(player, amount);
                            if (Banking.Element(player) != "none"){
                                Banking.deposit(Banking.Element(player), amount);
                            }else{
                                List<String> list = Banking.GetBanks();
                                Random rand = new Random();
                                Banking.deposit(list.get(rand.nextInt(list.size())), amount);
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
