package com.klanting.signclick.Economy;

import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class Account {

    public UUID uuid;
    public Map<String, Integer> shares = new HashMap<String, Integer>();
    boolean offer = false;

    public String compNamePending = null;
    public double compAmountPending = 0.0;
    public int compWeeksPending = 0;

    public String compReason = "no_reason";

    Account(UUID u){
        uuid = u;
    }

    public double getBal(){
        return SignClick.getEconomy().getBalance(Bukkit.getOfflinePlayer(uuid));

    }

    void addBal(double amount){
        SignClick.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(uuid), amount);
    }

    boolean removeBal(double amount){
        SignClick.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        return true;
    }

    public void buyShare(String Sname, Integer amount, Player player){
        double v = Market.get_buy_price(Sname, amount);
        if (getBal() >= v){
            if (Market.buy(Sname, amount, this)){
                removeBal(v);
                Market.get_business(Sname).add_books(v);

                Market.change_base(Sname);

                int share_amount = shares.getOrDefault(Sname, 0);
                shares.put(Sname, share_amount+amount);
                if (Market.get_business(Sname).openTrade){
                    Market.setTotal(Sname, Market.getTotal(Sname)+amount);
                }
                player.sendMessage("§bbuy: §aaccepted");
            }else{
                player.sendMessage("§bbuy: §cdenied (not enough shares on the market)");
            }

        }else{
            player.sendMessage("§bbuy: §cdenied (not enough money)");
        }
    }

    public void sellShare(String Sname, Integer amount, Player player){
        double v = Market.get_sell_price(Sname, amount);

        String countryName = Market.get_business(Sname).GetCountry();

        double sub_fee = Market.getFee();
        Country country = CountryManager.getCountry(countryName);

        double to_gov;
        //TODO fix this later
        if (country != null){
            if (country.getStability() < 50){
                sub_fee += 0.01;
            }

            to_gov = v/(1.0-(sub_fee- country.getPolicyBonus(0, 0))*(Market.getFee()- country.getPolicyBonus(0, 0)- country.getPolicyBonus(1, 1)));
            Country playerCountry = CountryManager.getCountry(player);
            if (playerCountry != null){
                playerCountry.deposit((int) to_gov);
            }else{
                Market.get_business(Sname).add_books(to_gov);
            }
        }else{
            to_gov = v/(1.0-sub_fee);
        }



        int share_amount = shares.getOrDefault(Sname, 0);
        if (share_amount >= amount){
            if (Market.sell(Sname, amount, this)){
                addBal(v);
                Market.get_business(Sname).remove_books(v+to_gov);
                Market.change_base(Sname);

                shares.put(Sname, share_amount-amount);
                player.sendMessage("§bsell: §aaccepted");
            }

        }
        else{
            player.sendMessage("§bsell: §cdenied (not enough shares)");
        }
    }

    void set_support(String Sname, UUID value){
        Market.get_business(Sname).support_update(this, value);

    }

    public Boolean transfer(String Sname, Integer amount, Account target, Player player){
        if (shares.getOrDefault(Sname, 0) >= amount){
            target.receive(Sname, amount, this, Objects.requireNonNull(Bukkit.getPlayer(target.uuid)));
            int share_amount = shares.get(Sname);
            shares.put(Sname, share_amount-amount);
            player.sendMessage("§btransfer: §aaccepted");
            return true;
        }else{
            player.sendMessage("§btransfer: §cdenied (not enough shares)");
            return false;
        }

    }

    public void receive(String Sname, Integer amount, Account sender, Player player){
        int share_amount = shares.getOrDefault(Sname, 0);
        shares.put(Sname, share_amount+amount);
        player.sendMessage("§breceived: "+amount+" shares for "+Sname+" from "+Bukkit.getPlayer(sender.uuid).getName());
    }

    public void receivePrivate(String Sname, Integer amount){
        int share_amount = shares.getOrDefault(Sname, 0);
        shares.put(Sname, share_amount+amount);
    }

    public void sendPlayer(String message){

            Player p = Bukkit.getPlayer(uuid);
            if (p != null){
                p.sendMessage(message);
            }


    }

    void offering(){
        //still need to do (next update)

    }

    public void getPortfolio(Player player){
        ArrayList<String> order = new ArrayList<String>();
        ArrayList<Double> values = new ArrayList<Double>();
        for(Map.Entry<String, Integer> entry : shares.entrySet()){
            String b = entry.getKey();
            int s = entry.getValue();
            double v = (Market.get_business(b).get_value()/Market.getTotal(b).doubleValue())*s;

            if (order.size() > 0){
                boolean found = false;

                for (int i=0; i<values.size(); i++){
                    double o = values.get(i);

                    if (v > o){
                        order.add(i, b);
                        values.add(i, v);
                        found = true;
                        break;
                    }
                }

                if (!found){
                    order.add(b);
                    values.add(v);
                }

            }else{
                order.add(b);
                values.add(v);
            }

        }

        player.sendMessage("§bportfolio:\n");
        double total = 0;
        for (int i=0; i<values.size(); i++){
            String b = order.get(i);
            double v = values.get(i);
            DecimalFormat df = new DecimalFormat("###,###,###");
            DecimalFormat df2 = new DecimalFormat("0.00");
            int i2 = i + 1;
            total += v;
            player.sendMessage("§b"+i2+". §3"+b+": §7" +df.format(v)+" ("+df2.format((shares.get(b).doubleValue()/Market.getTotal(b).doubleValue()*100.0))+"%)\n");
        }
        DecimalFormat df = new DecimalFormat("###,###,###");
        player.sendMessage("§9Total value: §e" +df.format(total));
        order.clear();
        values.clear();

    }

    public void accept_offer_comp_contract(){
        if (compNamePending == null){
            return;
        }

        Market.setContractPlayertoComp(uuid.toString(), compNamePending, compAmountPending, compWeeksPending, compReason);

        compNamePending = null;
        compAmountPending = 0.0;
        compWeeksPending = 0;
        compReason = "no_reason";

    }

    public void receive_offer_comp_contract(String stock_name, double amount, int weeks, String reason){
        compNamePending = stock_name;
        compAmountPending = amount;
        compWeeksPending = weeks;
        compReason = reason;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
            public void run() {
                compNamePending = null;
                compAmountPending = 0.0;
                compWeeksPending = 0;
                compReason = "no_reason";

            }
        }, 20*120L);

        Player p = Bukkit.getPlayer(uuid);
        if (p != null){
            p.sendMessage("§b you §7"+p.getName()+"§b got a contract request from §7" + stock_name
                    + "§b they will ask you §7"+amount+"§b for §7"+weeks+"§b weeks, do §c/company sign_contract_ptc ");
        }
    }


}
