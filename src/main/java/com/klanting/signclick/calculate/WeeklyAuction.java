package com.klanting.signclick.calculate;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;

public class WeeklyAuction {
    public static long start_time = 0;
    public static long time_end;
    public static void check(){
        time_end = (System.currentTimeMillis()/1000)+(60*60*24*7);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(SignClick.getPlugin(), new Runnable() {

            public void run() {
                for (int i=0; i<5; i++){
                    if (Auction.getInstance().bitsOwner.get(i) == null){
                        continue;
                    }

                    Company comp = Market.getBusiness(Auction.getInstance().bitsOwner.get(i));
                    comp.patentUpgrades.add(Auction.getInstance().toBuy.get(i));
                }

                Auction.getInstance().init();
                time_end = System.currentTimeMillis()/(60*60*24*7*20);

            }
        },start_time,60*60*24*7*20);

    }
}
