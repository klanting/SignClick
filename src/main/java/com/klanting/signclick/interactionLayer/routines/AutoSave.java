package com.klanting.signclick.interactionLayer.routines;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.CountryManager;
import com.klanting.signclick.logicLayer.Market;
import com.klanting.signclick.logicLayer.companyPatent.Auction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import static org.bukkit.Bukkit.getServer;

public class AutoSave {

    private static BukkitTask task;

    public static void start(){

        long delay = SignClick.getConfigManager().getConfig("general.yml").getLong("autoSaveInterval");

        if (delay <= 0){
            getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "SignClick: AutoSave has been disabled");
            return;
        }

        task = Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {

            public void run() {
                getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "SignClick: Conducting AutoSave");
                WeeklyPay.save();
                CountryManager.saveData();
                Market.SaveData();
                Auction.Save();
                WeeklyComp.Save();

            }
        }, delay*20l, delay*20l);

    }

    public static void stop(){
        task.cancel();
    }
}
