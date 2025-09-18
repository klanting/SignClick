package com.klanting.signclick.interactionLayer.routines;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

public class WeeklyPay {
    public static Map<UUID, Map<String, Integer>> payments = new HashMap<UUID, Map<String, Integer>>();
    public static Map<UUID, List<String>> offcheck = new HashMap<>();


    public static void check(){
        Bukkit.getServer().getScheduler().runTaskTimer(SignClick.getPlugin(), new Runnable() {
            public void run() {
                payments.keySet().forEach(n ->{

                   Map<String, Integer> map = payments.get(n);

                    HashMap<String, Integer> mapCopy = new HashMap<>(map);

                    mapCopy.keySet().forEach(r ->{
                        boolean suc6 = pay(n, r, mapCopy.get(r));

                        if (suc6){
                            for (OfflinePlayer oplayer : Bukkit.getOfflinePlayers()){
                                if (n.equals(oplayer.getUniqueId())){
                                    send(r, "§a[weeklypay] you weekly received "+mapCopy.get(r)+" from "+oplayer.getName());
                                    send(oplayer.getName(), "§c[weeklypay] you weekly paid "+mapCopy.get(r)+" to"+ r);
                                }
                            }
                        }


                    });
                });

            }},60*60*24*7*20,60*60*24*7*20);
    }

    public static boolean pay(UUID uuid, String receiver, int amount){

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (SignClick.getEconomy().getBalance(player) >= amount) {

            OfflinePlayer finalTarget = null;
            for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                if (target.getName().equals(receiver)) {
                    finalTarget = target;
                }
            }

            if (finalTarget == null){
                SignClick.getPlugin().getLogger().log(Level.SEVERE, "player corresponding to name "+receiver+" could not be found");
                return false;
            }

            int PCT_amount;
            Country country = CountryManager.getCountry(finalTarget);

            if (country != null){

                PCT_amount = (int) (amount * country.getTaxRate());
                country.deposit(PCT_amount);
            }else{
                PCT_amount = 0;
            }

            SignClick.getEconomy().depositPlayer(finalTarget, (amount-PCT_amount));
            SignClick.getEconomy().withdrawPlayer(player, amount);
            return true;
        }else{
            stop(Objects.requireNonNull(Bukkit.getPlayer(uuid)),receiver);
            return false;
        }


    }

    public static void start(Player player, String receiver, int amount){
        Map<String, Integer> target_payment;
        if (amount < 0){
            return;
        }
        if (payments.containsKey(player.getUniqueId())){
            target_payment = payments.get(player.getUniqueId());
        }else{
            target_payment = new HashMap<String, Integer>();
        }
        if (target_payment.containsKey(receiver)){
            int current = target_payment.get(receiver);
            target_payment.put(receiver, (amount+current));
        }else{
            target_payment.put(receiver, amount);
        }
        payments.put(player.getUniqueId(), target_payment);
        player.sendMessage("§byou succesfully started your weekly pay");
        send(receiver, "§a[weeklypay] you got a new payment from §7"+ player.getName()
                + "§a \n with an amount of §7"
                + amount);


    }

    public static void stop (Player player, String receiver){
        if (payments.containsKey(player.getUniqueId())){
            Map<String, Integer> map = payments.get(player.getUniqueId());
            if (map.containsKey(receiver)){
                map.remove(receiver);
                player.sendMessage("§bweekly payment has been cancelled");
                send(receiver, "§c[weeklypay] you lost a payment from §7"+ player.getName());
            }
        }
    }

    public static void list(Player player){
        list(player, player);
    }

    public static void list(Player player, OfflinePlayer informationTarget){
        StringBuilder income = new StringBuilder("§bincoming: ");
        payments.keySet().forEach(key->{

            Map<String, Integer> map = payments.get(key);
            if (map.containsKey(informationTarget.getName())){
                OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(key);
                income.append("\n§a").append(otherPlayer.getName()).append(": §7").append(map.get(informationTarget.getName()));

            }
        });
        income.append("\n");

        income.append("§boutgoing: ");
        if (payments.containsKey(informationTarget.getUniqueId())){
            Map<String, Integer> outgoing = payments.get(informationTarget.getUniqueId());
            outgoing.keySet().forEach(key ->{
                income.append("\n§c").append(key).append(": §7").append(outgoing.get(key));
            });
        }
        player.sendMessage(String.valueOf(income));
    }

    public static List<String> receivers(Player player){
        if (payments.containsKey(player.getUniqueId())){
            Map<String, Integer> map = payments.get(player.getUniqueId());
            return new ArrayList<>(map.keySet());
        }
        return null;
    }

    public static void send(String pl, String message){

        OfflinePlayer player = Bukkit.getOfflinePlayer(pl);

        if (player.isOnline()){
            Player p = Bukkit.getPlayerExact(pl);
            p.sendMessage(message);
        }else{
            List<String> lst;
            if (offcheck.containsKey(player.getUniqueId())){
                lst = offcheck.get(player.getUniqueId());
            }else{
                lst = new ArrayList<>();
            }
            lst.add(message);
            offcheck.put(player.getUniqueId(), lst);
        }
    }

    public static void save(){
        Utils.writeSave("weeklyPayments", payments);
    }

    public static void restore(){

        payments = Utils.readSave("weeklyPayments",
                new TypeToken<HashMap<UUID, Map<String, Integer>>>(){}.getType(), new HashMap<>());

    }
}
