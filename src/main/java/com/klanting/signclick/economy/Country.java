package com.klanting.signclick.economy;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.decisions.DecisionPolicy;
import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.economy.policies.*;
import com.klanting.signclick.utils.JsonTools;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.dynmap.markers.MarkerIcon;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.*;

import static com.klanting.signclick.SignClick.getDynmap;
import static com.klanting.signclick.SignClick.markerSet;
import static com.klanting.signclick.events.CountryEvents.sortTab;
import static org.bukkit.Bukkit.getServer;

public class Country {

    /*
    * Name of the country
    * */
    private String name;

    /*
    * balance of the country its bank account
    * */
    private int balance;

    private List<UUID> owners = new ArrayList<>();
    private List<UUID> members = new ArrayList<>();

    private List<UUID> lawEnforcement = new ArrayList<>();

    private List<Policy> policies = new ArrayList<>();
    private List<Party> parties = new ArrayList<>();
    private List<Decision> decisions = new ArrayList<>();

    private Election countryElection;

    private ChatColor memberColor;

    private Location spawnLocation;

    /*
    * value between 0 and 1
    * */
    private double taxRate;

    private double stability;

    private boolean forbidParty;
    private boolean aboardMilitary;

    public Country(String countryName, OfflinePlayer player){
        name = countryName;
        owners.add(player.getUniqueId());

        taxRate = 0;
        stability = 70.0;
        balance = 0;
        memberColor = ChatColor.WHITE;
        spawnLocation = null;
        countryElection = null;

        forbidParty = false;
        aboardMilitary = false;

        policies = Arrays.asList(new PolicyEconomics(2), new PolicyMarket(2), new PolicyMilitary(2), new PolicyTourist(2), new PolicyTaxation(2));

        Party party = new Party("Government", name, player.getUniqueId());
        parties.add(party);
        party.PCT = 1.0;
    }

    public Country(JsonObject jsonObject, JsonDeserializationContext context){
        /*
         * Load/create company from json file
         * */

        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {

            try{
                String fieldName = field.getName();

                JsonElement element = jsonObject.get(fieldName);

                if (element == null){
                    field.set(this, null);
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

        Field[] fields = this.getClass().getDeclaredFields();
        Map<String, Pair<Type, Object>> fieldMap = new HashMap<>();

        for (Field field : fields) {
            try{
                String fieldName = field.getName();
                Object fieldValue = field.get(this);

                fieldMap.put(fieldName, Pair.of(field.getGenericType(), fieldValue));

            }catch (IllegalAccessException ignored){
            }

        }

        return JsonTools.toJson(fieldMap, new HashMap<>(), context);
    }

    public Country(){
        name = null;
    }


    public String getName() {
        /*
        * getter to retrieve the name of a country
        * */
        return name;
    }

    public List<UUID> getLawEnforcement() {
        return lawEnforcement;
    }

    public boolean isOwner(OfflinePlayer offlinePlayer){
        return owners.contains(offlinePlayer.getUniqueId());
    }

    public List<UUID> getOwners(){
        return owners;
    }

    public List<UUID> getMembers(){
        return members;
    }

    public boolean has (int amount){
        return amount <= balance;
    }

    public int getBalance(){
        return balance;
    }

    public void deposit(int amount){
        int oldBalance = balance;
        balance += amount;
        changeCapital(oldBalance);
    }

    public boolean withdraw(int amount){

        if (amount > balance){
            return false;
        }

        int oldBalance = balance;
        balance -= amount;
        changeCapital(oldBalance);
        return true;
    }

    private void changeCapital(int oldCap){
        Map<Integer, Double> d = new HashMap<>();

        d.put(5000000, 2.0);
        d.put(20000000, 2.0);
        d.put(40000000, 1.0);
        d.put(60000000, 2.0);
        d.put(80000000, 1.0);
        d.put(100000000, 2.0);

        for (Map.Entry<Integer, Double> entry: d.entrySet()){
            if (!(Math.min(oldCap, balance) < entry.getKey() && entry.getKey() < Math.max(oldCap, balance))){
                continue;
            }

            boolean grow = balance-oldCap > 0;

            if (grow){
                addStability(entry.getValue());
            }else{
                addStability(-entry.getValue());
            }
        }
    }

    public ChatColor getColor(){
        return memberColor;
    }

    public void setColor(ChatColor color){
        /*
        * Change the tab color of players belonging to this country
        * */
        memberColor = color;

        for (Player player: Bukkit.getOnlinePlayers()){
            if (owners.contains(player.getUniqueId()) || members.contains(player.getUniqueId())){
                player.setPlayerListName(memberColor+player.getName());
            }
        }
        sortTab();

    }

    public Location getSpawn(){
        return spawnLocation;
    }

    public void setSpawn(Location location){

        if (SignClick.dynmapSupport && markerSet != null){
            MarkerIcon icon = getDynmap().getMarkerAPI().getMarkerIcon("house");

            if (markerSet.findMarker(name) != null){
                markerSet.findMarker(name).deleteMarker();
            }

            markerSet.createMarker(
                    name,
                    name+" Country Spawn",
                    location.getWorld().getName(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    icon,
                    true
            );
        }

        spawnLocation = location;
    }

    public void removeOwner(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        removeOwner(uuid);
    }

    public void removeOwner(UUID uuid){
        if (!owners.contains(uuid)){
            return;
        }

        owners.remove(uuid);
        CountryManager.leaveCountry(uuid);
    }

    public void addMember(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();

        addMember(uuid);

    }

    public void addMember(UUID uuid){

        if (!members.contains(uuid) || !owners.contains(uuid)) {
            members.add(uuid);

            CountryManager.joinCountry(this, uuid);

            addStability(3.0*(1.0+ getPolicyBonus("joinPlayerBonus")));
        }

    }

    public boolean addOwner(OfflinePlayer offlinePlayer){

        UUID uuid = offlinePlayer.getUniqueId();
        return addOwner(uuid);

    }

    public boolean addOwner(UUID uuid){

        if (owners.contains(uuid)){
            return false;
        }

        CountryManager.joinCountry(this, uuid);

        owners.add(uuid);
        return true;

    }

    public void addLawEnforcement(Player player){

        if (!lawEnforcement.contains(player.getUniqueId()) || !owners.contains(player.getUniqueId())) {
            lawEnforcement.add(player.getUniqueId());
        }
    }

    public void setTaxRate(double rate){
        taxRate = rate;
    }

    public void removeMember(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        removeMember(uuid);
    }

    public void removeMember(UUID uuid){
        if (!members.contains(uuid)) {
            return;
        }

        members.remove(uuid);

        CountryManager.leaveCountry(uuid);
        addStability(-3.0*(1.0- getPolicyBonus("removePlayerPenalty")));
    }

    public Double addStability(double change){
        stability += change;
        return stability;
    }

    public double getPolicyBonus(String s){

        double total = 0.0;
        for(Policy policy: policies){
            total += policy.getBonus(s);
        }

        return total;
    }

    public double getFunding(String type){
        double val = 0.0;
        for(Policy p: policies){
            val += p.getFunding(type);
        }

        return val;
    }


    public double getStability() {
        return stability;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public Election getCountryElection() {
        return countryElection;
    }

    public List<Party> getParties() {
        return parties;
    }

    public Party getRuling(){
        /*
         * First Ruling party is automatic the highest, when no elections occurred
         * */
        double highest_pct = -0.1;
        Party highest_party = null;


        for (Party p: parties){

            if (p.PCT > highest_pct){
                highest_pct = p.PCT;
                highest_party = p;
            }
        }

        return highest_party;
    }

    public void setPoliciesReal(int id, int old_level, int level){
        policies.get(id).setLevel(level);

        if (id == 0){
            Double old_stab = policies.get(id).getBonusLevel("stabilityModifier", old_level);
            Double new_stab = policies.get(id).getBonusLevel("stabilityModifier", level);
            double change = new_stab-old_stab;
            stability += change;
        }

        if (id == 2){

            Double old_stab = policies.get(id).getBonusLevel("stabilityModifier", old_level);
            Double new_stab = policies.get(id).getBonusLevel("stabilityModifier", level);
            double change = new_stab-old_stab;
            stability += change;

            if (old_level == 4){
                forbidParty = false;
            }

        }

        if (id == 4){
            Double old_stab = policies.get(id).getBonusLevel("stabilityModifier", old_level);
            Double new_stab = policies.get(id).getBonusLevel("stabilityModifier", level);
            double change = new_stab-old_stab;
            stability += change;

        }

    }

    public boolean setPolicies(int id, int level){
        int old_level = policies.get(id).getLevel();
        if (old_level == level){
            return false;
        }

        if (id == 0 || id == 3){
            int gov_cap = getPolicyRequire(id, "capital", level);
            if (gov_cap > getBalance() && (level == 0 || level == 4)){
                return false;
            }
        }

        if (id == 2 || id == 4){
            int gov_cap = getPolicyRequire(id, "capital", level);
            if (gov_cap > getBalance()){
                return false;
            }

            int law_enfo = getPolicyRequire(id, "lawEnforcement", level);
            if (law_enfo > lawEnforcement.size()){
                return false;
            }
        }

        if (id == 4){
            int min_tax_rate = getPolicyRequire(id, "minTaxRate", level);
            int max_tax_rate = getPolicyRequire(id, "maxTaxRate", level);

            if(taxRate < min_tax_rate){
                return false;
            }

            if(taxRate > max_tax_rate){
                return false;
            }
        }

        double change = 0.5;
        if (level == 0 || level == 4){
            change = 0.7;
        }


        Decision d = new DecisionPolicy("§6Policy §9"+policies.get(id).getTitle(old_level)+
                "§6 to §9"+policies.get(id).getTitle(level), change, this.name, id, old_level, level);

        if (hasDecisionName(d.name)){
            return false;
        }

        decisions.add(d);
        return true;
    }

    public int getPolicyRequire(int id, String s, int level){
        return policies.get(id).getRequireLevel(s, level);
    }

    public void createParty(String name, UUID owner){
        Party p = new Party(name, this.name, owner);
        parties.add(p);
    }

    public Boolean hasDecisionName(String name){
        for (Decision d : decisions){
            if (d.name.equals(name)){
                return true;
            }
        }

        return false;
    }

    public List<Policy> getPolicies(){
        return policies;
    }

    public Party getParty(UUID uuid){
        for (Party p : parties){
            if (p.inParty(uuid)){
                return p;
            }
        }

        return null;
    }

    public Party getParty(String name){
        for (Party p : parties){
            if (p.name.equals(name)){
                return p;
            }
        }

        return null;
    }

    public List<Decision> getDecisions() {
        return decisions;
    }

    public boolean isForbidParty() {
        return forbidParty;
    }

    public boolean isAboardMilitary() {
        return aboardMilitary;
    }

    public void addDecision(Decision d){
        decisions.add(d);
    }
    public void removeDecision(Decision d){
        decisions.remove(d);
    }

    public void info(Player player){
        int amount = balance;
        List<UUID> player_list = owners;
        List<UUID> member_list = members;
        List<UUID> law_list = lawEnforcement;

        List<String> p_list = new ArrayList<String>();
        for (UUID p: player_list){
            p_list.add(Bukkit.getOfflinePlayer(p).getName());
        }

        List<String> m_list = new ArrayList<String>();
        if (member_list != null){
            for (UUID p: member_list){
                m_list.add(Bukkit.getOfflinePlayer(p).getName());
            }
        }

        List<String> l_list = new ArrayList<String>();
        if (law_list != null){
            for (UUID p: law_list){
                l_list.add(Bukkit.getOfflinePlayer(p).getName());
            }
        }
        Location spawn = spawnLocation;
        String spawnString;
        if (spawn != null){
            spawnString = "\nX: "+spawn.getX()+ "\nY: "+spawn.getY()+ "\nZ: "+spawn.getZ();
        }else{
            spawnString = "No spawn has been set use '/country setspawn' to set a country spawn location";
        }

        List<String> pa_list = new ArrayList<>();
        for (Party p: parties){
            pa_list.add(p.name);
        }


        DecimalFormat df = new DecimalFormat("###,###,###");
        player.sendMessage("§bCountry name: §7"+name+"\n" +
                "§bbalance: §7"+df.format(amount)+"\n" +
                "§bowners: §7"+p_list+"\n" +
                "§bmembers: §7"+m_list+"\n" +
                "§blaw enforcement: §7"+l_list+"\n" +
                "§btaxrate: §7"+ taxRate*100+"\n" +
                "§bstability: §7"+ df.format(getStability())+"\n" +
                "§bspawn: §7"+ spawnString+"\n"+
                "§bparties: §7"+ pa_list);

    }

    public void removeLawEnforcement(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        lawEnforcement.remove(uuid);
    }


    public void setCountryElection(Election countryElection) {
        this.countryElection = countryElection;
    }

    public Boolean inParty(UUID uuid){
        for (Party p : parties){
            if (p.inParty(uuid)){
                return true;
            }
        }
        return false;
    }

    public Boolean hasPartyName(String name){
        for (Party p : parties){
            if (p.name.equals(name)){
                return true;
            }
        }

        return false;
    }

    public void setForbidParty(boolean forbidParty) {
        this.forbidParty = forbidParty;
    }

    public void setAboardMilitary(boolean aboardMilitary) {
        this.aboardMilitary = aboardMilitary;
    }

    public void removeParty(Party p){
        Party ph = getRuling();

        ph.PCT += p.PCT;

        parties.remove(p);

        addStability(-10.0);
    }
}
