package com.klanting.signclick.Calculate;

import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.Economy.CompanyPatent.Auction;
import com.klanting.signclick.Economy.Market;
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
                    if (Auction.bits_owner.get(i) == null){
                        continue;
                    }

                    Company comp = Market.get_business(Auction.bits_owner.get(i));
                    comp.patent_upgrades.add(Auction.to_buy.get(i));
                }

                Auction.init();
                time_end = System.currentTimeMillis()/(60*60*24*7*20);

            }
        },start_time,60*60*24*7*20);

    }
}
