package com.klanting.signclick.economy.logs;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.bukkit.Material;

import java.text.DecimalFormat;
import java.time.*;
import java.util.*;

public class MachineProduction extends PluginLogs{
    /*
     * log map: date -> block -> amount, to keep logs for each day as a summary
     * */
    public final Map<LocalDate, List<itemLogEntry>> machineProduction = new HashMap<>();

    public MachineProduction(){
        super("Machine production");
    }
    @Override
    public void update(String action, Object message, UUID issuer) {
        if (!action.equals("Machine production")){
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
        List<itemLogEntry> dailyShopLogs = machineProduction.getOrDefault(ldt, new ArrayList<>());
        dailyShopLogs.add(itemLogEntry);

        machineProduction.put(ldt, dailyShopLogs);
    }

    @Override
    public List<MutableTriple<LocalDateTime, String, String>> getLogs() {
        List<MutableTriple<LocalDateTime, String, String>> shopLogsEntries = new ArrayList<>();


        for(Map.Entry<LocalDate, List<itemLogEntry>> entry: machineProduction.entrySet()){
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

            StringBuilder mess = new StringBuilder();

            DecimalFormat df = new DecimalFormat("###,###,##0.00");

            for(Map.Entry<Material, ItemSummary> iSum: summaryMap.entrySet()){
                mess.append("§7").append(iSum.getValue().amount)
                        .append("x ").append(iSum.getKey().name()).
                        append(" created for $").append(df.format(iSum.getValue().price)).append("\n");
            }

            LocalTime time = LocalTime.of(0, 0); // 2:30 PM

            shopLogsEntries.add(MutableTriple.of(
                    entry.getKey().atTime(time),
                    "Machines production",
                    mess.toString()));
        }

        return shopLogsEntries;
    }
}
