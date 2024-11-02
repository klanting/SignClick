package com.klanting.signclick.economy;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.calculate.SignStock;
import com.klanting.signclick.economy.companyPatent.*;
import com.klanting.signclick.economy.companyUpgrades.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.contracts.Contract;
import com.klanting.signclick.economy.contracts.ContractCTC;
import com.klanting.signclick.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class Market {
    /*
    * Stores which account corresponds to which UUID (player)
    * */
    private static Map<UUID, Account> accounts = new HashMap<UUID, Account>();
    private static Map<String, Company> company = new HashMap<String, Company>();
    private static final Map<String, Double> shareValues = new HashMap<String, Double>();
    private static final Map<String, Double> shareBase = new HashMap<String, Double>();

    public static final Double fee = 0.05;
    public static final Double flux = 1.15;

    public static Boolean showParticles = true;

    private static final ArrayList<String> names = new ArrayList<String>();

    public static Double getValue(String Sname){
        return shareValues.get(Sname);
    }

    public static ArrayList<Contract> contractCompToComp = new ArrayList<>();

    public static ArrayList<Object[]> ContractComptoPlayer = new ArrayList<Object[]>();

    public static ArrayList<Object[]> ContractPlayertoComp = new ArrayList<Object[]>();

    public static ArrayList<Object[]> ContractServertoComp = new ArrayList<Object[]>();

    public static ArrayList<Location> stock_signs = new ArrayList<Location>();

    public static void clear(){
        /*
        * Clear all static information
        * */
        accounts.clear();
        company.clear();
        shareValues.clear();
        shareBase.clear();

        names.clear();
        contractCompToComp.clear();
        ContractComptoPlayer.clear();
        ContractServertoComp.clear();
        ContractPlayertoComp.clear();
        stock_signs.clear();
    }

    public static Double getBuyPrice(String Sname, Integer amount){
        Company comp = Market.getBusiness(Sname);

        double market_pct = (comp.getMarketShares().doubleValue()/(comp.getTotalShares().doubleValue()+Math.min(comp.getMarketShares(), 0)));
        double a = (1.0 - market_pct) * 25.0 - 10.0;

        market_pct = ((comp.getMarketShares().doubleValue()-amount.doubleValue())/(comp.getTotalShares().doubleValue()+Math.min(comp.getMarketShares(), 0)));
        double b = (1.0 - market_pct) * 25.0 - 10.0;

        double base = shareBase.get(Sname);
        double v = base * ((Math.pow(flux, b)) - (Math.pow(flux, a))) /Math.log(flux)/(b-a);
        return v*amount;
    }

    public static  Double getSellPrice(String Sname, Integer amount){

        String countryName = Market.getBusiness(Sname).getCountry();
        Country country = CountryManager.getCountry(countryName);


        double sub_fee = fee;

        if (country == null){
            //TODO make this not temp
            return (getBuyPrice(Sname, -amount)*-1)*(1-sub_fee);
        }

        if (country.getStability() < 50){
            sub_fee += 0.01;
        }
        return (getBuyPrice(Sname, -amount)*-1)*(1.0 - (sub_fee - country.getPolicyBonus(0, 0)- country.getPolicyBonus(1, 1)));

    }

    public static void changeValue(String Sname){
        Company comp = Market.getBusiness(Sname);
        double market_pct = (comp.getMarketShares().doubleValue()/comp.getTotalShares().doubleValue());
        double x = (1.0 - market_pct)*25.0 - 10.0;

        double base = shareBase.get(Sname);
        shareValues.put(Sname, base*Math.pow(flux, x));
    }

    public static Boolean buy(String Sname, Integer amount, Account acc){
        Company comp = getBusiness(Sname);
        if (comp.getMarketShares() >= amount || comp.openTrade){
            int market_am = comp.getMarketShares();
            comp.marketShares = market_am-amount;

            comp.changeShareHolder(acc, amount);

            changeValue(Sname);

            return true;
        }
        return false;
    }

    public static Boolean sell(String Sname, Integer amount, Account acc){
        Company comp = company.get(Sname);

        int market_am = comp.getMarketShares();
        comp.marketShares = market_am+amount;

        comp.changeShareHolder(acc, -amount);

        changeValue(Sname);

        return true;
    }

    public static void changeBase(String Sname){
        Company comp = company.get(Sname);
        double a = -10.0;
        double b = 15.0;
        double v = (comp.getBal()/comp.getTotalShares()) / (((Math.pow(flux, b)) - (Math.pow(flux, a))) /Math.log(flux)/(b-a));
        shareBase.put(Sname, v);

        changeValue(Sname);
    }

    public static Company getBusiness(String Sname){
        return company.get(Sname);
    }

    public static Boolean addBusiness(String namebus, String Sname, Account acc){
        if (!names.contains(namebus)){
            names.add(namebus);
            Company comp = new Company(namebus, Sname, acc);
            company.put(Sname, comp);

            comp.marketShares = 0;

            comp.totalShares = 1000000;

            shareBase.put(Sname, 0.0);
            changeBase(Sname);

            shareValues.put(Sname, 0.0);
            changeValue(Sname);

            comp.checkSupport();
            comp.calculateCountry();

            return true;
        }
        return false;
    }

    public static Boolean hasBusiness(String Sname){
        return company.containsKey(Sname);
    }

    public static ArrayList<Company> getBusinessByOwner(UUID uuid){
        ArrayList<Company> outputs = new ArrayList<Company>();
        for(Map.Entry<String, Company> entry : company.entrySet()){
            if (entry.getValue().isOwner(uuid)){
                outputs.add(entry.getValue());
            }
        }
        return outputs;
    }

    public static void getMarketValueTop(Player player){
        ArrayList<String> order = new ArrayList<String>();
        ArrayList<Double> values = new ArrayList<Double>();

        for(Map.Entry<String, Company> entry : company.entrySet()){
            String b = entry.getKey();
            Double v = entry.getValue().getValue();

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


    public static Double getFee(){
        return fee;
    }

    public static Boolean hasAccount(Player player){

        return accounts.containsKey(player.getUniqueId());

    }

    public static void createAccount(Player player){
        createAccount(player.getUniqueId());
    }

    public static void createAccount(UUID uuid){
        Account acc = new Account(uuid);
        accounts.put(uuid, acc);

    }

    public static Account getAccount(Player player){

        return getAccount(player.getUniqueId());

    }

    public static Account getAccount(UUID uuid){
        if (!accounts.containsKey(uuid)){
            Market.createAccount(uuid);
        }
        return accounts.get(uuid);

    }


    public static Boolean hasAccount(UUID uuid){
        return accounts.containsKey(uuid);

    }



    public static void addCompany(String namebus, String Sname, ArrayList<UUID> owners,
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
        comp.shareHolders = share_holders;
        comp.lastValue = last;
        comp.securityFunds = sf;
        comp.openTrade = open_trade;
        comp.type = type;
        company.put(Sname, comp);

        comp.marketShares = ma;

        comp.totalShares = to;

        shareBase.put(Sname, sb);
        changeBase(Sname);

        shareValues.put(Sname, sv);
        changeValue(Sname);

        comp.checkSupport();
        comp.calculateCountry();
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
                    comp.patentUpgrades.add(up);
                }else if (id == 0){
                    PatentUpgrade up = new PatentUpgradeJumper();
                    up.level = level;
                    comp.patentUpgrades.add(up);
                }else if (id == 1){
                    PatentUpgrade up = new PatentUpgradeEvade();
                    up.level = level;
                    comp.patentUpgrades.add(up);
                }else if (id == 2){
                    PatentUpgrade up = new PatentUpgradeRefill();
                    up.level = level;
                    comp.patentUpgrades.add(up);
                }else if (id == 3){
                    PatentUpgrade up = new PatentUpgradeCunning();
                    up.level = level;
                    comp.patentUpgrades.add(up);
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
                    p_ups.add(comp.patentUpgrades.get(Integer.parseInt(index)));
                }

                Patent p =  new Patent(name, item, p_ups);
                p.createCraft(comp);
                comp.patent.add(p);
                counter++;
            }
        }

    }

    public static double getBooks(String Sname){
        Company comp = getBusiness(Sname);
        return comp.books;
    }

    public static void resetSpendable(){

        for(Map.Entry<String, Company> entry : company.entrySet()){
            Company comp = entry.getValue();
            comp.resetSpendable();
        }

    }

    public static void resetPatentCrafted(){

        for(Map.Entry<String, Company> entry : company.entrySet()){
            Company comp = entry.getValue();
            comp.resetPatentCrafted();
        }

    }

    public static void runDividends(){

        for(Map.Entry<String, Company> entry : company.entrySet()){
            Company comp = entry.getValue();
            comp.dividend();
        }

    }

    public static void marketAvailable(Player player){
        ArrayList<String> order = new ArrayList<String>();
        ArrayList<Integer> values = new ArrayList<Integer>();
        for(Map.Entry<String, Company> entry : company.entrySet()){
            String b = entry.getKey();
            int v = entry.getValue().getMarketShares();

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
            Company comp = Market.getBusiness(b);
            double v = values.get(i);
            DecimalFormat df = new DecimalFormat("###,###,###");
            DecimalFormat df2 = new DecimalFormat("0.00");
            int i2 = i + 1;

            if (Market.getBusiness(b).openTrade){
                player.sendMessage("§b"+i2+". §9"+b+": §7" +"inf"+" ("+"inf"+"%)\n");
            }else{
                player.sendMessage("§b"+i2+". §9"+b+": §7" +df.format(v)+" ("+df2.format((v/comp.getTotalShares().doubleValue()*100.0))+"%)\n");
            }

        }
    }

    public static void SaveData(){
        Utils.writeSave("accounts", accounts);

        Utils.writeSave("companies", company);

        Utils.writeSave("contractCompToComp", contractCompToComp);

        List<String>  contractString = new ArrayList<>();
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

        SignClick.getPlugin().getConfig().set("show_particles", showParticles);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick save Market completed!");

        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();
    }

    public static void restoreData(){
        accounts = Utils.readSave("accounts", new TypeToken<HashMap<UUID, Account>>(){}.getType(), new HashMap<>());

        company = Utils.readSave("companies", new TypeToken<HashMap<String, Company>>(){}.getType(), new HashMap<>());

        if (SignClick.getPlugin().getConfig().contains("company") && false){
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

                Market.addCompany(name, Sname, owners, bal, books, spendable, su, sh, sv, sb, ma, to, last_value, security_funds, upgrades, open_trade, type);

            }
        }

        if (SignClick.getPlugin().getConfig().contains("sign") && !SignClick.getPlugin().getConfig().get("sign").equals("[]")){

            for (Location s :  (List<Location>) SignClick.getPlugin().getConfig().get("sign")) {
                stock_signs.add(s);
            }
        }

        contractCompToComp = Utils.readSave("contractCompToComp",
                new TypeToken<ArrayList<ContractCTC>>(){}.getType(), new ArrayList<>());

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
            showParticles = (Boolean) SignClick.getPlugin().getConfig().get("show_particles", false);
        }


    }

    public static void runContracts(){
        // still crashes
        ArrayList<Contract> new_ctc = new ArrayList<>();
        for (Contract c: contractCompToComp){
            boolean keep = c.runContract();
            if (keep){
             new_ctc.add(c);
            }

        }

        contractCompToComp = new_ctc;

        ArrayList<Object[]> new_data = new ArrayList<>();
        for (Object[] tuple : ContractComptoPlayer) {
            Company from = Market.getBusiness(tuple[0].toString());
            double amount = (Double) tuple[2];
            if (from.removeBal(amount)) {
                Account to = Market.getAccount(UUID.fromString(tuple[1].toString()));
                int weeks = (Integer) tuple[3];
                weeks -= 1;
                to.addBal(amount);
                if (weeks > 0) {
                    Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4]};
                    new_data.add(new_tuple);
                }

                from.sendOwner("§cContract: from " + from.getStockName() + "(C) to " + to.getName() + "(P) amount: " + amount);
                if (to.getPlayer() != null){
                    to.getPlayer().sendMessage("§aContract: from " + from.getStockName() + "(C) to " + to.getName() + "(P) amount: " + amount);
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
                Account from = Market.getAccount(UUID.fromString(tuple[0].toString()));
                double amount = (Double) tuple[2];
                if (from.removeBal(amount)) {
                    Company to = Market.getBusiness(tuple[1].toString());
                    int weeks = (Integer) tuple[3];
                    weeks -= 1;
                    to.addBal(amount);
                    if (weeks > 0) {
                        Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4]};
                        new_data.add(new_tuple);
                    }

                    to.sendOwner("§aContract: from " + from.getName() + "(P) to " + to.getStockName() + "(C) amount: " + amount);
                    if (from.getPlayer() != null){
                        from.getPlayer().sendMessage("§cContract: from " + from.getName() + "(P) to " + to.getStockName() + "(C) amount: " + amount);
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
            Company to = Market.getBusiness(tuple[1].toString());
            int weeks = (Integer) tuple[3];
            int delay = (Integer) tuple[5];

            if (delay == 0){
                weeks -= 1;
                to.addBalNoPoint(amount);
                if (weeks > 0) {
                    Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4], delay};
                    new_data.add(new_tuple);
                }

                to.sendOwner("§aContract: from SERVER (S) to " + to.getStockName() + "(C) amount: " + amount);
            }else{
                delay -= 1;
                Object[] new_tuple = {tuple[0], tuple[1], amount, weeks, tuple[4], delay};
                new_data.add(new_tuple);
            }

        }

        ContractServertoComp = new_data;
    }

    public static void setContractComptoComp(String from, String to, double amount, int weeks, String reason){
        contractCompToComp.add(new ContractCTC(Market.getBusiness(from), Market.getBusiness(to), amount, weeks, reason));

    }

    public static void setContractComptoPlayer(String from, String toUUID, double amount, int weeks, String reason){
        Object[] tuple = {from, toUUID, amount, weeks, reason};
        ContractComptoPlayer.add(tuple);

    }

    public static void setContractPlayertoComp(String fromUUID, String to, double amount, int weeks, String reason){
        Object[] tuple = {fromUUID, to, amount, weeks, reason};
        ContractPlayertoComp.add(tuple);

    }

    public static void setContractServertoComp(String to, double amount, int weeks, String reason, int delay){
        String from = "Server";
        Object[] tuple = {from, to, amount, weeks, reason, delay};
        ContractServertoComp.add(tuple);

    }

    public static List<String> getBusinesses(){
        List<String> autoCompletes = new ArrayList<>();
        for (Company comp : company.values()){
            autoCompletes.add(comp.getStockName());
        }
        return autoCompletes;
    }

    public static void getContracts(String stock_name, Player p){
        ArrayList<String> income = new ArrayList<>();
        ArrayList<String> outcome = new ArrayList<>();

        for (Contract c : contractCompToComp) {

            Company from = Market.getBusiness(c.from());
            double amount = c.getAmount();
            Company to = Market.getBusiness(c.to());
            int weeks = c.getWeeks();
            if (to.getStockName().equals(stock_name)){
                income.add("§aContract: from " + from.getStockName() + "(C) to " + to.getStockName() + "(C) amount: " + amount
                        + " for "+weeks+" weeks, " + "reason: "+c.getReason());
            }

            if (from.getStockName().equals(stock_name)){
                outcome.add("§cContract: from " + from.getStockName() + "(C) to " + to.getStockName() + "(C) amount: " + amount
                        + " for "+weeks+" weeks, "+ "reason: "+c.getReason());
            }

        }

        for (Object[] tuple : ContractComptoPlayer) {
            Company from = Market.getBusiness(tuple[0].toString());
            double amount = (Double) tuple[2];
            Account to = Market.getAccount(UUID.fromString(tuple[1].toString()));
            int weeks = (Integer) tuple[3];

            if (from.getStockName().equals(stock_name)){
                outcome.add("§cContract: from " + from.getStockName() + "(C) to " + to.getName() + "(P) amount: " + amount
                        + " for "+weeks+" weeks, "+ "reason: "+tuple[4]);
            }

        }

        for (Object[] tuple : ContractPlayertoComp) {
            try{
                Account from = Market.getAccount(UUID.fromString(tuple[0].toString()));
                double amount = (Double) tuple[2];
                Company to = Market.getBusiness(tuple[1].toString());
                int weeks = (Integer) tuple[3];

                if (to.getStockName().equals(stock_name)){
                    income.add("§aContract: from " + from.getName() + "(P) to " + to.getStockName() + "(C) amount: " + amount
                            + " for "+weeks+" weeks, "+ "reason: "+tuple[4]);
                }
            }catch (Exception e){

            }

        }

        for (Object[] tuple : ContractServertoComp) {
            double amount = (Double) tuple[2];
            Company to = Market.getBusiness(tuple[1].toString());
            int weeks = (Integer) tuple[3];
            if (to.getStockName().equals(stock_name)){
                income.add("§aContract: from SERVER (S) to " + to.getStockName() + "(C) amount: " + amount
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

    public static void runStockCompare(){
        for (Location o: stock_signs){
            Sign s = (Sign) o.getBlock().getState();
            SignStock.update(s);
        }

        for (Company comp: company.values()){
            comp.stockCompare();
        }
    }

    public static void runWeeklyCompanySalary(){
        for (Company comp : company.values()){

            String countryName = comp.getCountry();
            Country country = CountryManager.getCountry(countryName);
            int total = 0;

            if (comp.type.equals("product")){

                comp.addBal(0+ country.getPolicyBonus(0, 4));
                comp.addBal(0+ country.getPolicyBonus(4, 2));
                total += (int) (country.getPolicyBonus(0, 4)+ country.getPolicyBonus(4, 2));

            }else if (comp.type.equals("building")){

                total+= 1000;
                comp.addBal(1000.0);

                comp.addBal(0+ country.getPolicyBonus(0, 6));
                comp.addBal(0+ country.getPolicyBonus(3, 5));
                comp.addBal(0+ country.getPolicyBonus(4, 5));
                total += (int) (country.getPolicyBonus(0, 6)+ country.getPolicyBonus(3, 5)+ country.getPolicyBonus(4, 5));
            }else if (comp.type.equals("military")){

                if (!country.isAboardMilitary()){
                    total+= 4000;
                    comp.addBal(4000.0);
                }

                comp.addBal(0+ country.getPolicyBonus(2, 5));
                comp.addBal(0+ country.getPolicyBonus(4, 4));
                total += (int) (country.getPolicyBonus(2, 5)+ country.getPolicyBonus(4, 4));
            }else if (comp.type.equals("transport")){
                comp.addBal(0+ country.getPolicyBonus(2, 6));
                comp.addBal(0+ country.getPolicyBonus(3, 3));
                comp.addBal(0+ country.getPolicyBonus(4, 1));
                total += (int) (country.getPolicyBonus(2, 6)+ country.getPolicyBonus(3, 3)+ country.getPolicyBonus(4, 1));
            }else if (comp.type.equals("bank")){
                comp.addBal(0+ country.getPolicyBonus(2, 7));
                comp.addBal(0+ country.getPolicyBonus(4, 0));
                total += (int) (country.getPolicyBonus(2, 7)+ country.getPolicyBonus(4, 0));
            }else if(comp.type.equals("real estate")){
                comp.addBal(0+ country.getPolicyBonus(3, 4));
                comp.addBal(0+country.getPolicyBonus(4, 3));
                total += (int) (country.getPolicyBonus(3, 4)+ country.getPolicyBonus(4, 3));
            }else{
                comp.addBal(0+ country.getPolicyBonus(4, 6));
                total += (int) country.getPolicyBonus(4, 6);
            }

            if (!comp.openTrade){
                double value = country.getPolicyBonus(1, 0);
                total += (int) value;
                if (value > 0.0){
                    comp.addBal(value);
                }else{
                    comp.removeBal(value*-1);
                }
            }

            if (total > 0){
                country.withdraw(total);
            }else{
                country.deposit(total);
            }
        }
    }



}
