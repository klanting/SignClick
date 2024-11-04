package com.klanting.signclick.calculate;

import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;


public class WeeklyComp {
    public static long system_end = 60*60*24*7*20L;
    public static void check(){
        if (SignClick.getPlugin().getConfig().contains("weekly_comp")){
            system_end = (int) SignClick.getPlugin().getConfig().get("weekly_comp");
        }
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
        },system_end,60*60*24*7*20);

        system_end = (System.currentTimeMillis()/1000) % 60*60*24*7*20;

    }

    public static void Save(){
        SignClick.getPlugin().getConfig().set("weekly_comp", system_end);
    }

}
