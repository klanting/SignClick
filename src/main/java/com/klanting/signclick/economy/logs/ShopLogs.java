package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.text.DecimalFormat;
import java.time.*;
import java.util.*;


class ItemSummary{
    public int amount = 0;
    public double price = 0;

}

public class ShopLogs extends PluginLogs{

    /*
    * log map: date -> block -> amount, to keep logs for each day as a summary
    * */
    public final Map<LocalDate, List<ShopLogEntry>> shopSalesMap = new HashMap<>();

    public ShopLogs(){
        super("Shop logs");
    }
    @Override
    public void update(String action, Object message, UUID issuer) {
        if (!action.equals("Shop sales")){
            return;
        }

        if(!(message instanceof ShopLogEntry shopLogEntry)){
            return;
        }

        Instant now = Instant.now();
        LocalDate ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault()).toLocalDate();

        /*
        * add new log to the given date
        * */
        List<ShopLogEntry> dailyShopLogs = shopSalesMap.getOrDefault(ldt, new ArrayList<>());
        dailyShopLogs.add(shopLogEntry);

        shopSalesMap.put(ldt, dailyShopLogs);
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        List<MutableTriple<LocalDateTime, String, String>> shopLogsEntries = new ArrayList<>();


        for(Map.Entry<LocalDate, List<ShopLogEntry>> entry: shopSalesMap.entrySet()){
            /*
            * make a summary by item
            * */
            Map<Material,  ItemSummary> summaryMap = new HashMap<>();

            for(ShopLogEntry shopLogEntry: entry.getValue()){
                ItemSummary iSum = summaryMap.getOrDefault(shopLogEntry.item(), new ItemSummary());

                iSum.amount += shopLogEntry.amount();
                iSum.price += shopLogEntry.price();

                summaryMap.put(shopLogEntry.item(), iSum);
            }

            String mess = "";

            DecimalFormat df = new DecimalFormat("###,###,##0.00");

            for(Map.Entry<Material, ItemSummary> iSum: summaryMap.entrySet()){
                mess += "§7"+iSum.getValue().amount+"x "+ iSum.getKey().name()+" sold for $"+df.format(iSum.getValue().price)+"\n";
            }

            LocalTime time = LocalTime.of(0, 0); // 2:30 PM

            shopLogsEntries.add(MutableTriple.of(
                    entry.getKey().atTime(time),
                    "Shop Sales",
                    mess));
        }

        return shopLogsEntries;
    }
}
