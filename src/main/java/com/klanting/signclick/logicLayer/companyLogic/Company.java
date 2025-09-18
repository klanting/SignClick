package com.klanting.signclick.logicLayer.companyLogic;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.klanting.signclick.configs.ConfigManager;
import com.klanting.signclick.logicLayer.*;
import com.klanting.signclick.logicLayer.companyLogic.logs.*;
import com.klanting.signclick.logicLayer.companyLogic.patent.Patent;
import com.klanting.signclick.logicLayer.companyLogic.patent.PatentUpgrade;
import com.klanting.signclick.logicLayer.companyLogic.upgrades.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.contractRequests.ContractRequest;
import com.klanting.signclick.logicLayer.contractRequests.ContractRequestCTC;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.interactionLayer.events.MenuEvents;
import com.klanting.signclick.utils.BlockPosKey;
import com.klanting.signclick.utils.JsonTools;
import com.klanting.signclick.utils.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import versionCompatibility.CompatibleLayer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import static com.klanting.signclick.utils.Utils.AssertMet;

public class Company extends LoggableSubject implements CompanyI{

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

    public HashMap<BlockPosKey, Machine> getMachines() {
        return machines;
    }

    public final HashMap<BlockPosKey, Machine> machines = new HashMap<>();


    public String getPlayerNamePending() {
        return playerNamePending;
    }

    public String playerNamePending = null;

    public double getPlayerAmountPending() {
        return playerAmountPending;
    }

    public double playerAmountPending = 0.0;

    public int getPlayerWeeksPending() {
        return playerWeeksPending;
    }

    public int playerWeeksPending = 0;
    public String playerReason = "no_reason";

    public ArrayList<Upgrade> getUpgrades() {
        return upgrades;
    }

    public ArrayList<Upgrade> upgrades = new ArrayList<>();

    public ArrayList<Patent> getPatent() {
        return patent;
    }

    public ArrayList<Patent> patent = new ArrayList<>();

    public ArrayList<PatentUpgrade> getPatentUpgrades() {
        return patentUpgrades;
    }

    public ArrayList<PatentUpgrade> patentUpgrades = new ArrayList<>();

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country country;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product){
        if (!products.stream().filter(p -> p.getMaterial() == product.getMaterial()).findFirst().isPresent()){
            products.add(product);
        }

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
        addObserver(new ResearchUpdate());
        addObserver(new ShopLogs());
        addObserver(new MachineProduction());

        creater.receivePrivate(stockName, getTotalShares());

        upgrades.add(new UpgradePatentSlot(0));
        upgrades.add(new UpgradePatentUpgradeSlot(0));
        upgrades.add(new UpgradeProductSlot(0));
        upgrades.add(new UpgradeBoardSize(0, this));

        upgrades.add(new UpgradeInvestReturnTime(0));
        upgrades.add(new UpgradeResearchModifier(0));
        upgrades.add(new UpgradeProductModifier(0));

        this.type = type;

        lastValue = creationCost;

        research = new Research(this.getRef());

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

    public void reCalcBalance(){
        if(shareBalance < 0){
            this.bal += shareBalance;
            shareBalance = 0;
        }
    }

    public boolean removeBal(double amount, boolean skipSpendable){

        AssertMet(amount >= 0.0, "Company: remove balance must be positive");

        if ((getValue() >= amount) && (spendable >= amount || skipSpendable)){
            reCalcBalance();

            double shareBalPCT;
            double balPCT;
            if(bal == 0.0){
                shareBalPCT = 1.0;
                balPCT = 0.0;
            }else{
                double shareBalMultiplier = shareBalance/bal;
                shareBalPCT = shareBalMultiplier/(shareBalMultiplier+1);
                balPCT = 1/(shareBalMultiplier+1);
            }

            AssertMet(balPCT+shareBalPCT <= 1.0001, "removeBal: balPCT+shareBalPCT must be 100% together");

            this.shareBalance -= amount*shareBalPCT;
            this.bal -= amount*balPCT;

            AssertMet(shareBalance >= 0, "Share balance must be positive afterwards");
            AssertMet(this.bal >=0, "Balance must always be positive");

            spendable -= amount;

            changeBase();
            return true;
        }

        return false;
    }

    public void removeBalVar(double amount){
        AssertMet(amount >= 0.0, "Company: remove balance must be positive");
        reCalcBalance();

        this.bal -= amount;

        AssertMet(this.bal >=0, "Balance must always be positive");

        spendable -= amount;
        changeBase();

    }

    public boolean addBalNoPoint(double bal) {
        this.bal += bal;
        changeBase();
        return true;
    }

    public void addShareBal(Double amount){
        shareBalance += amount;
    }

    public void removeShareBal(Double amount){
        shareBalance -= amount;
    }

    public void changeBase(){
        int startShares = SignClick.getConfigManager().getConfig("companies.yml").getInt("companyStartShares");

        AssertMet(startShares >= 0, "The amount of start shares needs to be positive");
        AssertMet(getBal() >= 0, "The balance needs to be positive");

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

    private static transient final List<String> softLink = new ArrayList<>(List.of("country", "machines", "upgrades", "research"));


    public Company(JsonObject jsonObject, JsonDeserializationContext context, ConfigManager configManager){
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
                            break;
                        case "machines":
                            Set<Entry<String, JsonElement>> entries = element.getAsJsonObject().entrySet();
                            for(Entry<String, JsonElement> entry: entries){
                                BlockPosKey blockPos = context.deserialize(JsonParser.parseString(entry.getKey()), new TypeToken<BlockPosKey>(){}.getType());
                                Machine machine = context.deserialize(entry.getValue(), new TypeToken<Machine>(){}.getType());

                                machines.put(blockPos, machine);
                            }

                            for (Machine machine: machines.values()){
                                if (machine.hasProduct()){
                                    MenuEvents.activeMachines.add(machine);
                                }
                            }

                            break;
                        case "upgrades":
                            upgrades = context.deserialize(element, new TypeToken<List<Upgrade>>(){}.getType());
                            for (Upgrade upgrade: upgrades){
                                if (upgrade instanceof UpgradeBoardSize bs){
                                    bs.comp = this.getRef();
                                }
                            }
                            break;
                        case "research":
                            research = context.deserialize(element, new TypeToken<Research>(){}.getType());
                            research.company = this;
                            break;
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

        /*
         * set right research tick
         * */
        if (research != null){
            research.setLastChecked(CompatibleLayer.getCurrentTick());
            research.loadMaterials(configManager);
        }
    }

    public JsonObject toJson(JsonSerializationContext context){

        /*
        * Check Research
        * */
        if (research != null){
            research.checkProgress();
        }


        Function<JsonObject, JsonObject> method = (jsonObject) -> {
            if (country != null){
                jsonObject.addProperty("country", country.getName());
            }
            return jsonObject;
        };

        Function<JsonObject, JsonObject> method2 = (jsonObject) -> {
            JsonObject machinesJson = new JsonObject();
            for (Map.Entry<BlockPosKey, Machine> entry: machines.entrySet()){
                machinesJson.add(context.serialize(entry.getKey()).toString(),
                        context.serialize(entry.getValue())
                        );

                jsonObject.add("machines", machinesJson);
            }
            return jsonObject;
        };

        Function<JsonObject, JsonObject> method3 = (jsonObject) -> {
            jsonObject.add("upgrades", context.serialize(upgrades, new TypeToken<List<Upgrade>>(){}.getType()));
            return jsonObject;
        };

        HashMap<String, Function<JsonObject, JsonObject>> map = new HashMap<>();
        map.put("country", method);
        map.put("machines", method2);
        map.put("upgrades", method3);

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

        AssertMet(amount >= 0.0, "Company: add balance must be positive");

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


    public void dividend(){
        double modifier1 = 0.0;
        if (country != null){
            modifier1 = country.getPolicyBonus("dividendReduction");
        }

        AssertMet((0.01- modifier1) >= 0, "Dividends payout % must be positive");

        double value_one = (getValue()/getTotalShares().doubleValue())*(0.01- modifier1);
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

    public double getUpgradeModifier(){
        double base = 1.0;

        double modifier = 0.0;
        if (country != null){
            if (country.getStability() < 30){
                base += 0.05;
            }
            if (country.getStability() < 50){
                base += 0.15;
            }
            modifier += country.getPolicyBonus("upgradeDiscount");
        }

        return base-modifier;

    }


    public boolean doUpgrade(Integer id){
        Upgrade u = upgrades.get(id);
        if (!u.canUpgrade((int) getValue())){
            return false;

        }

        double modifier2 = 0.0;
        if (country != null){
            modifier2 += country.getPolicyBonus("UpgradeReturnTimeReduction");
        }

        int cost = (int) ((double) u.getUpgradeCost()*getUpgradeModifier());

        boolean suc6 = removeBal(cost);
        if (!suc6){
            return false;
        }
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

    public CompanyI getRef(){
        return new CompanyRef(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CompanyI companyI){
            return companyI.getStockName().equals(getStockName());
        }
        return false;
    }
}
