package com.klanting.signclick.Economy;

import com.klanting.signclick.Calculate.SignStock;
import com.klanting.signclick.Economy.CompanyPatent.*;
import com.klanting.signclick.Economy.CompanyUpgrades.*;
import com.klanting.signclick.SignClick;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class Market {
    private static Map<UUID, Account> accounts = new HashMap<UUID, Account>();
    private static Map<String, Company> company = new HashMap<String, Company>();
    private static Map<String, Double> share_values = new HashMap<String, Double>();
    private static Map<String, Double> share_base = new HashMap<String, Double>();
    private static Map<String, Integer> market_amount = new HashMap<String, Integer>();
    private static Map<String, Integer> total = new HashMap<String, Integer>();
    private static Double fee = 0.05;
    private static Double flux = 1.15;

    public static Boolean show_particles = true;

    private static ArrayList<String> names = new ArrayList<String>();

    public static Double get_value(String Sname){
        return share_values.get(Sname);
    }
    public static Double get_base(String Sname){
        return share_base.get(Sname);
    }

    public static Integer get_market_amount(String Sname){
        return market_amount.get(Sname);
    }

    public static ArrayList<Object[]> ContractComptoComp = new ArrayList<Object[]>();

    public static ArrayList<Object[]> ContractComptoPlayer = new ArrayList<Object[]>();

    public static ArrayList<Object[]> ContractPlayertoComp = new ArrayList<Object[]>();

    public static ArrayList<Object[]> ContractServertoComp = new ArrayList<Object[]>();

    public static ArrayList<Location> stock_signs = new ArrayList<Location>();

    public static void clear(){
        accounts.clear();
        company.clear();
        share_values.clear();
        share_base.clear();
        market_amount.clear();
        total.clear();
        names.clear();
        ContractComptoComp.clear();
        ContractComptoPlayer.clear();
        ContractServertoComp.clear();
        ContractPlayertoComp.clear();
        stock_signs.clear();
    }

    public static void set_market_amount(String Sname, Integer amount){
        market_amount.put(Sname, amount);
    }

    public static Double get_buy_price(String Sname, Integer amount){
        double market_pct = (market_amount.get(Sname).doubleValue()/(total.get(Sname).doubleValue()+Math.min(Market.get_market_amount(Sname), 0)));
        double a = (1.0 - market_pct) * 25.0 - 10.0;

        market_pct = ((market_amount.get(Sname).doubleValue()-amount.doubleValue())/(total.get(Sname).doubleValue()+Math.min(Market.get_market_amount(Sname), 0)));
        double b = (1.0 - market_pct) * 25.0 - 10.0;

        double base = share_base.get(Sname);
        double v = base * ((Math.pow(flux, b)) - (Math.pow(flux, a))) /Math.log(flux)/(b-a);
        return v*amount;
    }

    public static  Double get_sell_price(String Sname, Integer amount){

        String country = Market.get_business(Sname).GetCountry();
        double sub_fee = fee;
        if (Banking.getStability(country) < 50){
            sub_fee += 0.01;
        }
        return (get_buy_price(Sname, -amount)*-1)*(1.0 - (sub_fee - Banking.getPolicyBonus(country, 0, 0)- Banking.getPolicyBonus(country, 1, 1)));

    }

    public static void change_value(String Sname){
        double market_pct = (market_amount.get(Sname).doubleValue()/total.get(Sname).doubleValue());
        double x = (1.0 - market_pct)*25.0 - 10.0;

        double base = share_base.get(Sname);
        share_values.put(Sname, base*Math.pow(flux, x));
    }

    public static Boolean buy(String Sname, Integer amount, Account acc){
        if (market_amount.get(Sname) >= amount || get_business(Sname).open_trade){
            int market_am = market_amount.get(Sname);
            market_amount.put(Sname, market_am-amount);

            Company comp = company.get(Sname);
            comp.change_share_holder(acc, amount);

            change_value(Sname);

            return true;
        }
        return false;
    }

    public static Boolean sell(String Sname, Integer amount, Account acc){
        int market_am = market_amount.get(Sname);
        market_amount.put(Sname, market_am+amount);

        Company comp = company.get(Sname);
        comp.change_share_holder(acc, -amount);

        change_value(Sname);

        return true;
    }

    public static void change_base(String Sname){
        Company comp = company.get(Sname);
        double a = -10.0;
        double b = 15.0;
        double v = (comp.get_bal()/total.get(Sname)) / (((Math.pow(flux, b)) - (Math.pow(flux, a))) /Math.log(flux)/(b-a));
        share_base.put(Sname, v);

        change_value(Sname);
    }

    public static Company get_business(String Sname){
        return company.get(Sname);
    }

    public static Boolean add_business(String namebus, String Sname, Account acc){
        if ((!names.contains(namebus)) & (!total.containsKey(Sname))){
            names.add(namebus);
            Company comp = new Company(namebus, Sname, acc);
            company.put(Sname, comp);

            market_amount.put(Sname, 0);
            total.put(Sname, 1000000);

            share_base.put(Sname, 0.0);
            change_base(Sname);

            share_values.put(Sname, 0.0);
            change_value(Sname);

            comp.check_support();
            comp.CalculateCountry();

            return true;
        }
        return false;
    }

    public static Boolean has_business(String Sname){
        return company.containsKey(Sname);

    }

    public static ArrayList<Company> get_business_by_owner(UUID uuid){
        ArrayList<Company> outputs = new ArrayList<Company>();
        for(Map.Entry<String, Company> entry : company.entrySet()){
            if (entry.getValue().is_owner(uuid)){
                outputs.add(entry.getValue());
            }
        }
        return outputs;
    }

    public static void get_market_value_top(Player player){
        ArrayList<String> order = new ArrayList<String>();
        ArrayList<Double> values = new ArrayList<Double>();

        for(Map.Entry<String, Company> entry : company.entrySet()){
            String b = entry.getKey();
            Double v = entry.getValue().get_value();

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

        for (int i=0; i<values.size(); i++){
            String b = order.get(i);
            Double v = values.get(i);
            DecimalFormat df = new DecimalFormat("###,###,###");
            int i2 = i + 1;
            player.sendMessage("§b"+i2+". §3"+b+": §7" +df.format(v)+"\n");
        }

        order.clear();
        values.clear();
    }

    public static Integer get_total(String Sname){
        return total.get(Sname);
    }

    public static Integer set_total(String Sname, Integer value){
        return total.put(Sname, value);
    }

    public static Double get_fee(){
        return fee;
    }

    public static Boolean has_account(Player player){
        return accounts.containsKey(player.getUniqueId());

    }

    public static void create_account(Player player){
        Account acc = new Account(player.getUniqueId());
        accounts.put(player.getUniqueId(), acc);

    }

    public static Account get_account(Player player){
        if (!accounts.containsKey(player.getUniqueId())){
            Market.create_account(player);
        }

        return accounts.get(player.getUniqueId());

    }

    public static Boolean has_account(UUID uuid){
        return accounts.containsKey(uuid);

    }

    public static Account get_account(UUID uuid){
        return accounts.get(uuid);

    }

    public static void add_account(Account acc){
        accounts.put(acc.uuid, acc);

    }



    public static void add_company(String namebus, String Sname, ArrayList<UUID> owners,
                                   double bal, double books, double spendable,
                                   Map<UUID, UUID> support, Map<UUID, Integer> share_holders,
                                   double sv, double sb, int ma, int to, double last, double sf,
                                   ArrayList<Upgrade> upgrades, Boolean open_trade, String type){
        names.add(namebus);
        Company comp = new Company(namebus, Sname);
        comp.owners = owners;
        comp.bal = bal;
        comp.books = books;
        comp.spendable = spendable;
        comp.support = support;
        comp.share_holders = share_holders;
        comp.last_value = last;
        comp.security_funds = sf;
        comp.open_trade = open_trade;
        comp.type = type;
        company.put(Sname, comp);

        market_amount.put(Sname, ma);
        total.put(Sname, to);

        share_base.put(Sname, sb);
        change_base(Sname);

        share_values.put(Sname, sv);
        change_value(Sname);

        comp.check_support();
        comp.CalculateCountry();
        if (!upgrades.isEmpty()){
            comp.upgrades = upgrades;
        }

        String path = "company."+Sname+".patent_up";
        if (SignClick.getPlugin().getConfig().contains(path)){
            Integer counter = 0;
            while(SignClick.getPlugin().getConfig().contains(path+"."+counter)){
                int id = (Integer) SignClick.getPlugin().getConfig().get(path+"."+counter+".id");
                int level = (Integer) SignClick.getPlugin().getConfig().get(path+"."+counter+".level");
                String name = (String) SignClick.getPlugin().getConfig().get(path+"."+counter+".name");

                if (id == 4){
                    Material texture_item = Material.valueOf((String) SignClick.getPlugin().getConfig().get(path+"."+counter+".applied_item"));
                    PatentUpgrade up = new PatentUpgradeCustom(name,texture_item);
                    up.level = level;
                    comp.patent_upgrades.add(up);
                }else if (id == 0){
                    PatentUpgrade up = new PatentUpgradeJumper();
                    up.level = level;
                    comp.patent_upgrades.add(up);
                }else if (id == 1){
                    PatentUpgrade up = new PatentUpgradeEvade();
                    up.level = level;
                    comp.patent_upgrades.add(up);
                }else if (id == 2){
                    PatentUpgrade up = new PatentUpgradeRefill();
                    up.level = level;
                    comp.patent_upgrades.add(up);
                }else if (id == 3){
                    PatentUpgrade up = new PatentUpgradeCunning();
                    up.level = level;
                    comp.patent_upgrades.add(up);
                }

                counter++;
            }
        }

        path = "company."+Sname+".patent";
        if (SignClick.getPlugin().getConfig().contains(path)){
            Integer counter = 0;
            while(SignClick.getPlugin().getConfig().contains(path+"."+counter)){

                String name = (String) SignClick.getPlugin().getConfig().get(path+"."+counter+".name");
                Material item = Material.valueOf((String) SignClick.getPlugin().getConfig().get(path+"."+counter+".item"));
                List<String> patent_ups_index = (List<String>) SignClick.getPlugin().getConfig().get(path+"."+counter+".upgrades");

                ArrayList<PatentUpgrade> p_ups = new ArrayList<>();
                for (String index: patent_ups_index){
                    p_ups.add(comp.patent_upgrades.get(Integer.parseInt(index)));
                }

                Patent p =  new Patent(name, item, p_ups);
                p.createCraft(comp);
                comp.patent.add(p);
                counter++;
            }
        }

    }

    public static double get_books(String Sname){
        Company comp = get_business(Sname);
        return comp.books;
    }

    public static void reset_spendable(){

        for(Map.Entry<String, Company> entry : company.entrySet()){
            Company comp = entry.getValue();
            comp.reset_spendable();
        }

    }

    public static void reset_patent_crafted(){

        for(Map.Entry<String, Company> entry : company.entrySet()){
            Company comp = entry.getValue();
            comp.reset_patent_crafted();
        }

    }

    public static void run_dividends(){

        for(Map.Entry<String, Company> entry : company.entrySet()){
            Company comp = entry.getValue();
            comp.dividend();
        }

    }

    public static void market_available(Player player){
        ArrayList<String> order = new ArrayList<String>();
        ArrayList<Integer> values = new ArrayList<Integer>();
        for(Map.Entry<String, Integer> entry : market_amount.entrySet()){
            String b = entry.getKey();
            int v = entry.getValue();

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

        player.sendMessage("§eMarket:\n");
        for (int i=0; i<values.size(); i++){
            String b = order.get(i);
            double v = values.get(i);
            DecimalFormat df = new DecimalFormat("###,###,###");
            DecimalFormat df2 = new DecimalFormat("0.00");
            int i2 = i + 1;

            if (Market.get_business(b).open_trade){
                player.sendMessage("§b"+i2+". §9"+b+": §7" +"inf"+" ("+"inf"+"%)\n");
            }else{
                player.sendMessage("§b"+i2+". §9"+b+": §7" +df.format(v)+" ("+df2.format((v/Market.get_total(b).doubleValue()*100.0))+"%)\n");
            }

        }
    }

    public static void SaveData(){
        for (Map.Entry<UUID, Account> entry : accounts.entrySet()){
            Account acc = entry.getValue();
            getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "shares " + acc.shares.toString());
            SignClick.getPlugin().getConfig().set("accounts." + entry.getKey(), acc.shares.toString());
        }

        for (Company comp: company.values()){
            comp.SaveData();
        }

        List<String> contractString = new ArrayList<>();
        for (Object[] o : ContractComptoComp){
            contractString.add(o[0].toString()+","+o[1].toString()+","+o[2].toString()+","+o[3].toString()+","+o[4].toString());
        }

        SignClick.getPlugin().getConfig().set("ctc", contractString);

        contractString = new ArrayList<>();
        for (Object[] o : ContractComptoPlayer){
            contractString.add(o[0].toString()+","+o[1].toString()+","+o[2].toString()+","+o[3].toString()+","+o[4].toString());
        }

        SignClick.getPlugin().getConfig().set("ctp", contractString);

        contractString = new ArrayList<>();
        for (Object[] o : ContractPlayertoComp){
            contractString.add(o[0].toString()+","+o[1].toString()+","+o[2].toString()+","+o[3].toString()+","+o[4].toString());
        }

        SignClick.getPlugin().getConfig().set("ptc", contractString);

        List<Location> Signs = new ArrayList<>();
        for (Location l: stock_signs){
            Signs.add(l);
        }

        SignClick.getPlugin().getConfig().set("sign", Signs);

        SignClick.getPlugin().getConfig().set("show_particles", show_particles);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick save Market completed!");

        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }

    public static void RestoreData(){
        if (SignClick.getPlugin().getConfig().contains("accounts")){
            SignClick.getPlugin().getConfig().getConfigurationSection("accounts").getKeys(true).forEach(key ->{
                Map<String, Integer> sh = new HashMap<String, Integer>();

                String s = (String) SignClick.getPlugin().getConfig().get("accounts." + key);
                s = s.substring(1, s.length()-1);
                if (s.contains(",") | s.contains("=")){
                    String[] pairs = s.split(", ");
                    for (int i=0;i<pairs.length;i++) {
                        String pair = pairs[i];
                        String[] keyValue = pair.split("=");
                        sh.put(keyValue[0], Integer.valueOf(keyValue[1]));
                    }

                }
                Account acc = new Account(UUID.fromString(key));
                acc.shares = sh;

                Market.add_account(acc);

                accounts.put(UUID.fromString(key), acc);
            });
        }

        if (SignClick.getPlugin().getConfig().contains("company")){
            for (String key : SignClick.getPlugin().getConfig().getConfigurationSection("company").getKeys(true)) {
                String Sname = key;
                if (Sname.contains(".")) {
                    continue;
                }
                getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Sname found " + Sname);

                ArrayList<UUID> owners = new ArrayList<>();
                for (String s : (List<String>) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "owners")) {
                    owners.add(UUID.fromString(s));
                }

                String name = SignClick.getPlugin().getConfig().get("company." + Sname + "." + "name").toString();
                double bal = (double) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "bal");
                double books = (double) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "books");
                double spendable = (double) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "spendable");
                double last_value = (double) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "last_value");
                double security_funds = (double) SignClick.getPlugin().getConfig().get("company."+Sname+"." + "security_funds");

                Map<UUID, UUID> su = new HashMap<>();
                String support_string = (String) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "support");
                support_string = support_string.substring(1, support_string.length() - 1);
                if (support_string.contains(",") | support_string.contains("=")) {
                    String[] pairs = support_string.split(", ");
                    for (int i = 0; i < pairs.length; i++) {
                        String pair = pairs[i];
                        String[] keyValue = pair.split("=");
                        if (keyValue[1].equals("null")){
                            su.put(UUID.fromString(keyValue[0]), null);
                        }else{
                            su.put(UUID.fromString(keyValue[0]), UUID.fromString(keyValue[1]));
                        }

                    }

                }

                Map<UUID, Integer> sh = new HashMap<>();
                String share_holder_string = (String) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "share_holders");
                share_holder_string = share_holder_string.substring(1, share_holder_string.length() - 1);
                if (share_holder_string.contains(",") | share_holder_string.contains("=")) {
                    String[] pairs = share_holder_string.split(", ");
                    for (int i = 0; i < pairs.length; i++) {
                        String pair = pairs[i];
                        String[] keyValue = pair.split("=");
                        sh.put(UUID.fromString(keyValue[0]), Integer.parseInt(keyValue[1]));
                    }

                }

                double sv = (double) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "share_value");
                double sb = (double) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "share_base");
                int ma = (int) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "market_amount");
                int to = (int) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "total");


                ArrayList<Upgrade> upgrades = new ArrayList<>();
                for (int i=0; i<5; i++){
                    if (SignClick.getPlugin().getConfig().contains("company."+Sname+".upgrade."+i)){
                        int level = (int) SignClick.getPlugin().getConfig().get("company."+Sname+".upgrade."+i);
                        if (i == 0){
                            upgrades.add(new UpgradeExtraPoints(level));
                        }else if (i == 1){
                            upgrades.add(new UpgradePatentSlot(level));
                        }else if (i == 2){
                            upgrades.add(new UpgradePatentUpgradeSlot(level));
                        }else if (i == 3){
                            upgrades.add(new UpgradeCraftLimit(level));
                        }else if (i == 4){

                            upgrades.add(new UpgradeInvestReturnTime(level));
                        }
                    }
                }

                boolean open_trade = false;
                if (SignClick.getPlugin().getConfig().contains("company." + Sname + "." + "open_trade")){
                    open_trade = (Boolean) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "open_trade");
                }

                String type = "other";
                if (SignClick.getPlugin().getConfig().contains("company." + Sname + "." + "type")){
                    type = (String) SignClick.getPlugin().getConfig().get("company." + Sname + "." + "type");
                }

                Market.add_company(name, Sname, owners, bal, books, spendable, su, sh, sv, sb, ma, to, last_value, security_funds, upgrades, open_trade, type);

            }
        }

        if (SignClick.getPlugin().getConfig().contains("sign") && !SignClick.getPlugin().getConfig().get("sign").equals("[]")){

            for (Location s :  (List<Location>) SignClick.getPlugin().getConfig().get("sign")) {
                stock_signs.add(s);
            }
        }

        if (SignClick.getPlugin().getConfig().contains("ctc") && !SignClick.getPlugin().getConfig().get("ctc").equals("[]")){
            for (String s :  (List<String>) SignClick.getPlugin().getConfig().get("ctc")) {
                String[] pairs = s.split(",");


                Object[] tup = {pairs[0], pairs[1], Double.parseDouble(pairs[2]), Integer.parseInt(pairs[3]), pairs[4]};
                ContractComptoComp.add(tup);
            }
        }

        if (SignClick.getPlugin().getConfig().contains("ctp") && !SignClick.getPlugin().getConfig().get("ctp").equals("[]")){
            for (String s :  (List<String>) SignClick.getPlugin().getConfig().get("ctp")) {
                String[] pairs = s.split(",");


                Object[] tup = {pairs[0], pairs[1], Double.parseDouble(pairs[2]), Integer.parseInt(pairs[3]), pairs[4]};
                ContractComptoPlayer.add(tup);
            }
        }

        if (SignClick.getPlugin().getConfig().contains("ptc") && !SignClick.getPlugin().getConfig().get("ptc").equals("[]")){
            for (String s :  (List<String>) SignClick.getPlugin().getConfig().get("ptc")) {
                String[] pairs = s.split(",");


                Object[] tup = {pairs[0], pairs[1], Double.parseDouble(pairs[2]), Integer.parseInt(pairs[3]), pairs[4]};
                ContractPlayertoComp.add(tup);
            }
        }

        if (SignClick.getPlugin().getConfig().contains("show_particles")){
            show_particles = (Boolean) SignClick.getPlugin().getConfig().get("show_particles", false);
        }


    }

    public static void RunContracts(){
        // still crashes
        ArrayList<Object[]> new_data = new ArrayList<>();
        for (Object[] tuple : ContractComptoComp) {
            Company from = Market.get_business(tuple[0].toString());
            double amount = (Double) tuple[2];
            if (from.remove_bal(amount)) {
                Company to = Market.get_business(tuple[1].toString());
                int weeks = (Integer) tuple[3];
                weeks -= 1;
                to.add_bal(amount);
                if (weeks > 0) {
                    Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4]};
                    new_data.add(new_tuple);
                }

                from.send_owner("§cContract: from " + from.Sname + "(C) to " + to.Sname + "(C) amount: " + amount);
                to.send_owner("§aContract: from " + from.Sname + "(C) to " + to.Sname + "(C) amount: " + amount);
            } else {
                //message: not paid
                new_data.add(tuple);
            }
        }
        ContractComptoComp = new_data;

        new_data = new ArrayList<>();
        for (Object[] tuple : ContractComptoPlayer) {
            Company from = Market.get_business(tuple[0].toString());
            double amount = (Double) tuple[2];
            if (from.remove_bal(amount)) {
                Account to = Market.get_account(UUID.fromString(tuple[1].toString()));
                int weeks = (Integer) tuple[3];
                weeks -= 1;
                to.add_bal(amount);
                if (weeks > 0) {
                    Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4]};
                    new_data.add(new_tuple);
                }

                from.send_owner("§cContract: from " + from.Sname + "(C) to " + Bukkit.getOfflinePlayer(to.uuid).getName() + "(P) amount: " + amount);
                if (Bukkit.getPlayer(to.uuid) != null){
                    Bukkit.getPlayer(to.uuid).sendMessage("§aContract: from " + from.Sname + "(C) to " + Bukkit.getOfflinePlayer(to.uuid).getName() + "(P) amount: " + amount);
                }

            } else {
                //message: not paid
                new_data.add(tuple);
            }
        }
        ContractComptoPlayer = new_data;

        new_data = new ArrayList<>();
        for (Object[] tuple : ContractPlayertoComp) {
            try{
                Account from = Market.get_account(UUID.fromString(tuple[0].toString()));
                double amount = (Double) tuple[2];
                if (from.remove_bal(amount)) {
                    Company to = Market.get_business(tuple[1].toString());
                    int weeks = (Integer) tuple[3];
                    weeks -= 1;
                    to.add_bal(amount);
                    if (weeks > 0) {
                        Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4]};
                        new_data.add(new_tuple);
                    }

                    to.send_owner("§aContract: from " + Bukkit.getOfflinePlayer(from.uuid).getName() + "(P) to " + to.Sname + "(C) amount: " + amount);
                    if (Bukkit.getPlayer(from.uuid) != null){
                        Bukkit.getPlayer(from.uuid).sendMessage("§cContract: from " + Bukkit.getOfflinePlayer(from.uuid).getName() + "(P) to " + to.Sname + "(C) amount: " + amount);
                    }

                } else {
                    //message: not paid
                    new_data.add(tuple);
                }
            }catch (Exception e){

            }

        }
        ContractPlayertoComp = new_data;

        for (Object[] tuple : ContractServertoComp) {
            double amount = (Double) tuple[2];
            Company to = Market.get_business(tuple[1].toString());
            int weeks = (Integer) tuple[3];
            int delay = (Integer) tuple[5];

            if (delay == 0){
                weeks -= 1;
                to.add_bal_no_point(amount);
                if (weeks > 0) {
                    Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4], delay};
                    new_data.add(new_tuple);
                }

                to.send_owner("§aContract: from SERVER (S) to " + to.Sname + "(C) amount: " + amount);
            }else{
                delay -= 1;
                Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4], delay};
                new_data.add(new_tuple);
            }

        }

        ContractServertoComp = new_data;
    }

    public static void SetContractComptoComp(String from, String to, double amount, int weeks, String reason){
        Object[] tuple = {from, to, amount, weeks, reason};
        ContractComptoComp.add(tuple);

    }

    public static void SetContractComptoPlayer(String from, String toUUID, double amount, int weeks, String reason){
        Object[] tuple = {from, toUUID, amount, weeks, reason};
        ContractComptoPlayer.add(tuple);

    }

    public static void SetContractPlayertoComp(String fromUUID, String to, double amount, int weeks, String reason){
        Object[] tuple = {fromUUID, to, amount, weeks, reason};
        ContractPlayertoComp.add(tuple);

    }

    public static void SetContractServertoComp(String to, double amount, int weeks, String reason, int delay){
        String from = "Server";
        Object[] tuple = {from, to, amount, weeks, reason, delay};
        ContractServertoComp.add(tuple);

    }

    public static List<String> getBusinesses(){
        List<String> autoCompletes = new ArrayList<>();
        for (Company comp : company.values()){
            autoCompletes.add(comp.Sname);
        }
        return autoCompletes;
    }

    public static void getContracts(String stock_name, Player p){
        ArrayList<String> income = new ArrayList<>();
        ArrayList<String> outcome = new ArrayList<>();

        for (Object[] tuple : ContractComptoComp) {

            Company from = Market.get_business(tuple[0].toString());
            double amount = (Double) tuple[2];
            Company to = Market.get_business(tuple[1].toString());
            int weeks = (Integer) tuple[3];
            if (to.Sname.equals(stock_name)){
                income.add("§aContract: from " + from.Sname + "(C) to " + to.Sname + "(C) amount: " + amount
                        + " for "+weeks+" weeks, " + "reason: "+tuple[4]);
            }

            if (from.Sname.equals(stock_name)){
                outcome.add("§cContract: from " + from.Sname + "(C) to " + to.Sname + "(C) amount: " + amount
                        + " for "+weeks+" weeks, "+ "reason: "+tuple[4]);
            }

        }

        for (Object[] tuple : ContractComptoPlayer) {
            Company from = Market.get_business(tuple[0].toString());
            double amount = (Double) tuple[2];
            Account to = Market.get_account(UUID.fromString(tuple[1].toString()));
            int weeks = (Integer) tuple[3];

            if (from.Sname.equals(stock_name)){
                outcome.add("§cContract: from " + from.Sname + "(C) to " + Bukkit.getOfflinePlayer(to.uuid).getName() + "(P) amount: " + amount
                        + " for "+weeks+" weeks, "+ "reason: "+tuple[4]);
            }

        }

        for (Object[] tuple : ContractPlayertoComp) {
            try{
                Account from = Market.get_account(UUID.fromString(tuple[0].toString()));
                double amount = (Double) tuple[2];
                Company to = Market.get_business(tuple[1].toString());
                int weeks = (Integer) tuple[3];

                if (to.Sname.equals(stock_name)){
                    income.add("§aContract: from " + Bukkit.getOfflinePlayer(from.uuid).getName() + "(P) to " + to.Sname + "(C) amount: " + amount
                            + " for "+weeks+" weeks, "+ "reason: "+tuple[4]);
                }
            }catch (Exception e){

            }


        }

        for (Object[] tuple : ContractServertoComp) {
            double amount = (Double) tuple[2];
            Company to = Market.get_business(tuple[1].toString());
            int weeks = (Integer) tuple[3];
            if (to.Sname.equals(stock_name)){
                income.add("§aContract: from SERVER (S) to " + to.Sname + "(C) amount: " + amount
                        + " for "+weeks+" weeks, " + "reason: "+tuple[4] + " delay: "+tuple[5]);
            }
        }

        p.sendMessage("§aincome:");
        for (String s : income){
            p.sendMessage(s);
        }

        p.sendMessage("§coutgoing:");
        for (String s : outcome){
            p.sendMessage(s);
        }
    }

    public static void RunStockCompare(){
        for (Location o: stock_signs){
            Sign s = (Sign) o.getBlock().getState();
            SignStock.update(s);
        }

        for (Company comp: company.values()){
            comp.StockCompare();
        }
    }

    public static void RunWeeklyCompanySalary(){
        for (Company comp : company.values()){

            String country = comp.GetCountry();
            int total = 0;
            if (comp.type.equals("product")){
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 0, 4));
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 4, 2));
                total += (int) (Banking.getPolicyBonus(comp.GetCountry(), 0, 4)+Banking.getPolicyBonus(comp.GetCountry(), 4, 2));

            }else if (comp.type.equals("building")){

                total+= 1000;
                comp.add_bal(1000.0);

                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 0, 6));
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 3, 5));
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 4, 5));
                total += (int) (Banking.getPolicyBonus(comp.GetCountry(), 0, 6)+Banking.getPolicyBonus(comp.GetCountry(), 3, 5)+Banking.getPolicyBonus(comp.GetCountry(), 4, 5));
            }else if (comp.type.equals("military")){
                if (!Banking.aboard_military.getOrDefault(country, false)){
                    total+= 4000;
                    comp.add_bal(4000.0);
                }


                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 2, 5));
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 4, 4));
                total += (int) (Banking.getPolicyBonus(comp.GetCountry(), 2, 5)+Banking.getPolicyBonus(comp.GetCountry(), 4, 4));
            }else if (comp.type.equals("transport")){
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 2, 6));
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 3, 3));
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 4, 1));
                total += (int) (Banking.getPolicyBonus(comp.GetCountry(), 2, 6)+Banking.getPolicyBonus(comp.GetCountry(), 3, 3)+Banking.getPolicyBonus(comp.GetCountry(), 4, 1));
            }else if (comp.type.equals("bank")){
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 2, 7));
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 4, 0));
                total += (int) (Banking.getPolicyBonus(comp.GetCountry(), 2, 7)+Banking.getPolicyBonus(comp.GetCountry(), 4, 0));
            }else if(comp.type.equals("real estate")){
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 3, 4));
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 4, 3));
                total += (int) (Banking.getPolicyBonus(comp.GetCountry(), 3, 4)+Banking.getPolicyBonus(comp.GetCountry(), 4, 3));
            }else{
                comp.add_bal(0+Banking.getPolicyBonus(comp.GetCountry(), 4, 6));
                total += (int) Banking.getPolicyBonus(comp.GetCountry(), 4, 6);
            }

            if (!comp.open_trade){
                double value = Banking.getPolicyBonus(comp.GetCountry(), 1, 0);
                total += (int) value;
                if (value > 0.0){
                    comp.add_bal(value);
                }else{
                    comp.remove_bal(value*-1);
                }
            }

            if (total > 0){
                Banking.withdraw(country, total);
            }else{
                Banking.deposit(country, total);
            }
        }
    }



}
