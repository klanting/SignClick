package com.klanting.signclick.routines;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;

import static org.bukkit.Bukkit.getServer;


public class WeeklyComp {
    public static long systemEnd;
    public static long checkCycle = 60*60*24*7*20L;
    public static void check(){

        systemEnd = Utils.readSave("weeklyComp",
                new TypeToken<Long>(){}.getType(), checkCycle);

        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {

            public void run() {
                CountryManager.runLawSalary();
                Market.resetPatentCrafted();
                Market.runDividends();
                Market.runContracts();
                Market.runStockCompare();
                Market.runWeeklyCompanySalary();
                CountryManager.runStability();

            }
        }, systemEnd,checkCycle);

        systemEnd = getServer().getCurrentTick() + checkCycle;

    }

    public static void Save(){
        Utils.writeSave("weeklyComp", systemEnd-getServer().getCurrentTick());
    }

}
