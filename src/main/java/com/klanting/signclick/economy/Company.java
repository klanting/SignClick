package com.klanting.signclick.economy;

import com.google.gson.*;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.companyUpgrades.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.contractRequests.ContractRequest;
import com.klanting.signclick.economy.contractRequests.ContractRequestCTC;
import com.klanting.signclick.economy.logs.ContractChange;
import com.klanting.signclick.economy.logs.ContractPayment;
import com.klanting.signclick.economy.logs.MoneyTransfer;
import com.klanting.signclick.economy.logs.ShareholderChange;
import com.klanting.signclick.utils.JsonTools;
import com.klanting.signclick.utils.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.io.Console;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

public class Company extends LoggableSubject{

    private String name;
    private String stockName;

    public Research getResearch() {
        return research;
    }

    private Research research;


    public boolean hasPendingContractRequest() {
        return pendingContractRequest != null;
    }


    public ContractRequest getPendingContractRequest() {
        return pendingContractRequest;
    }

    private ContractRequest pendingContractRequest = null;


    public String playerNamePending = null;

    public double playerAmountPending = 0.0;
    public int playerWeeksPending = 0;
    public String playerReason = "no_reason";
    public ArrayList<Upgrade> upgrades = new ArrayList<>();

    public ArrayList<Patent> patent = new ArrayList<>();
    public ArrayList<PatentUpgrade> patentUpgrades = new ArrayList<>();
    public Integer patentCrafted = 0;

    public Country country;
    public String type;

    /*
     * Represents the share base value
     * */
    private double shareBase = 0.0;

    /*
     * Represents the amount of money in the company back account
     * */
    private double bal = 0.0;

    private double shareBalance = 0.0;

    private double spendable = 0.0;

    private double lastValue = 0.0;

    public List<Product> getProducts() {
        return products;
    }

    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product){
        products.add(product);
    }

    public CompanyOwnerManager getCOM() {
        /*
        * Short for Get(ter) CompanyOwnerManager
        * */
        return companyOwnerManager;
    }

    public double getSpendable() {
        return spendable;
    }

    public void setSpendable(double spendable){
        this.spendable = spendable;
    }

    private CompanyOwnerManager companyOwnerManager;

    public Company(@NotNull String n, String StockName, Account creater, double creationCost, String type){
        super();
        name = n;
        stockName = StockName;

        companyOwnerManager = new CompanyOwnerManager(creater.getUuid());

        addObserver(new ContractChange());
        addObserver(new ContractPayment());
        addObserver(new MoneyTransfer());
        addObserver(new ShareholderChange());

        creater.receivePrivate(stockName, getTotalShares());

        upgrades.add(new UpgradeExtraPoints(0));
        upgrades.add(new UpgradePatentSlot(0));
        upgrades.add(new UpgradePatentUpgradeSlot(0));
        upgrades.add(new UpgradeCraftLimit(0));
        upgrades.add(new UpgradeInvestReturnTime(0));
        this.type = type;

        lastValue = creationCost;

        research = new Research(type);

        addBal(creationCost);
    }



    public void setMarketShares(Integer marketShares) {
        getCOM().setMarketShares(marketShares);
    }

    public double getShareBalance() {
        return shareBalance;
    }

    public Integer getTotalShares() {
        return getCOM().getTotalShares();
    }

    public void setTotalShares(Integer totalShares) {
        getCOM().setTotalShares(totalShares);
    }

    public double getValue(){
        return getBal() + getShareBalance();
    }



    public double getShareBase() {
        return shareBase;
    }

    public double getBal() {
        return bal;
    }

    public boolean removeBal(double amount){
        return removeBal(amount, false);
    }

    public boolean removeBal(double amount, boolean skipSpendable){
        if ((getBal()+ shareBalance >= amount) && (spendable >= amount || skipSpendable)){
            this.bal -= amount;
            spendable -= amount;

            changeBase();
            return true;
        }
        return false;
    }

    public boolean addBalNoPoint(double bal) {
        this.bal += bal;
        changeBase();
        return true;
    }

    void addShareBal(Double amount){
        shareBalance += amount;
    }

    void removeShareBal(Double amount){
        shareBalance -= amount;
    }

    public void changeBase(){
        shareBase = (getBal()/getTotalShares()) / Market.calculateFluxChange(-10, 15);
    }

    public double stockCompareGet(){
        if (lastValue == 0){
            return 0.0;
        }

        return ((getValue()/ lastValue)-1)*100;

    }
    public double stockCompare(){
        double diff = stockCompareGet();
        lastValue = getValue();
        return diff;
    }

    public Integer getMarketShares() {
        return getCOM().getMarketShares();
    }

    private static final List<String> softLink = new ArrayList<>(List.of("country"));


    public Company(JsonObject jsonObject, JsonDeserializationContext context){
        /*
        * Load/create company from json file
        * */

        Field[] fields = Utils.getAllFields(this.getClass());
        for (Field field : fields) {

            try{
                String fieldName = field.getName();

                JsonElement element = jsonObject.get(fieldName);

                if (element == null){
                    field.set(this, null);
                    continue;
                }

                if (softLink.contains(fieldName)){
                    switch (fieldName){
                        case "country":
                            field.set(this, CountryManager.getCountry(element.getAsString()));
                    }

                    continue;
                }

                Object o;

                if (field.getType() == String.class){
                    o = element.getAsString();
                }else if (field.getType() == Integer.class){
                    o = element.getAsInt();
                }else if (field.getType() == Double.class){
                    o = element.getAsDouble();
                }else{
                    o = context.deserialize(element, field.getGenericType());
                }

                field.set(this, o);

            }catch (IllegalAccessException ignored){

            }

        }
    }

    public JsonObject toJson(JsonSerializationContext context){

        Function<JsonObject, JsonObject> method = (jsonObject) -> {
            if (country != null){
                jsonObject.addProperty("country", country.getName());
            }
            return jsonObject;
        };

        HashMap<String, Function<JsonObject, JsonObject>> map = new HashMap<>();
        map.put("country", method);

        Field[] fields = Utils.getAllFields(this.getClass());
        Map<String, Pair<Type, Object>> fieldMap = new HashMap<>();

        for (Field field : fields) {
            try{
                String fieldName = field.getName();
                Object fieldValue = field.get(this);
                fieldMap.put(fieldName, Pair.of(field.getGenericType(), fieldValue));

            }catch (IllegalAccessException ignored){
            }

        }

        return JsonTools.toJson(fieldMap, map, context);
    }


    public boolean addBal(double amount){
        bal += amount;
        changeBase();

        return true;
    }

    public void supportUpdate(Account holder, UUID uuid){
        getCOM().addSupport(holder.getUuid(), uuid);
        checkSupport();

    }

    public void checkSupport(){
        getCOM().checkOwnerSupport();
        calculateCountry();

    }


    public void getShareTop(Player player){
        getCOM().getShareTop(player);
    }


    void dividend(){
        double modifier1 = 0.0;
        double modifier2 = 0.0;
        if (country != null){
            modifier1 = country.getPolicyBonus(0, 1);
            modifier2 = country.getPolicyBonus(1, 1);
        }

        double value_one = (getValue()/getTotalShares().doubleValue())*(0.01- modifier1-modifier2);
        assert removeBal(value_one*(getTotalShares()-getMarketShares()), true);

        for (Entry<UUID, Integer> entry : getCOM().getShareHolders().entrySet()){
            UUID holder = entry.getKey();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(holder);
            int shares = entry.getValue();
            double payout = value_one*shares;
            SignClick.getEconomy().depositPlayer(offlinePlayer, payout);
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Market.getAccount(holder).sendPlayer("§byou got §7"+df.format(payout)+" §b from dividends in §7"+ stockName);

        }
    }

    public void info(Player player){
        DecimalFormat df = new DecimalFormat("###,###,###");
        ArrayList<String> name_array = new ArrayList<>();
        for(Entry<UUID, Integer> entry : getCOM().getShareHolders().entrySet()){
            UUID uuid = entry.getKey();
            name_array.add(Bukkit.getOfflinePlayer(uuid).getName());
        }

        UUID CEO = getCOM().getBoard().getChief("CEO");
        UUID CTO = getCOM().getBoard().getChief("CTO");
        UUID CFO = getCOM().getBoard().getChief("CFO");

        player.sendMessage("§bName: §7"+name+"\n" +
                "§bStockname: §7"+ stockName +"\n" +
                "§bCEO: §7"+ (CEO != null ? Bukkit.getOfflinePlayer(CEO).getName(): "NONE") +"\n" +
                "§bCTO: §7"+ (CTO != null ? Bukkit.getOfflinePlayer(CTO).getName(): "NONE") +"\n" +
                "§bCFO: §7"+ (CFO != null ? Bukkit.getOfflinePlayer(CFO).getName(): "NONE") +"\n" +
                "§bbal: §7"+df.format(getValue())+"\n" +
                "§bshares: §7"+df.format(getTotalShares())+"\n" +
                "§bshareholders: §7"+ name_array);

        name_array.clear();

    }

    public void acceptOfferCompContract(){
        if (pendingContractRequest == null){
            return;
        }

        pendingContractRequest.accept();

        pendingContractRequest = null;

    }

    public void sendOfferCompContract(String stock_name, double amount, int weeks, String reason){
        Market.getCompany(stock_name).receiveOfferCompContract(stockName, amount, weeks, reason);
    }

    public void receiveOfferCompContract(String stock_name, double amount, int weeks, String reason){

        pendingContractRequest = new ContractRequestCTC(this, Market.getCompany(stock_name), amount, weeks, reason);

        Bukkit.getServer().getScheduler().runTaskLater(SignClick.getPlugin(), new Runnable() {
            public void run() {

                pendingContractRequest = null;

            }
        }, 20*120L);

        getCOM().sendOwner("§b your company §7"+ stockName +"§b got a contract from §7" + stock_name
                + "§b they will ask you §7"+amount+"§b for §7"+weeks+"§b weeks, do §c/company sign_contract_ctc "+ stockName);
    }


    public void acceptOfferPlayerContract(){
        if (playerNamePending == null){
            return;
        }

        Market.setContractComptoPlayer(stockName, playerNamePending, playerAmountPending, playerWeeksPending, playerReason);

        playerNamePending = null;
        playerAmountPending = 0.0;
        playerWeeksPending = 0;
        playerReason = "no_reason";

    }

    //correct
    public void receiveOfferPlayerContract(String playerUUID, double amount, int weeks, String reason){
        playerNamePending = playerUUID;
        playerAmountPending = amount;
        playerWeeksPending = weeks;
        playerReason = reason;

        Bukkit.getServer().getScheduler().runTaskLater(SignClick.getPlugin(), new Runnable() {
            public void run() {
                playerNamePending = null;
                playerAmountPending = 0.0;
                playerWeeksPending = 0;
                playerReason = "no_reason";

            }
        }, 20*120L);

        getCOM().sendOwner("§b your company §7"+ stockName +"§b got a contract from §7" + Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName()
                + "§b he/she will ask you §7"+amount+"§b for §7"+weeks+"§b weeks, do §c/company sign_contract_ctp "+ stockName);
    }

    public void resetPatentCrafted(){
        patentCrafted = 0;
    }


    public boolean doUpgrade(Integer id){
        Upgrade u = upgrades.get(id);
        if (!u.canUpgrade((int) (getBal()+ getShareBalance()))){
            return false;

        }

        double base = 1.0;

        double modifier = 0.0;
        double modifier2 = 0.0;
        if (country != null){
            if (country.getStability() < 30){
                base += 0.05;
            }
            if (country.getStability() < 50){
                base += 0.15;
            }
            modifier += country.getPolicyBonus(1, 3);
            modifier2 += country.getPolicyBonus(3, 2);
        }

        int cost = (int) ((double) u.getUpgradeCost()*(base-modifier));
        removeBal(cost);
        u.DoUpgrade();

        int pct = upgrades.get(4).getBonus()+(int) (modifier2*100.0);
        double weeks = (10.0-(10.0*pct/100.0));
        double weekly_back = cost/weeks;
        Market.setContractServertoComp(this.stockName, weekly_back, (int) Math.floor(weeks), "Upgrade["+u.id+"] "+u.level, 0);
        if (Math.floor(weeks) < weeks){
            Market.setContractServertoComp(this.stockName, cost - (weekly_back*Math.floor(weeks)), 1, "Upgrade["+u.id+"] "+u.level, (int) Math.floor(weeks));
        }

        return true;
    }

    public void calculateCountry(){
        HashMap<String, Integer> country_top = new HashMap<>();

        Integer highest = -1;
        String linked_name = null;

        for (Entry<UUID, Integer> entry : getCOM().getShareHolders().entrySet()){

            if (CountryManager.getCountry(entry.getKey()) == null){
                continue;
            }

            String countryName = CountryManager.getCountry(entry.getKey()).getName();
            Integer amount = country_top.getOrDefault(countryName, 0);
            amount += entry.getValue();
            country_top.put(countryName, amount);

            if (highest < amount){
                highest = amount;
                linked_name = countryName;
            }
        }

        country = CountryManager.getCountry(linked_name);

    }

    public String getCountry(){
        if (country == null){
            return "none";
        }

        return country.getName();
    }

    public String getName() {
        return name;
    }

    public String getStockName() {
        return stockName;
    }
}
