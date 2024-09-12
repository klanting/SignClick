package com.klanting.signclick.Calculate;

import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

public class WeeklyPay {
    public static Map<UUID, Map<String, Integer>> payments = new HashMap<UUID, Map<String, Integer>>();
    public static Map<UUID, List<String>> offcheck = new HashMap<UUID, List<String>>();


    public static void check(){
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(SignClick.getPlugin(), new Runnable() {
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

            int PCT_amount;
            try {
                Player target = Bukkit.getServer().getPlayer(receiver);

                String bank = Banking.Element(target);
                if (bank != "none"){
                    PCT_amount = (int) (amount * ((double) Banking.getPCT(bank))/100.0);
                    Banking.deposit(bank,PCT_amount);
                }else{
                    PCT_amount = 0;
                }
                SignClick.getEconomy().depositPlayer(target, (amount-PCT_amount));
            } catch (Exception e) {
                for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                    if (target.getName().equals(receiver)) {
                        String bank = Banking.Element(target);
                        if (bank != "none"){
                            PCT_amount = (int) (amount * ((double) Banking.getPCT(bank))/100.0);
                            Banking.deposit(bank,PCT_amount);
                        }else{
                            PCT_amount = 0;
                        }
                        SignClick.getEconomy().depositPlayer(target, (amount-PCT_amount));
                    }
                }
            }
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
        StringBuilder income = new StringBuilder("§bincoming: ");
        payments.keySet().forEach(key->{
            Map<String, Integer> map = payments.get(key);
            if (map.containsKey(player.getName())){
                try{
                    income.append("\n§a").append(Bukkit.getServer().getPlayer(key).getName()).append(": §7").append(map.get(player.getName()));

                }catch (Exception e){
                    for (OfflinePlayer oplayer : Bukkit.getOfflinePlayers()){
                        if (key.equals(oplayer.getUniqueId())){
                            income.append("§a").append(oplayer.getName()).append(": §7").append(map.get(player.getName()));
                        }
                    }


                }

            }
        });
        income.append("\n");

        income.append("§boutgoing: ");
        if (payments.containsKey(player.getUniqueId())){
            Map<String, Integer> outgoing = payments.get(player.getUniqueId());
            outgoing.keySet().forEach(key ->{
                income.append("\n§c").append(key).append(": §7").append(outgoing.get(key));
            });
        }
        player.sendMessage(String.valueOf(income));
    }

    public static void offlinelist(Player p, OfflinePlayer player){
        StringBuilder income = new StringBuilder("§bincome: ");
        payments.keySet().forEach(key->{
            Map<String, Integer> map = payments.get(key);
            if (map.containsKey(player.getName())){
                income.append("\n§a").append(Bukkit.getServer().getPlayer(key).getName()).append(": §7").append(map.get(player.getName()));
            }
        });
        p.sendMessage(String.valueOf(income));

        StringBuilder outcome = new StringBuilder("\n§boutgoing: ");
        if (payments.containsKey(player.getUniqueId())){
            Map<String, Integer> outgoing = payments.get(player.getUniqueId());
            outgoing.keySet().forEach(key ->{
                outcome.append("\n§c").append(key).append(": §7").append(outgoing.get(key));
            });
        }
        p.sendMessage(String.valueOf(outcome));
    }

    public static List<String> receivers(Player player){
        if (payments.containsKey(player.getUniqueId())){
            Map<String, Integer> map = payments.get(player.getUniqueId());
            return new ArrayList<String>(map.keySet());
        }
        return null;
    }

    public static void send(String pl, String message){

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().equals(pl)) {
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
        }

    }

    public static void save(){
        for(UUID uuid : payments.keySet()) {
            SignClick.getPlugin().getConfig().set("weekly."+uuid, payments.get(uuid));
        }
        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }

    public static void restore(){
        if (SignClick.getPlugin().getConfig().contains("weekly")){
            SignClick.getPlugin().getConfig().get("weekly");
                SignClick.getPlugin().getConfig().getConfigurationSection("weekly").getKeys(false).forEach(key -> {
                    Map<String, Integer> map = new HashMap<>();

                    SignClick.getPlugin().getConfig().getConfigurationSection("weekly."+key).getKeys(false).forEach(key2 -> {
                    map.put(key2, (Integer) SignClick.getPlugin().getConfig().get("weekly."+key+"."+key2));
                    });
                    payments.put(UUID.fromString(key), map);
                });

        }
    }
}
