package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.CompanyPatent.Patent;
import com.klanting.signclick.Economy.CompanyPatent.PatentUpgrade;
import com.klanting.signclick.Economy.CompanyPatent.PatentUpgradeCustom;
import com.klanting.signclick.Economy.CompanyUpgrades.*;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

import static org.bukkit.Bukkit.getServer;

public class Company {
    public ArrayList<UUID> owners = new ArrayList<UUID>();
    public String name;
    public String Sname;

    public double bal = 0.0;
    public double books = 0.0;
    public double securityFunds = 0.0;

    public double spendable = 0.0;

    public Boolean openTrade = false;

    public double lastValue = 40000000.0;
    public Map<UUID, UUID> support = new HashMap<UUID, UUID>();

    public Map<UUID, Integer> shareHolders = new HashMap<UUID, Integer>();

    public String compNamePending = null;
    public double compAmountPending = 0.0;
    public int compWeeksPending = 0;

    public String compReason = "no_reason";

    public String playerNamePending = null;
    public double playerAmountPending = 0.0;
    public int playerWeeksPending = 0;

    public String playerReason = "no_reason";


    public ArrayList<Upgrade> upgrades = new ArrayList<>();

    public ArrayList<Patent> patent = new ArrayList<>();

    public ArrayList<PatentUpgrade> patentUpgrades = new ArrayList<>();

    public Integer patentCrafted = 0;

    public String countryName;

    public String type;

    Company(String n, String StockName, Account creater){
        name = n;
        Sname = StockName;

        support.put(creater.uuid, creater.uuid);
        shareHolders.put(creater.uuid, 1000000);
        creater.receive_private(Sname, 1000000);

        upgrades.add(new UpgradeExtraPoints(0));
        upgrades.add(new UpgradePatentSlot(0));
        upgrades.add(new UpgradePatentUpgradeSlot(0));
        upgrades.add(new UpgradeCraftLimit(0));
        upgrades.add(new UpgradeInvestReturnTime(0));
        type = "other";
    }

    Company(String n, String StockName){
        name = n;
        Sname = StockName;
        lastValue = get_value();

        upgrades.add(new UpgradeExtraPoints(0));
        upgrades.add(new UpgradePatentSlot(0));
        upgrades.add(new UpgradePatentUpgradeSlot(0));
        upgrades.add(new UpgradeCraftLimit(0));
        upgrades.add(new UpgradeInvestReturnTime(0));

    }

    Double get_bal(){
        return bal;
    }

    public Double get_value(){
        return bal+books;
    }

    public Boolean add_bal(Double amount){
        bal += amount;
        Market.change_base(Sname);

        if (amount > 0){
            spendable += ((0.2+ CountryDep.getPolicyBonus(countryName, 0, 3))*amount);
        }

        double sub_pct = 1.0;
        if (CountryDep.getStability(GetCountry()) < 30){
            sub_pct -= 0.20;
        }
        if (CountryDep.getStability(GetCountry()) < 50){
            sub_pct -= 0.10;
        }
        if (CountryDep.getStability(GetCountry()) > 80){
            sub_pct += 0.10;
        }

        securityFunds += (0.01*amount)*(sub_pct+(double) upgrades.get(0).getBonus()/100.0)*(1.0+ CountryDep.getPolicyBonus(countryName, 0, 2));
        return true;
    }

    public Boolean add_bal_no_point(Double amount){
        bal += amount;
        Market.change_base(Sname);

        return true;
    }

    public Boolean remove_bal(Double amount){
        if ((bal+books >= amount) & (spendable >= amount)){
            bal -= amount;
            spendable -= amount;
            Market.change_base(Sname);
            return true;
        }
        return false;

    }

    void add_books(Double amount){
        books += amount;
        spendable += (0.2*amount);
    }

    void remove_books(Double amount){
        books -= amount;
        spendable -= amount;
    }

    public void change_share_holder(Account holder, Integer amount){
        if (shareHolders.getOrDefault(holder.uuid, null) != null){
            Integer am = shareHolders.get(holder.uuid);
            shareHolders.put(holder.uuid, am+amount);

        }else {
            shareHolders.put(holder.uuid, amount);
            support.put(holder.uuid, null);
        }

        if (shareHolders.getOrDefault(holder.uuid, 0) == 0){
            shareHolders.remove(holder.uuid);
            support.remove(holder.uuid);
        }
    }

    public void support_update(Account holder, UUID uuid){
        support.put(holder.uuid, uuid);
        check_support();
        CalculateCountry();
    }

    public void check_support(){
        double neutral = 0.0;
        Integer total = Market.getTotal(Sname);
        Map<UUID, Integer> s_dict = new HashMap<UUID, Integer>();

        int highest = 0;
        UUID highest_uuid = null;

        for(Entry<UUID, UUID> entry : support.entrySet()){
            UUID k = entry.getKey();
            UUID v = entry.getValue();

            Integer impact = shareHolders.getOrDefault(k, 0);
            if (v == null){
                neutral +=impact;
            }else{
                Integer bef = s_dict.getOrDefault(v, 0);
                s_dict.put(v, bef+impact);

                if (bef+impact > highest){
                    highest = bef+impact;
                    highest_uuid = v;
                }
            }

        }

        neutral = neutral/total.doubleValue();
        ArrayList<UUID> new_owners = new ArrayList<UUID>();
        for(Entry<UUID, Integer> entry : s_dict.entrySet()){
            UUID k = entry.getKey();
            double v = entry.getValue();

            v = v/total.doubleValue();

            if (v >= 0.45){
                new_owners.add(k);
            }else if ((owners.contains(k)) & (v+neutral >= 0.5)){
                new_owners.add(k);
            }
        }
        if (new_owners.size() != 0){
            owners = new_owners;
        }else if (highest_uuid != null){
            new_owners.add(highest_uuid);
            owners = new_owners;
        }


    }

    public Boolean is_owner(UUID uuid){
        return owners.contains(uuid);
    }

    public void get_share_top(Player player){
        ArrayList<UUID> order = new ArrayList<UUID>();
        ArrayList<Integer> values = new ArrayList<Integer>();

        for(Entry<UUID, Integer> entry : shareHolders.entrySet()){
            UUID s = entry.getKey();
            int v = entry.getValue();

            if (!order.isEmpty()){

                boolean found = false;

                for (int i = 0; i < values.size(); i++) {
                    int o = values.get(i);
                    if (v > o){
                        order.add(i, s);
                        values.add(i, v);
                        found = true;
                        break;

                    }
                }


                if (!found){
                    order.add(s);
                    values.add(v);
                }


            }else{
                order.add(s);
                values.add(v);
            }


        }

        player.sendMessage("§bsharetop:");
        Integer total = Market.getTotal(Sname);
        DecimalFormat df = new DecimalFormat("###,###,###");
        DecimalFormat df2 = new DecimalFormat("0.00");
        for (int i = 0; i < values.size(); i++) {
            player.sendMessage("§9"+Bukkit.getOfflinePlayer(order.get(i)).getName()+": §f"+df.format(values.get(i))+
                    " ("+df2.format(values.get(i)/total.doubleValue()*100.0)+"%)");
        }

        if (openTrade){
            player.sendMessage("§eMarket: §f"+"inf"+" ("+"inf"+"%)");
        }else{
            player.sendMessage("§eMarket: §f"+df.format(Market.get_market_amount(Sname))+" ("+df2.format(Market.get_market_amount(Sname)/total.doubleValue()*100.0)+"%)");
        }


        order.clear();
        values.clear();
    }


    void dividend(){
        String country = GetCountry();

        double value_one = (get_value()/Market.getTotal(Sname).doubleValue())*(0.01- CountryDep.getPolicyBonus(country, 0, 1)- CountryDep.getPolicyBonus(country, 1, 1));
        remove_bal(value_one*(Market.getTotal(Sname)-Market.get_market_amount(Sname)));
        for (Entry<UUID, Integer> entry : shareHolders.entrySet()){
            UUID holder = entry.getKey();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(holder);
            int shares = entry.getValue();
            double payout = value_one*shares;
            SignClick.getEconomy().depositPlayer(offlinePlayer, payout);
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Market.getAccount(holder).send_player("§byou got §7"+df.format(payout)+" §b from dividends in §7"+Sname);

        }
    }

    public void info(Player player){
        DecimalFormat df = new DecimalFormat("###,###,###");
        ArrayList<String> name_array = new ArrayList<String>();
        ArrayList<String> owner_array = new ArrayList<String>();
        for(Entry<UUID, Integer> entry : shareHolders.entrySet()){
            UUID uuid = entry.getKey();
            name_array.add(Bukkit.getOfflinePlayer(uuid).getName());
        }

        for (int i = 0; i < owners.size(); i++) {
            owner_array.add(Bukkit.getOfflinePlayer(owners.get(i)).getName());
        }

        player.sendMessage("§bName: §7"+name+"\n" +
                "§bStockname: §7"+Sname+"\n" +
                "§bCEO: §7"+owner_array+"\n" +
                "§bbal: §7"+df.format(get_value())+"\n" +
                "§bshares: §7"+df.format(Market.getTotal(Sname))+"\n" +
                "§bshareholders: §7"+ name_array);

        name_array.clear();
        owner_array.clear();

    }

    public void send_owner(String message){
        for (int i = 0; i < owners.size(); i++){
            Player p = Bukkit.getPlayer(owners.get(i));
            if (p != null){
                p.sendMessage(message);
            }

        }
    }

    public void accept_offer_comp_contract(){
        if (compNamePending == null){
            return;
        }

        Market.setContractComptoComp(Sname, compNamePending, compAmountPending, compWeeksPending, compReason);

        compNamePending = null;
        compAmountPending = 0.0;
        compWeeksPending = 0;
        compReason = "no_reason";

    }

    public void send_offer_comp_contract(String stock_name, double amount, int weeks, String reason){
        Market.get_business(stock_name).receive_offer_comp_contract(Sname, amount, weeks, reason);
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

        send_owner("§b your company §7"+Sname+"§b got a contract from §7" + stock_name
                + "§b they will ask you §7"+amount+"§b for §7"+weeks+"§b weeks, do §c/company sign_contract_ctc "+Sname);
    }


    //correct
    public void accept_offer_player_contract(){
        if (playerNamePending == null){
            return;
        }

        Market.setContractComptoPlayer(Sname, playerNamePending, playerAmountPending, playerWeeksPending, playerReason);

        playerNamePending = null;
        playerAmountPending = 0.0;
        playerWeeksPending = 0;
        playerReason = "no_reason";

    }



    //correct
    public void receive_offer_player_contract(String playerUUID, double amount, int weeks, String reason){
        playerNamePending = playerUUID;
        playerAmountPending = amount;
        playerWeeksPending = weeks;
        playerReason = reason;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
            public void run() {
                playerNamePending = null;
                playerAmountPending = 0.0;
                playerWeeksPending = 0;
                playerReason = "no_reason";

            }
        }, 20*120L);

        send_owner("§b your company §7"+Sname+"§b got a contract from §7" + Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName()
                + "§b he/she will ask you §7"+amount+"§b for §7"+weeks+"§b weeks, do §c/company sign_contract_ctp "+ Sname);
    }



    public double get_spendable(){
        return spendable;
    }

    public void reset_spendable(){
        double base = 0.2;
        if (CountryDep.getStability(GetCountry()) < 50){
            base -= 0.03;
        }
        double pct = (base+ CountryDep.getPolicyBonus(countryName, 0, 3));
        if (type.equals("bank")){
            pct += CountryDep.getPolicyBonus(countryName, 0, 7);
            pct += CountryDep.getPolicyBonus(countryName, 1, 5);
            pct += CountryDep.getPolicyBonus(countryName, 2, 11);
        }
        spendable = get_value()*pct;
    }

    public void reset_patent_crafted(){
        patentCrafted = 0;
    }


    public void SaveData(){

        List<String> f_list = new ArrayList<String>();
        for (UUID uuid: owners){
            f_list.add(uuid.toString());
        }
        f_list.clear();

        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "owners", f_list);
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "name", name);
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "bal", bal);
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "books", books);
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "spendable", spendable);
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "support", support.toString());
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "share_holders", shareHolders.toString());
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "open_trade", openTrade);
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "share_value", Market.get_value(Sname));
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "share_base", Market.get_base(Sname));
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "market_amount", Market.get_market_amount(Sname));
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "total", Market.getTotal(Sname));
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "last_value", lastValue);
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "security_funds", securityFunds);
        SignClick.getPlugin().getConfig().set("company."+Sname+"." + "type", type);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick save company "+Sname+" completed!");

        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();

        for (Upgrade u: upgrades){
            u.save(this);
        }

        SignClick.getPlugin().getConfig().set("company."+Sname+".patent", null);
        for (Patent p: patent){
            p.save(this);
        }

        SignClick.getPlugin().getConfig().set("company."+Sname+".patent_up", null);
        Integer counter = 0;
        for (PatentUpgrade up: patentUpgrades){
            if (up instanceof PatentUpgradeCustom){
                PatentUpgradeCustom u = (PatentUpgradeCustom) up;
                u.save(this, counter);
            }else{
                up.save(this, counter);
            }
            counter++;
        }
    }

    public double StockCompareGet(){
        return ((get_value()/ lastValue)-1)*100;

    }
    public double StockCompare(){
        double diff = ((get_value()/ lastValue)-1)*100;
        lastValue = get_value();
        return diff;
    }

    public void DoUpgrade(Integer id){
        Upgrade u = upgrades.get(id);
        if (u.canUpgrade((int) (bal+books), (int) securityFunds)){
            double base = 1.0;
            if (CountryDep.getStability(GetCountry()) < 30){
                base += 0.05;
            }
            if (CountryDep.getStability(GetCountry()) < 50){
                base += 0.15;
            }
            securityFunds -= u.getUpgradeCostPoints()*(base- CountryDep.getPolicyBonus(GetCountry(), 1, 3));
            int cost = (int) ((double) u.getUpgradeCost()*(base- CountryDep.getPolicyBonus(GetCountry(), 1, 3)));
            bal -= cost;
            u.DoUpgrade();

            int pct = upgrades.get(4).getBonus()+(int) (CountryDep.getPolicyBonus(countryName, 3, 2)*100.0);
            double weeks = (10.0-(10.0*pct/100.0));
            double weekly_back = cost/weeks;
            Market.setContractServertoComp(this.Sname, weekly_back, (int) Math.floor(weeks), "Upgrade["+u.id+"] "+u.level, 0);
            if (Math.floor(weeks) < weeks){
                Market.setContractServertoComp(this.Sname, cost - (weekly_back*Math.floor(weeks)), 1, "Upgrade["+u.id+"] "+u.level, (int) Math.floor(weeks));
            }

        }
    }

    public void CalculateCountry(){
        HashMap<String, Integer> country_top = new HashMap<String, Integer>();

        Integer highest = -1;
        String linked_name = null;

        for (Entry<UUID, Integer> entry : shareHolders.entrySet()){
            String country = CountryDep.ElementUUID(entry.getKey());
            Integer amount = country_top.getOrDefault(country, 0);
            amount += entry.getValue();
            country_top.put(country, amount);

            if (highest < amount){
                highest = amount;
                linked_name = country;
            }
        }

        countryName = linked_name;

    }

    public String GetCountry(){
        return countryName;
    }

}
