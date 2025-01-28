package com.klanting.signclick.calculate;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;


public class WeeklyComp {
    public static long systemEnd = 60*60*24*7*20L;
    public static void check(){
        systemEnd = Utils.readSave("weeklyComp",
                new TypeToken<Long>(){}.getType(), 60*60*24*7*20L);

        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {

            public void run() {
                CountryManager.runLawSalary();
                Market.resetSpendable();
                Market.resetPatentCrafted();
                Market.runDividends();
                Market.runContracts();
                Market.runStockCompare();
                Market.runWeeklyCompanySalary();
                CountryManager.runStability();

            }
        }, systemEnd,60*60*24*7*20);

        systemEnd = (System.currentTimeMillis()/1000) % 60*60*24*7*20;

    }

    public static void Save(){
        Utils.writeSave("weeklyComp", systemEnd);
    }

}
