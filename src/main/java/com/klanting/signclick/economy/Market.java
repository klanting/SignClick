package com.klanting.signclick.economy;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.routines.SignStock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.contracts.*;
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
    private static Map<String, Company> companies = new HashMap<String, Company>();

    public static final Double fee = SignClick.getPlugin().getConfig().getDouble("fee");
    public static final Double flux = SignClick.getPlugin().getConfig().getDouble("flux");

    public static ArrayList<Contract> contractCompToComp = new ArrayList<>();

    public static ArrayList<Contract> contractCompToPlayer = new ArrayList<>();

    public static ArrayList<Contract> contractPlayerToComp = new ArrayList<>();

    public static ArrayList<ContractSTC> contractServerToComp = new ArrayList<>();

    public static ArrayList<Location> stockSigns = new ArrayList<>();

    public static void clear(){
        /*
        * Clear all static information
        * */
        accounts.clear();
        companies.clear();

        contractCompToComp.clear();
        contractCompToPlayer.clear();
        contractServerToComp.clear();
        contractPlayerToComp.clear();
        stockSigns.clear();
    }

    public static Double getBuyPrice(String Sname, Integer amount){
        Company comp = Market.getCompany(Sname);

        double market_pct = (comp.getMarketShares().doubleValue()/(comp.getTotalShares().doubleValue()+Math.min(comp.getMarketShares(), 0)));
        double a = (1.0 - market_pct) * 25.0 - 10.0;

        market_pct = ((comp.getMarketShares().doubleValue()-amount.doubleValue())/(comp.getTotalShares().doubleValue()+Math.min(comp.getMarketShares(), 0)));
        double b = (1.0 - market_pct) * 25.0 - 10.0;


        double base = comp.getShareBase();
        double v = base * calculateFluxChange(a, b);
        return v*amount;
    }

    public static  Double getSellPrice(String Sname, Integer amount){

        String countryName = Market.getCompany(Sname).getCountry();
        Country country = CountryManager.getCountry(countryName);
        if (country == null){
            country = new CountryNull();
        }


        double sub_fee = fee;

        if (country.getStability() < 50){
            sub_fee += 0.01;
        }
        return (getBuyPrice(Sname, -amount)*-1)*(1.0 - (sub_fee - country.getPolicyBonus(0, 0)- country.getPolicyBonus(1, 1)));

    }


    public static Boolean buy(String Sname, Integer amount, Account acc){
        Company comp = getCompany(Sname);
        if (comp.getMarketShares() >= amount || comp.getCOM().isOpenTrade()){
            int market_am = comp.getMarketShares();
            comp.setMarketShares(market_am-amount);

            comp.getCOM().changeShareHolder(acc, amount);

            return true;
        }
        return false;
    }

    public static Boolean sell(String Sname, Integer amount, Account acc){
        Company comp = companies.get(Sname);

        int market_am = comp.getMarketShares();
        comp.setMarketShares(market_am+amount);

        comp.getCOM().changeShareHolder(acc, -amount);

        return true;
    }

    public static double calculateFluxChange(double a, double b){
        return (Math.pow(flux, b) - Math.pow(flux, a))/Math.log(flux)/(b-a);
    }

    public static Company getCompany(String Sname){
        return companies.get(Sname);
    }

    public static Boolean addCompany(String namebus, String StockName, Account acc){
        return Market.addCompany(namebus,StockName,acc, 0, "other");
    }

    public static Boolean addCompany(String namebus, String StockName, Account acc, double creationCost){
        return Market.addCompany(namebus,StockName,acc, creationCost, "other");
    }

    public static Boolean addCompany(String namebus, String StockName, Account acc, double creationCost, String type){

        /*
        * Check StockName already in use
        * */
        if (companies.containsKey(StockName)){
            return false;
        }

        /*
         * Check name already in use
         * */
        for (Company c: companies.values()){
            if (c.getName().equals(namebus)){
                return false;
            }
        }

        Company comp = new Company(namebus, StockName, acc, creationCost, type);
        companies.put(StockName, comp);

        comp.changeBase();

        comp.checkSupport();

        return true;
    }

    public static Boolean hasBusiness(String Sname){
        return companies.containsKey(Sname);
    }

    public static ArrayList<Company> getBusinessByDirector(UUID uuid){
        ArrayList<Company> outputs = new ArrayList<Company>();
        for(Map.Entry<String, Company> entry : companies.entrySet()){
            Board board = entry.getValue().getCOM().getBoard();
            if (board.getBoardMembers().contains(uuid)
                    || board.getChief("CEO").equals(uuid)
                    || board.getChief("CTO").equals(uuid)
                    || board.getChief("CFO").equals(uuid)
            ){
                outputs.add(entry.getValue());
            }
        }
        return outputs;
    }

    public static void getMarketValueTop(Player player){
        /*
        * Make a ranking of the top companies by value
        * */

        ArrayList<Map.Entry<String, Company>> entries = new ArrayList<>(companies.entrySet());

        entries.sort(Comparator.comparing(item -> -item.getValue().getValue()));

        for (int i=0; i<entries.size(); i++){
            String b = entries.get(i).getKey();
            Double v = entries.get(i).getValue().getValue();
            DecimalFormat df = new DecimalFormat("###,###,###");
            int i2 = i + 1;
            player.sendMessage("§b"+i2+". §3"+b+": §7" +df.format(v)+"\n");
        }
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

    public static void resetPatentCrafted(){

        for(Map.Entry<String, Company> entry : companies.entrySet()){
            Company comp = entry.getValue();
            comp.resetPatentCrafted();
        }

    }

    public static void runDividends(){

        for(Map.Entry<String, Company> entry : companies.entrySet()){
            Company comp = entry.getValue();
            comp.dividend();
        }

    }

    public static void marketAvailable(Player player){

        ArrayList<Company> entries = getTopMarketAvailable();

        entries.sort(Comparator.comparing(item -> -item.getMarketShares()));

        ArrayList<String> marketList = new ArrayList<>();

        marketList.add("§eMarket:");

        for (int i=0; i<entries.size(); i++){
            String b = entries.get(i).getStockName();
            Company comp = Market.getCompany(b);
            double v = entries.get(i).getMarketShares();
            DecimalFormat df = new DecimalFormat("###,###,###");
            DecimalFormat df2 = new DecimalFormat("0.00");
            int i2 = i + 1;

            if (Market.getCompany(b).getCOM().isOpenTrade()){
                marketList.add("§b"+i2+". §9"+b+": §7" +"inf"+" ("+"inf"+"%)");
            }else{
                marketList.add("§b"+i2+". §9"+b+": §7" +df.format(v)+" ("+df2.format((v/comp.getTotalShares().doubleValue()*100.0))+"%)");
            }

        }

        player.sendMessage(String.join("\n", marketList));
    }

    public static ArrayList<Company> getTopMarketAvailable(){
        ArrayList<Company> entries = new ArrayList<>(companies.values());

        entries.sort(Comparator.comparing(item -> -item.getMarketShares()));

        return entries;

    }

    public static void SaveData(){
        Utils.writeSave("accounts", accounts);

        Utils.writeSave("companies", companies);

        Utils.writeSave("contractCompToComp", contractCompToComp);

        Utils.writeSave("contractCompToPlayer", contractCompToPlayer);

        Utils.writeSave("contractServerToComp", contractServerToComp);

        Utils.writeSave("contractPlayerToComp", contractPlayerToComp);

        Utils.writeSave("stockSigns", stockSigns);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick save Market completed!");


    }

    public static void restoreData(){
        accounts = Utils.readSave("accounts", new TypeToken<HashMap<UUID, Account>>(){}.getType(), new HashMap<>());

        companies = Utils.readSave("companies", new TypeToken<HashMap<String, Company>>(){}.getType(), new HashMap<>());

        stockSigns = Utils.readSave("stockSigns", new TypeToken<ArrayList<Location>>(){}.getType(), new ArrayList<>());

        contractCompToComp = Utils.readSave("contractCompToComp",
                new TypeToken<ArrayList<ContractCTC>>(){}.getType(), new ArrayList<>());

        contractCompToPlayer = Utils.readSave("contractCompToPlayer",
                new TypeToken<ArrayList<ContractCTP>>(){}.getType(), new ArrayList<>());

        contractServerToComp = Utils.readSave("contractServerToComp",
                new TypeToken<ArrayList<ContractSTC>>(){}.getType(), new ArrayList<>());

        contractPlayerToComp = Utils.readSave("contractPlayerToComp",
                new TypeToken<ArrayList<ContractPTC>>(){}.getType(), new ArrayList<>());



    }

    public static void runContracts(){
        ArrayList<Contract> new_ctc = new ArrayList<>();
        for (Contract c: contractCompToComp){
            boolean keep = c.runContract();
            if (keep){
             new_ctc.add(c);
            }

        }

        contractCompToComp = new_ctc;

        ArrayList<Contract> new_ctp = new ArrayList<>();
        for (Contract c: contractCompToPlayer){

            boolean keep = c.runContract();
            if (keep){
                new_ctp.add(c);
            }

        }

        contractCompToPlayer = new_ctp;

        ArrayList<Contract> new_ptc = new ArrayList<>();
        for (Contract c: contractPlayerToComp){

            boolean keep = c.runContract();
            if (keep){
                new_ptc.add(c);
            }

        }

        contractPlayerToComp = new_ptc;

        ArrayList<ContractSTC> new_stc = new ArrayList<>();
        for (ContractSTC c: contractServerToComp){

            boolean keep = c.runContract();
            if (keep){
                new_stc.add(c);
            }

        }

        contractServerToComp = new_stc;
    }

    public static void setContractComptoComp(String from, String to, double amount, int weeks, String reason){
        ContractCTC ctc = new ContractCTC(Market.getCompany(from), Market.getCompany(to), amount, weeks, reason);
        contractCompToComp.add(ctc);

        Market.getCompany(from).update("Contract Signed", ctc.getContractStatus(false), null);
        Market.getCompany(to).update("Contract Signed", ctc.getContractStatus(true), null);

    }

    public static void setContractComptoPlayer(String from, String toUUID, double amount, int weeks, String reason){
        Contract contract = new ContractCTP(Market.getCompany(from), UUID.fromString(toUUID),
                amount, weeks, reason);

        contractCompToPlayer.add(contract);

        Market.getCompany(from).update("Contract Signed", contract.getContractStatus(false), null);

    }

    public static void setContractPlayertoComp(String fromUUID, String to, double amount, int weeks, String reason){

        Contract contract = new ContractPTC(UUID.fromString(fromUUID),
                Market.getCompany(to), amount, weeks, reason);

        contractPlayerToComp.add(contract);

        Market.getCompany(to).update("Contract Signed", contract.getContractStatus(true), null);

    }

    public static void setContractServertoComp(String to, double amount, int weeks, String reason, int delay){
        ContractSTC contract = new ContractSTC(Market.getCompany(to), amount, weeks, reason, delay);

        contractServerToComp.add(contract);

        Market.getCompany(to).update("Contract Signed", contract.getContractStatus(true), null);

    }

    public static List<String> getBusinesses(){
        List<String> autoCompletes = new ArrayList<>();
        for (Company comp : companies.values()){
            autoCompletes.add(comp.getStockName());
        }
        return autoCompletes;
    }

    public static void getContracts(String stockName, Player p){
        ArrayList<String> income = new ArrayList<>();
        ArrayList<String> outcome = new ArrayList<>();

        for (Contract c : contractCompToComp) {

            Company from = Market.getCompany(c.from());
            Company to = Market.getCompany(c.to());
            if (to.getStockName().equals(stockName)){
                income.add(c.getContractStatus(true));
            }

            if (from.getStockName().equals(stockName)){
                outcome.add(c.getContractStatus(false));
            }

        }

        for (Contract c : contractCompToPlayer) {
            Company from = Market.getCompany(c.from());

            if (from.getStockName().equals(stockName)){
                outcome.add(c.getContractStatus(false));
            }

        }

        for (Contract c : contractPlayerToComp) {
            Company to = Market.getCompany(c.to());

            if (to.getStockName().equals(stockName)){
                income.add(c.getContractStatus(true));
            }

        }

        for (ContractSTC c : contractServerToComp) {
            Company to = Market.getCompany(c.to());

            if (to.getStockName().equals(stockName)){

                income.add(c.getContractStatus(true));
            }
        }

        List<String> result = new ArrayList<>();
        result.add("§aincome:");
        result.addAll(income);
        result.add("§coutgoing:");
        result.addAll(outcome);

        p.sendMessage(String.join("§0\n", result));

    }

    public static void runStockCompare(){
        for (Location o: stockSigns){
            Sign s = (Sign) o.getBlock().getState();
            SignStock.update(s);
        }

        for (Company comp: companies.values()){
            comp.stockCompare();
        }
    }

    public static void runWeeklyCompanySalary(){
        for (Company comp : companies.values()){

            comp.getCOM().getBoard().paySalaries(comp);

            String countryName = comp.getCountry();
            Country country = CountryManager.getCountry(countryName);
            if (country == null){
                country = new CountryNull();
            }

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
                if (country != null){
                    comp.addBal(0+ country.getPolicyBonus(4, 6));
                    total += (int) country.getPolicyBonus(4, 6);
                }

            }

            if (!comp.getCOM().isOpenTrade()){
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
