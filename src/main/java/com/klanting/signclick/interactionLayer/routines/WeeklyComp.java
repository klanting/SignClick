package com.klanting.signclick.interactionLayer.routines;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;
import versionCompatibility.CompatibleLayer;


public class WeeklyComp {
    public static long systemEnd;
    public static long checkCycle = 60*60*24*7*20L;
    public static void check(){
        systemEnd = Utils.readSave("weeklyComp",
                new TypeToken<Long>(){}.getType(), checkCycle);

        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {

            public void run() {
                CountryManager.runLawSalary();
                Market.runDividends();
                Market.runContracts();
                Market.runStockCompare();
                Market.runWeeklyCompanySalary();
                CountryManager.runStability();
                Market.runLicenses();

            }
        }, systemEnd,checkCycle);

        systemEnd = CompatibleLayer.getCurrentTick() + checkCycle;

    }

    public static void Save(){
        Utils.writeSave("weeklyComp", systemEnd-CompatibleLayer.getCurrentTick());
    }

}
