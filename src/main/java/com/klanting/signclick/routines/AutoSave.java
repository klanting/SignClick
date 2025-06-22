package com.klanting.signclick.routines;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.companyPatent.Auction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import static org.bukkit.Bukkit.getServer;

public class AutoSave {
    public static void start(){

        long delay = SignClick.getPlugin().getConfig().getLong("autoSaveInterval");

        if (delay <= 0){
            getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "SignClick: AutoSave has been disabled");
            return;
        }

        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {

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
}
