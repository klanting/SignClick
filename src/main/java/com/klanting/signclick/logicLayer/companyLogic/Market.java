package com.klanting.signclick.logicLayer.companyLogic;

import com.google.common.reflect.TypeToken;
import com.klanting.signclick.interactionLayer.routines.SignStock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.contracts.*;
import com.klanting.signclick.logicLayer.companyLogic.producible.License;
import com.klanting.signclick.logicLayer.companyLogic.producible.LicenseSingleton;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.countryLogic.CountryNull;
import com.klanting.signclick.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

import static com.klanting.signclick.utils.Utils.AssertMet;
import static org.bukkit.Bukkit.getServer;

public class Market {
    /*
    * Stores which account corresponds to which UUID (player)
    * */
    private static Map<UUID, Account> accounts = new HashMap<UUID, Account>();
    private static Map<String, CompanyI> companies = new HashMap<String, CompanyI>();

    public static final Double fee = SignClick.getConfigManager().getConfig("companies.yml").getDouble("fee");
    public static final Double flux = SignClick.getConfigManager().getConfig("companies.yml").getDouble("flux");

    public static ArrayList<Contract> contractCompToComp = new ArrayList<>();

    public static ArrayList<Contract> contractCompToPlayer = new ArrayList<>();

    public static ArrayList<Contract> contractPlayerToComp = new ArrayList<>();

    public static ArrayList<ContractSTC> contractServerToComp = new ArrayList<>();

    public static ArrayList<Location> stockSigns = new ArrayList<>();

    public static void clear(){
        /*
        * Clear all static information
        * TESTING ONLY
        * */
        accounts.clear();
        companies.clear();

        contractCompToComp.clear();
        contractCompToPlayer.clear();
        contractServerToComp.clear();
        contractPlayerToComp.clear();
        stockSigns.clear();
    }

    public static Double getShareBalSellPrice(String Sname, Integer amount){
        AssertMet(amount >= 0, "Cannot get shareBal sell price for negative amount of shares");

        CompanyI comp = Market.getCompany(Sname);
        comp.reCalcBalance();

        double market_pct = (comp.getMarketShares().doubleValue()/(comp.getTotalShares().doubleValue()+Math.min(comp.getMarketShares(), 0)));
        double a = (1.0 - market_pct) * 25.0 - 10.0;

        market_pct = ((comp.getMarketShares().doubleValue()+amount.doubleValue())/(comp.getTotalShares().doubleValue()+Math.min(comp.getMarketShares(), 0)));
        double b = (1.0 - market_pct) * 25.0 - 10.0;

        double sharebalPCT = (calculateFluxChange(a, b)*(b-a)/(calculateFluxChange(a, -10)*(-10-a)));

        return sharebalPCT*comp.getShareBalance();
    }

    public static Double getBalSellPrice(String Sname, Integer amount){
        AssertMet(amount >= 0, "Cannot get Bal sell price for negative amount of shares");

        CompanyI comp = Market.getCompany(Sname);
        comp.reCalcBalance();
        double base = comp.getShareBase(true)*Market.calculateFluxChange(-10, 15);
        return base*amount;
    }

    public static Double getBuyPrice(String Sname, Integer amount){
        CompanyI comp = Market.getCompany(Sname);
        comp.reCalcBalance();

        double market_pct = (comp.getMarketShares().doubleValue()/(comp.getTotalShares().doubleValue()+Math.min(comp.getMarketShares(), 0)));
        double a = (1.0 - market_pct) * 25.0 - 10.0;

        market_pct = ((comp.getMarketShares().doubleValue()-amount.doubleValue())/(comp.getTotalShares().doubleValue()+Math.min(comp.getMarketShares(), 0)));
        double b = (1.0 - market_pct) * 25.0 - 10.0;


        double base = comp.getShareBase();

        AssertMet(base >= 0, "Share base price needs to be positive");

        double v = base * calculateFluxChange(a, b);
        return v*amount;
    }

    public static double getKeepFee(Country country){
        double sub_fee = fee;

        if (country.getStability() < 50){
            sub_fee += 0.01;
        }

        double keepPCT = (1.0 - (sub_fee - country.getPolicyBonus("taxReduction")));

        AssertMet(keepPCT > 0.0, "Player needs to keep at least more than 0 from selling shares");
        AssertMet(keepPCT <= 1.0, "Player can only keep 100% of the shares at most");

        return  keepPCT;
    }

    public static  Double getSellPrice(String Sname, Integer amount){
        AssertMet(amount >= 0, "Cannot get sell price for negative amount of shares");

        String countryName = Market.getCompany(Sname).getCountry();
        Country country = CountryManager.getCountry(countryName);
        if (country == null){
            country = new CountryNull();
        }

        double totalValue = getShareBalSellPrice(Sname, amount)+getBalSellPrice(Sname, amount);

        double keepPCT = getKeepFee(country);

        return totalValue * keepPCT;

    }


    public static Boolean buy(String Sname, Integer amount, Account acc){
        CompanyI comp = getCompany(Sname);
        if (comp.getMarketShares() >= amount || comp.getCOM().isOpenTrade()){
            int market_am = comp.getMarketShares();
            comp.setMarketShares(market_am-amount);

            comp.getCOM().changeShareHolder(acc, amount);

            return true;
        }
        return false;
    }

    public static Boolean sell(String Sname, Integer amount, Account acc){
        CompanyI comp = companies.get(Sname);

        int market_am = comp.getMarketShares();
        comp.setMarketShares(market_am+amount);

        comp.getCOM().changeShareHolder(acc, -amount);

        return true;
    }

    public static double calculateFluxChange(double a, double b){
        return (Math.pow(flux, b) - Math.pow(flux, a))/Math.log(flux)/(b-a);
    }

    public static CompanyI getCompany(String Sname){
        return companies.get(Sname);
    }

    public static Boolean addCompany(String namebus, String StockName, Account acc){
        return Market.addCompany(namebus,StockName,acc, 0, "Miscellaneous");
    }

    public static Boolean addCompany(String namebus, String StockName, Account acc, double creationCost){
        return Market.addCompany(namebus,StockName,acc, creationCost, "Miscellaneous");
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
        for (CompanyI c: companies.values()){
            if (c.getName().equals(namebus)){
                return false;
            }
        }

        CompanyI comp = new Company(namebus, StockName, acc, creationCost, type);
        companies.put(StockName, comp);

        comp.changeBase();

        comp.checkSupport();

        return true;
    }

    public static Boolean hasBusiness(String Sname){
        return companies.containsKey(Sname);
    }

    public static List<CompanyI> getBusinessExclude(CompanyI company){
        return companies.values().stream().filter(s -> s != company).toList();
    }

    public static ArrayList<CompanyI> getBusinessByShares(UUID uuid){
        ArrayList<CompanyI> outputs = new ArrayList<>();
        for(Map.Entry<String, CompanyI> entry : companies.entrySet()){

            if(entry.getValue().getCOM().getShareHolders().keySet().contains(uuid)){
                outputs.add(entry.getValue());
            }

        }
        return outputs;
    }


    public static ArrayList<CompanyI> getBusinessByDirector(UUID uuid){
        ArrayList<CompanyI> outputs = new ArrayList<CompanyI>();
        for(Map.Entry<String, CompanyI> entry : companies.entrySet()){
            Board board = entry.getValue().getCOM().getBoard();
            if (board.getBoardMembers().contains(uuid)
                    || (board.getChief("CEO") != null && board.getChief("CEO").equals(uuid))
                    || (board.getChief("CTO") != null && board.getChief("CTO").equals(uuid))
                    || (board.getChief("CFO") != null && board.getChief("CFO").equals(uuid))
            ){
                outputs.add(entry.getValue());
            }
        }

        return outputs;
    }

    public static ArrayList<CompanyI> getBusinessByChief(UUID uuid){
        ArrayList<CompanyI> outputs = new ArrayList<CompanyI>();
        for(Map.Entry<String, CompanyI> entry : companies.entrySet()){
            Board board = entry.getValue().getCOM().getBoard();
            if ((board.getChief("CEO") != null && board.getChief("CEO").equals(uuid))
                || (board.getChief("CTO") != null && board.getChief("CTO").equals(uuid))
                || (board.getChief("CFO") != null && board.getChief("CFO").equals(uuid))
            ){
                outputs.add(entry.getValue());
            }
        }

        return outputs;
    }

    public static void getMarketValueTop(Player player, int page){
        /*
        * Make a ranking of the top companies by value
        * */
        int index = page-1;

        ArrayList<Map.Entry<String, CompanyI>> entries = new ArrayList<>(companies.entrySet());

        entries.sort(Comparator.comparing(item -> -item.getValue().getValue()));

        player.sendMessage(SignClick.getPrefix()+"Balancetop: page "+page+"/"+(int) Math.ceil(entries.size()/10.0));
        for (int i=index*10; i<Math.min(entries.size(), (index*10)+10); i++){
            String b = entries.get(i).getKey();
            Double v = entries.get(i).getValue().getValue();
            DecimalFormat df = new DecimalFormat("###,###,###");
            int i2 = i + 1;
            player.sendMessage(SignClick.getPrefix()+i2+". §3"+b+": §7" +df.format(v)+"\n");
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

    public static void runDividends(){

        for(Map.Entry<String, CompanyI> entry : companies.entrySet()){
            CompanyI comp = entry.getValue();
            comp.dividend();
        }

    }

    public static void runLicenses(){

        for(Map.Entry<String, CompanyI> entry : companies.entrySet()){
            CompanyI comp = entry.getValue();
            List<License> licenses = LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp);
            for (License license: licenses){
                if (license.getTo().removeBal(license.getWeeklyCost())){
                    license.getFrom().addBal(license.getWeeklyCost());
                }else{
                    license.frozenByLicenseCost += license.getWeeklyCost();
                }
            }
        }

    }

    public static void marketAvailable(Player player){

        ArrayList<CompanyI> entries = getTopMarketAvailable();

        entries.sort(Comparator.comparing(item -> -item.getMarketShares()));

        ArrayList<String> marketList = new ArrayList<>();

        marketList.add("§eMarket:");

        for (int i=0; i<entries.size(); i++){
            String b = entries.get(i).getStockName();
            CompanyI comp = Market.getCompany(b);
            double v = entries.get(i).getMarketShares();
            DecimalFormat df = new DecimalFormat("###,###,###");
            DecimalFormat df2 = new DecimalFormat("0.00");
            int i2 = i + 1;

            if (Market.getCompany(b).getCOM().isOpenTrade()){
                marketList.add(SignClick.getPrefix()+i2+". §9"+b+": §7" +"inf"+" ("+"inf"+"%)");
            }else{
                marketList.add(SignClick.getPrefix()+i2+". §9"+b+": §7" +df.format(v)+" ("+df2.format((v/comp.getTotalShares().doubleValue()*100.0))+"%)");
            }

        }

        player.sendMessage(String.join("\n", marketList));
    }

    public static ArrayList<CompanyI> getTopMarketAvailable(){
        ArrayList<CompanyI> entries = new ArrayList<>(companies.values());

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

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick: save Companies completed!");


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
        for (CompanyI comp : companies.values()){
            autoCompletes.add(comp.getStockName());
        }
        return autoCompletes;
    }

    public static void getContracts(String stockName, Player p){
        ArrayList<String> income = new ArrayList<>();
        ArrayList<String> outcome = new ArrayList<>();

        for (Contract c : contractCompToComp) {

            CompanyI from = Market.getCompany(c.from());
            CompanyI to = Market.getCompany(c.to());
            if (to.getStockName().equals(stockName)){
                income.add(c.getContractStatus(true));
            }

            if (from.getStockName().equals(stockName)){
                outcome.add(c.getContractStatus(false));
            }

        }

        for (Contract c : contractCompToPlayer) {
            CompanyI from = Market.getCompany(c.from());

            if (from.getStockName().equals(stockName)){
                outcome.add(c.getContractStatus(false));
            }

        }

        for (Contract c : contractPlayerToComp) {
            CompanyI to = Market.getCompany(c.to());

            if (to.getStockName().equals(stockName)){
                income.add(c.getContractStatus(true));
            }

        }

        for (ContractSTC c : contractServerToComp) {
            CompanyI to = Market.getCompany(c.to());

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

        for (CompanyI comp: companies.values()){
            comp.stockCompare();
        }
    }

    public static void runWeeklyCompanySalary(){
        for (CompanyI comp : companies.values()){

            comp.getCOM().getBoard().paySalaries(comp);

            String countryName = comp.getCountry();
            Country country = CountryManager.getCountry(countryName);
            if (country == null){
                country = new CountryNull();
            }

            int total = 0;

            if (comp.getType().equals("Fighter")){

                if (!country.isAboardMilitary()){
                    total+= 4000;
                    comp.addBal(4000.0);
                }

                double val = country.getFunding("Fighter");
                if (val >= 0){
                    comp.addBal(val);
                }else{
                    comp.removeBal(val);
                }
                total += (int) val;

            }

            double val = country.getFunding(comp.getType());
            if (val >= 0){
                comp.addBal(val);
            }else{
                comp.removeBal(val);
            }
            total += (int) val;

            if (!comp.getCOM().isOpenTrade()){
                double value = country.getPolicyBonus( "closedMarket");
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
