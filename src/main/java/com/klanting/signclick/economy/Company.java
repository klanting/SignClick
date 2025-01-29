package com.klanting.signclick.economy;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.companyUpgrades.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.contractRequests.ContractRequest;
import com.klanting.signclick.economy.contractRequests.ContractRequestCTC;
import com.klanting.signclick.utils.JsonTools;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public class Company extends Stock{

    private String name;
    private String stockName;

    private ArrayList<UUID> owners = new ArrayList<>();

    public ArrayList<UUID> getOwners() {
        return owners;
    }

    public void testAddOwner(UUID uuid){
        /*
        * Test method to inject an owner
        * */
        owners.add(uuid);
    }

    public Boolean openTrade = false;



    public Map<UUID, UUID> support = new HashMap<>();
    public Map<UUID, Integer> shareHolders = new HashMap<>();

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

    public Integer marketShares = 0;

    public Integer getMarketShares() {
        return marketShares;
    }


    public Company(String n, String StockName, Account creater){
        super();
        name = n;
        stockName = StockName;

        support.put(creater.getUuid(), creater.getUuid());
        shareHolders.put(creater.getUuid(), getTotalShares());
        creater.receivePrivate(stockName, getTotalShares());

        upgrades.add(new UpgradeExtraPoints(0));
        upgrades.add(new UpgradePatentSlot(0));
        upgrades.add(new UpgradePatentUpgradeSlot(0));
        upgrades.add(new UpgradeCraftLimit(0));
        upgrades.add(new UpgradeInvestReturnTime(0));
        type = "other";
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

        BiFunction<String, JsonObject, JsonObject> method = (fieldName, jsonObject) -> {
            switch (fieldName){
            case "country":
                if (country != null){
                    jsonObject.addProperty(fieldName, country.getName());
                }
            }
            return jsonObject;
        };

        HashMap<String, BiFunction<String, JsonObject, JsonObject>> map = new HashMap<>();
        map.put("country", method);

        Field[] fields = Utils.getAllFields(this.getClass());
        Map<String, Object> fieldMap = new HashMap<>();

        for (Field field : fields) {
            try{
                String fieldName = field.getName();
                Object fieldValue = field.get(this);
                fieldMap.put(fieldName, fieldValue);

            }catch (IllegalAccessException ignored){
            }

        }

        return JsonTools.toJson(fieldMap, map, context);
    }

    public Company(String n, String StockName){
        name = n;
        stockName = StockName;

        setLastValue(getValue());

        upgrades.add(new UpgradeExtraPoints(0));
        upgrades.add(new UpgradePatentSlot(0));
        upgrades.add(new UpgradePatentUpgradeSlot(0));
        upgrades.add(new UpgradeCraftLimit(0));
        upgrades.add(new UpgradeInvestReturnTime(0));

    }

    @Override
    public boolean addBal(double amount){
        //TODO replace

        double modifier = 0.0;
        if (country != null){
            modifier += country.getPolicyBonus(0, 3);
        }

        double modifier2 = 0.0;
        double sub_pct = 1.0;
        if (country != null){
            if (country.getStability() < 30){
                sub_pct -= 0.20;
            }
            if (country.getStability() < 50){
                sub_pct -= 0.10;
            }
            if (country.getStability() > 80){
                sub_pct += 0.10;
            }
            modifier2 += country.getPolicyBonus(0, 2);
        }

        double modifier3 = (sub_pct+(double) upgrades.get(0).getBonus()/100.0);

        boolean suc6 = super.addBal(amount);

        if (amount > 0){
            spendable += (0.2+ modifier)*amount;
        }
        securityFunds += (0.01*amount)*modifier3*(1.0+ modifier2);

        return suc6;
    }

    public void changeShareHolder(Account holder, Integer amount){
        if (shareHolders.getOrDefault(holder.getUuid(), null) != null){
            Integer am = shareHolders.get(holder.getUuid());
            shareHolders.put(holder.getUuid(), am+amount);

        }else {
            shareHolders.put(holder.getUuid(), amount);
            support.put(holder.getUuid(), null);
        }

        if (shareHolders.getOrDefault(holder.getUuid(), 0) == 0){
            shareHolders.remove(holder.getUuid());
            support.remove(holder.getUuid());
        }
    }

    public void supportUpdate(Account holder, UUID uuid){
        support.put(holder.getUuid(), uuid);
        checkSupport();
        calculateCountry();
    }

    public void checkSupport(){
        double neutral = 0.0;

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

        neutral = neutral/getTotalShares().doubleValue();
        ArrayList<UUID> new_owners = new ArrayList<UUID>();
        for(Entry<UUID, Integer> entry : s_dict.entrySet()){
            UUID k = entry.getKey();
            double v = entry.getValue();

            v = v/getTotalShares().doubleValue();

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

    public Boolean isOwner(UUID uuid){
        return owners.contains(uuid);
    }

    public void getShareTop(Player player){
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

        DecimalFormat df = new DecimalFormat("###,###,###");
        DecimalFormat df2 = new DecimalFormat("0.00");
        for (int i = 0; i < values.size(); i++) {
            player.sendMessage("§9"+Bukkit.getOfflinePlayer(order.get(i)).getName()+": §f"+df.format(values.get(i))+
                    " ("+df2.format(values.get(i)/getTotalShares().doubleValue()*100.0)+"%)");
        }

        if (openTrade){
            player.sendMessage("§eMarket: §f"+"inf"+" ("+"inf"+"%)");
        }else{
            player.sendMessage("§eMarket: §f"+df.format(marketShares)+" ("+df2.format(marketShares/getTotalShares().doubleValue()*100.0)+"%)");
        }

        order.clear();
        values.clear();
    }


    void dividend(){
        double modifier1 = 0.0;
        double modifier2 = 0.0;
        if (country != null){
            modifier1 = country.getPolicyBonus(0, 1);
            modifier2 = country.getPolicyBonus(1, 1);
        }


        double value_one = (getValue()/getTotalShares().doubleValue())*(0.01- modifier1-modifier2);
        removeBal(value_one*(getTotalShares()-marketShares));
        for (Entry<UUID, Integer> entry : shareHolders.entrySet()){
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
                "§bStockname: §7"+ stockName +"\n" +
                "§bCEO: §7"+owner_array+"\n" +
                "§bbal: §7"+df.format(getValue())+"\n" +
                "§bshares: §7"+df.format(getTotalShares())+"\n" +
                "§bshareholders: §7"+ name_array);

        name_array.clear();
        owner_array.clear();

    }

    public void sendOwner(String message){
        for (int i = 0; i < owners.size(); i++){
            Player p = Bukkit.getPlayer(owners.get(i));
            if (p != null){
                p.sendMessage(message);
            }

        }
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

        sendOwner("§b your company §7"+ stockName +"§b got a contract from §7" + stock_name
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

        sendOwner("§b your company §7"+ stockName +"§b got a contract from §7" + Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName()
                + "§b he/she will ask you §7"+amount+"§b for §7"+weeks+"§b weeks, do §c/company sign_contract_ctp "+ stockName);
    }



    public void resetSpendable(){
        double base = 0.2;

        if (country == null || country.getStability() < 50){
            base -= 0.03;
        }

        double modifier = 0.0;
        if (country != null){
            modifier = country.getPolicyBonus(0, 3);
        }

        double pct = (base+modifier);
        if (type.equals("bank") && country != null){
            pct += country.getPolicyBonus(0, 7);
            pct += country.getPolicyBonus(1, 5);
            pct += country.getPolicyBonus(2, 11);
        }

        setSpendable(getValue()*pct);
    }

    public void resetPatentCrafted(){
        patentCrafted = 0;
    }


    public double stockCompareGet(){

        if (getLastValue() == 0){
            return 0.0;
        }

        return ((getValue()/ getLastValue())-1)*100;

    }
    public double stockCompare(){
        double diff = stockCompareGet();
        setLastValue(getValue());
        return diff;
    }

    public void doUpgrade(Integer id){
        Upgrade u = upgrades.get(id);
        if (u.canUpgrade((int) (getBal()+ getShareBalance()),
                (int) getSecurityFunds())){
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

            setSecurityFunds(getSecurityFunds() - u.getUpgradeCostPoints()*(base-modifier));
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

        }
    }

    public void calculateCountry(){
        HashMap<String, Integer> country_top = new HashMap<String, Integer>();

        Integer highest = -1;
        String linked_name = null;

        for (Entry<UUID, Integer> entry : shareHolders.entrySet()){

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
