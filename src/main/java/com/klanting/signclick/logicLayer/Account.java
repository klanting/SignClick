package com.klanting.signclick.logicLayer;

import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

import static com.klanting.signclick.utils.Utils.AssertMet;

public class Account {

    /*
    * This class defines a portfolio/account from a user
    * */

    private final UUID uuid;
    public Map<String, Integer> shares = new HashMap<String, Integer>();

    public String compNamePending = null;
    public double compAmountPending = 0.0;
    public int compWeeksPending = 0;

    public String compReason = "no_reason";

    public Account(UUID u){
        uuid = u;
    }

    public double getBal(){
        return SignClick.getEconomy().getBalance(Bukkit.getOfflinePlayer(uuid));

    }

    public void addBal(double amount){
        SignClick.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(uuid), amount);
    }

    public boolean removeBal(double amount){
        SignClick.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        return true;
    }

    public void buyShare(String Sname, Integer amount, Player player){

        double base = Market.getCompany(Sname).getShareBase();
        double v = Market.getBuyPrice(Sname, amount);
        if (getBal()<v){
            player.sendMessage(SignClick.getPrefix()+"buy: §cdenied (not enough money)");
            return;
        }

        if (!Market.buy(Sname, amount, this)){
            player.sendMessage(SignClick.getPrefix()+"buy: §cdenied (not enough shares on the market)");
            return;
        }

        removeBal(v);

        double toBal = base*amount*Market.calculateFluxChange(-10, 15);

        Market.getCompany(Sname).addShareBal(v-toBal);
        Market.getCompany(Sname).addBal(toBal);

        int share_amount = shares.getOrDefault(Sname, 0);
        shares.put(Sname, share_amount+amount);
        if (Market.getCompany(Sname).getCOM().isOpenTrade()){
            CompanyI comp = Market.getCompany(Sname);
            comp.setTotalShares(comp.getTotalShares()+amount);
        }

        Market.getCompany(Sname).changeBase();

        DecimalFormat df = new DecimalFormat("###,###,###");
        Market.getCompany(Sname).update("Shares bought",
                "§aPlayer bought " + amount + " shares for " + df.format(v),
                player.getUniqueId());

        player.sendMessage(SignClick.getPrefix()+"buy: §aaccepted");
    }

    public void sellShare(String Sname, Integer amount, Player player){
        double shareBalSellPrice = Market.getShareBalSellPrice(Sname, amount);
        double balSellPrice = Market.getBalSellPrice(Sname, amount);

        String countryName = Market.getCompany(Sname).getCountry();
        Country country = CountryManager.getCountry(countryName);

        if (country == null){
            country = new CountryNull();
        }

        double keepPCT = Market.getKeepFee(country);

        double total = shareBalSellPrice+balSellPrice;
        AssertMet(total <= Market.getCompany(Sname).getValue(), "The total sell value is larger then value");


        double to_gov;

        to_gov = total*(1.0-keepPCT);

        Country playerCountry = CountryManager.getCountry(player);
        if (playerCountry != null){
            playerCountry.deposit((int) to_gov);
        }else{
            Market.getCompany(Sname).addShareBal(to_gov);
        }

        int share_amount = shares.getOrDefault(Sname, 0);
        if (share_amount < amount){
            player.sendMessage(SignClick.getPrefix()+"sell: §cdenied (not enough shares)");
            return;
        }

        if (Market.sell(Sname, amount, this)){
            addBal(total*keepPCT);

            Market.getCompany(Sname).removeShareBal(shareBalSellPrice);
            Market.getCompany(Sname).removeBalVar(balSellPrice);
            Market.getCompany(Sname).changeBase();

            shares.put(Sname, share_amount-amount);

            DecimalFormat df = new DecimalFormat("###,###,###");
            Market.getCompany(Sname).update("Shares sold",
                    "§cPlayer sold " + amount + " shares for " + df.format(total*keepPCT),
                    player.getUniqueId());

            player.sendMessage(SignClick.getPrefix()+"sell: §aaccepted");
        }
    }

    public Boolean transfer(String Sname, Integer amount, Account target, Player player){
        if (shares.getOrDefault(Sname, 0) >= amount){
            target.receive(Sname, amount, this, Objects.requireNonNull(Bukkit.getPlayer(target.uuid)));
            int share_amount = shares.get(Sname);
            shares.put(Sname, share_amount-amount);
            player.sendMessage(SignClick.getPrefix()+"transfer: §aaccepted");
            return true;
        }else{
            player.sendMessage(SignClick.getPrefix()+"transfer: §cdenied (not enough shares)");
            return false;
        }

    }

    public void receive(String Sname, Integer amount, Account sender, Player player){
        int share_amount = shares.getOrDefault(Sname, 0);
        shares.put(Sname, share_amount+amount);
        player.sendMessage(SignClick.getPrefix()+"received: "+amount+" shares for "+Sname+" from "+Bukkit.getPlayer(sender.uuid).getName());
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

            CompanyI comp = Market.getCompany(b);
            double v = (comp.getValue()/(comp.getTotalShares().doubleValue()))*s;

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

        player.sendMessage(SignClick.getPrefix()+"portfolio:\n");
        double total = 0;
        for (int i=0; i<values.size(); i++){
            String b = order.get(i);
            double v = values.get(i);
            DecimalFormat df = new DecimalFormat("###,###,###");
            DecimalFormat df2 = new DecimalFormat("0.00");
            int i2 = i + 1;
            total += v;
            CompanyI comp = Market.getCompany(b);
            player.sendMessage(SignClick.getPrefix()+i2+". §3"+b+": §7" +df.format(v)+" ("+df2.format((shares.get(b).doubleValue()/comp.getTotalShares().doubleValue()*100.0))+"%)\n");
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

        Bukkit.getServer().getScheduler().runTaskLater(SignClick.getPlugin(), new Runnable() {
            public void run() {
                compNamePending = null;
                compAmountPending = 0.0;
                compWeeksPending = 0;
                compReason = "no_reason";

            }
        }, 20*120L);

        Player p = Bukkit.getPlayer(uuid);
        if (p != null){
            p.sendMessage(SignClick.getPrefix()+" you §7"+p.getName()+SignClick.getPrefix()+" got a contract request from §7" + stock_name
                    + SignClick.getPrefix()+" they will ask you §7"+amount+SignClick.getPrefix()+" for §7"+weeks+SignClick.getPrefix()+" weeks, do §c/company sign_contract_ptc");
        }
    }


    public UUID getUuid() {
        return uuid;
    }

    public String getName(){
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }
}
