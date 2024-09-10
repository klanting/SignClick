package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.CompanyPatent.Patent;
import com.klanting.signclick.Economy.CompanyPatent.PatentUpgrade;
import com.klanting.signclick.Economy.CompanyPatent.PatentUpgradeCustom;
import com.klanting.signclick.Economy.CompanyUpgrades.*;
import com.klanting.signclick.Economy.Policies.Policy;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.security.KeyPair;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static org.bukkit.Bukkit.getServer;

public class Company {
    public ArrayList<UUID> owners = new ArrayList<UUID>();
    public String name;
    public String Sname;

    public double bal = 0.0;
    public double books = 0.0;
    public double security_funds = 0.0;

    public double spendable = 0.0;

    public Boolean open_trade = false;

    public double last_value = 40000000.0;
    public Map<UUID, UUID> support = new HashMap<UUID, UUID>();

    public Map<UUID, Integer> share_holders = new HashMap<UUID, Integer>();

    public String comp_name_pending = null;
    public double comp_amount_pending = 0.0;
    public int comp_weeks_pending = 0;

    public String comp_reason = "no_reason";

    public String player_name_pending = null;
    public double player_amount_pending = 0.0;
    public int player_weeks_pending = 0;

    public String player_reason = "no_reason";


    public ArrayList<Upgrade> upgrades = new ArrayList<>();

    public ArrayList<Patent> patent = new ArrayList<>();

    public ArrayList<PatentUpgrade> patent_upgrades = new ArrayList<>();

    public Integer patent_crafted = 0;

    public String country_name;

    public String type;

    Company(String n, String StockName, Account creater){
        name = n;
        Sname = StockName;

        support.put(creater.uuid, creater.uuid);
        share_holders.put(creater.uuid, 1000000);
        creater.receive_private(Sname, 1000000);

        upgrades.add(new UpgradeExtraPoints(0));
        upgrades.add(new UpgradePatentSlot(0));
        upgrades.add(new UpgradePatentUpgradeSlot(0));
        upgrades.add(new UpgradeCraftLimit(0));
        upgrades.add(new UpgradeInvestReturnTime(0));
    }

    Company(String n, String StockName){
        name = n;
        Sname = StockName;
        last_value = get_value();

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
            spendable += ((0.2+Banking.getPolicyBonus(country_name, 0, 3))*amount);
        }

        double sub_pct = 1.0;
        if (Banking.get_stability(GetCountry()) < 30){
            sub_pct -= 0.20;
        }
        if (Banking.get_stability(GetCountry()) < 50){
            sub_pct -= 0.10;
        }
        if (Banking.get_stability(GetCountry()) > 80){
            sub_pct += 0.10;
        }

        security_funds += (0.01*amount)*(sub_pct+(double) upgrades.get(0).getBonus()/100.0)*(1.0+Banking.getPolicyBonus(country_name, 0, 2));
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
        if (share_holders.getOrDefault(holder.uuid, null) != null){
            Integer am = share_holders.get(holder.uuid);
            share_holders.put(holder.uuid, am+amount);

        }else {
            share_holders.put(holder.uuid, amount);
            support.put(holder.uuid, null);
        }

        if (share_holders.getOrDefault(holder.uuid, 0) == 0){
            share_holders.remove(holder.uuid);
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
        Integer total = Market.get_total(Sname);
        Map<UUID, Integer> s_dict = new HashMap<UUID, Integer>();

        int highest = 0;
        UUID highest_uuid = null;

        for(Entry<UUID, UUID> entry : support.entrySet()){
            UUID k = entry.getKey();
            UUID v = entry.getValue();

            Integer impact = share_holders.getOrDefault(k, 0);
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

        for(Entry<UUID, Integer> entry : share_holders.entrySet()){
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
        Integer total = Market.get_total(Sname);
        DecimalFormat df = new DecimalFormat("###,###,###");
        DecimalFormat df2 = new DecimalFormat("0.00");
        for (int i = 0; i < values.size(); i++) {
            player.sendMessage("§9"+Bukkit.getOfflinePlayer(order.get(i)).getName()+": §f"+df.format(values.get(i))+
                    " ("+df2.format(values.get(i)/total.doubleValue()*100.0)+"%)");
        }

        if (open_trade){
            player.sendMessage("§eMarket: §f"+"inf"+" ("+"inf"+"%)");
        }else{
            player.sendMessage("§eMarket: §f"+df.format(Market.get_market_amount(Sname))+" ("+df2.format(Market.get_market_amount(Sname)/total.doubleValue()*100.0)+"%)");
        }


        order.clear();
        values.clear();
    }


    void dividend(){
        String country = GetCountry();

        double value_one = (get_value()/Market.get_total(Sname).doubleValue())*(0.01-Banking.getPolicyBonus(country, 0, 1)-Banking.getPolicyBonus(country, 1, 1));
        remove_bal(value_one*(Market.get_total(Sname)-Market.get_market_amount(Sname)));
        for (Entry<UUID, Integer> entry : share_holders.entrySet()){
            UUID holder = entry.getKey();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(holder);
            int shares = entry.getValue();
            double payout = value_one*shares;
            SignClick.getEconomy().depositPlayer(offlinePlayer, payout);
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Market.get_account(holder).send_player("§byou got §7"+df.format(payout)+" §b from dividends in §7"+Sname);

        }
    }

    public void info(Player player){
        DecimalFormat df = new DecimalFormat("###,###,###");
        ArrayList<String> name_array = new ArrayList<String>();
        ArrayList<String> owner_array = new ArrayList<String>();
        for(Entry<UUID, Integer> entry : share_holders.entrySet()){
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
                "§bshares: §7"+df.format(Market.get_total(Sname))+"\n" +
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
        if (comp_name_pending == null){
            return;
        }

        Market.SetContractComptoComp(Sname, comp_name_pending, comp_amount_pending, comp_weeks_pending, comp_reason);

        comp_name_pending = null;
        comp_amount_pending = 0.0;
        comp_weeks_pending= 0;
        comp_reason = "no_reason";

    }

    public void send_offer_comp_contract(String stock_name, double amount, int weeks, String reason){
        Market.get_business(stock_name).receive_offer_comp_contract(Sname, amount, weeks, reason);
    }

    public void receive_offer_comp_contract(String stock_name, double amount, int weeks, String reason){
        comp_name_pending = stock_name;
        comp_amount_pending = amount;
        comp_weeks_pending = weeks;
        comp_reason = reason;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
            public void run() {
                comp_name_pending = null;
                comp_amount_pending = 0.0;
                comp_weeks_pending= 0;
                comp_reason = "no_reason";

            }
        }, 20*120L);

        send_owner("§b your com.company §7"+Sname+"§b got a contract from §7" + stock_name
                + "§b they will ask you §7"+amount+"§b for §7"+weeks+"§b weeks, do §c/com.company sign_contract_ctc "+Sname);
    }


    //correct
    public void accept_offer_player_contract(){
        if (player_name_pending == null){
            return;
        }

        Market.SetContractComptoPlayer(Sname, player_name_pending, player_amount_pending, player_weeks_pending, player_reason);

        player_name_pending = null;
        player_amount_pending = 0.0;
        player_weeks_pending= 0;
        player_reason = "no_reason";

    }



    //correct
    public void receive_offer_player_contract(String playerUUID, double amount, int weeks, String reason){
        player_name_pending = playerUUID;
        player_amount_pending = amount;
        player_weeks_pending = weeks;
        player_reason = reason;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
            public void run() {
                player_name_pending = null;
                player_amount_pending = 0.0;
                player_weeks_pending= 0;
                player_reason = "no_reason";

            }
        }, 20*120L);

        send_owner("§b your com.company §7"+Sname+"§b got a contract from §7" + Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName()
                + "§b he/she will ask you §7"+amount+"§b for §7"+weeks+"§b weeks, do §c/com.company sign_contract_ctp "+ Sname);
    }



    public double get_spendable(){
        return spendable;
    }

    public void reset_spendable(){
        double base = 0.2;
        if (Banking.get_stability(GetCountry()) < 50){
            base -= 0.03;
        }
        double pct = (base+Banking.getPolicyBonus(country_name, 0, 3));
        if (type.equals("bank")){
            pct += Banking.getPolicyBonus(country_name, 0, 7);
            pct += Banking.getPolicyBonus(country_name, 1, 5);
            pct += Banking.getPolicyBonus(country_name, 2, 11);
        }
        spendable = get_value()*pct;
    }

    public void reset_patent_crafted(){
        patent_crafted = 0;
    }


    public void SaveData(){

        List<String> f_list = new ArrayList<String>();
        for (UUID uuid: owners){
            f_list.add(uuid.toString());
        }
        f_list.clear();

        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "owners", f_list);
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "name", name);
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "bal", bal);
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "books", books);
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "spendable", spendable);
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "support", support.toString());
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "share_holders", share_holders.toString());
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "open_trade", open_trade);
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "share_value", Market.get_value(Sname));
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "share_base", Market.get_base(Sname));
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "market_amount", Market.get_market_amount(Sname));
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "total", Market.get_total(Sname));
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "last_value", last_value);
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "security_funds", security_funds);
        SignClick.getPlugin().getConfig().set("com.company."+Sname+"." + "type", type);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick save com.company "+Sname+" completed!");

        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();

        for (Upgrade u: upgrades){
            u.save(this);
        }

        SignClick.getPlugin().getConfig().set("com.company."+Sname+".patent", null);
        for (Patent p: patent){
            p.save(this);
        }

        SignClick.getPlugin().getConfig().set("com.company."+Sname+".patent_up", null);
        Integer counter = 0;
        for (PatentUpgrade up: patent_upgrades){
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
        return ((get_value()/last_value)-1)*100;

    }
    public double StockCompare(){
        double diff = ((get_value()/last_value)-1)*100;
        last_value = get_value();
        return diff;
    }

    public void DoUpgrade(Integer id){
        Upgrade u = upgrades.get(id);
        if (u.canUpgrade((int) (bal+books), (int) security_funds)){
            double base = 1.0;
            if (Banking.get_stability(GetCountry()) < 30){
                base += 0.05;
            }
            if (Banking.get_stability(GetCountry()) < 50){
                base += 0.15;
            }
            security_funds -= u.getUpgradeCostPoints()*(base-Banking.getPolicyBonus(GetCountry(), 1, 3));
            int cost = (int) ((double) u.getUpgradeCost()*(base-Banking.getPolicyBonus(GetCountry(), 1, 3)));
            bal -= cost;
            u.DoUpgrade();

            int pct = upgrades.get(4).getBonus()+(int) (Banking.getPolicyBonus(country_name, 3, 2)*100.0);
            double weeks = (10.0-(10.0*pct/100.0));
            double weekly_back = cost/weeks;
            Market.SetContractServertoComp(this.Sname, weekly_back, (int) Math.floor(weeks), "Upgrade["+u.id+"] "+u.level, 0);
            if (Math.floor(weeks) < weeks){
                Market.SetContractServertoComp(this.Sname, cost - (weekly_back*Math.floor(weeks)), 1, "Upgrade["+u.id+"] "+u.level, (int) Math.floor(weeks));
            }

        }
    }

    public void CalculateCountry(){
        HashMap<String, Integer> country_top = new HashMap<String, Integer>();

        Integer highest = -1;
        String linked_name = null;

        for (Entry<UUID, Integer> entry : share_holders.entrySet()){
            String country = Banking.ElementUUID(entry.getKey());
            Integer amount = country_top.getOrDefault(country, 0);
            amount += entry.getValue();
            country_top.put(country, amount);

            if (highest < amount){
                highest = amount;
                linked_name = country;
            }
        }

        country_name = linked_name;

    }

    public String GetCountry(){
        return country_name;
    }

}
