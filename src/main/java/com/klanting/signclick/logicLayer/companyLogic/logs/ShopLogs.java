package com.klanting.signclick.logicLayer.companyLogic.logs;

import org.apache.commons.lang3.tuple.MutableTriple;
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
    public final Map<LocalDate, List<itemLogEntry>> shopSalesMap = new HashMap<>();

    public ShopLogs(){
        super("Shop logs");
    }
    @Override
    public void update(String action, Object message, UUID issuer) {
        if (!action.equals("Shop sales")){
            return;
        }

        if(!(message instanceof itemLogEntry itemLogEntry)){
            return;
        }

        Instant now = Instant.now();
        LocalDate ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault()).toLocalDate();

        /*
        * add new log to the given date
        * */
        List<itemLogEntry> dailyShopLogs = shopSalesMap.getOrDefault(ldt, new ArrayList<>());
        dailyShopLogs.add(itemLogEntry);

        shopSalesMap.put(ldt, dailyShopLogs);
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        List<MutableTriple<LocalDateTime, String, String>> shopLogsEntries = new ArrayList<>();


        for(Map.Entry<LocalDate, List<itemLogEntry>> entry: shopSalesMap.entrySet()){
            /*
            * make a summary by item
            * */
            Map<Material,  ItemSummary> summaryMap = new HashMap<>();

            for(itemLogEntry itemLogEntry : entry.getValue()){
                ItemSummary iSum = summaryMap.getOrDefault(itemLogEntry.item(), new ItemSummary());

                iSum.amount += itemLogEntry.amount();
                iSum.price += itemLogEntry.price();

                summaryMap.put(itemLogEntry.item(), iSum);
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
