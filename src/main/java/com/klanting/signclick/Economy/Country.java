package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.Decisions.Decision;
import com.klanting.signclick.Economy.Decisions.DecisionPolicy;
import com.klanting.signclick.Economy.Parties.Election;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.Economy.Policies.*;
import com.klanting.signclick.SignClick;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class Country {

    private String name;

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
    }


    public Country(String name, int balance,
                   List<UUID> owners, List<UUID> members,
                   List<UUID> lawEnforcement,
                   List<Policy> policies, List<Party> parties,
                   List<Decision> decisions, Election countryElection,
                   ChatColor memberColor, Location spawnLocation,
                   double taxRate, double stability,
                   boolean forbidParty, boolean aboardMilitary){
        /*
         * File loading constructor
         * */
        this.name = name;
        this.balance = balance;
        this.owners = owners;
        this.members = members;
        this.lawEnforcement = lawEnforcement;
        this.policies = policies;
        this.parties = parties;
        this.decisions = decisions;
        this.countryElection = countryElection;
        this.memberColor = memberColor;
        this.spawnLocation = spawnLocation;
        this.taxRate = taxRate;
        this.stability = stability;
        this.forbidParty = forbidParty;
        this.aboardMilitary = aboardMilitary;

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
        memberColor = color;
    }

    public Location getSpawn(){
        return spawnLocation;
    }

    public void setSpawn(Location location){
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

            addStability(3.0*(1.0+ getPolicyBonus(2, 9)));
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
        addStability(-3.0*(1.0- getPolicyBonus(2, 10)));
    }

    public Double addStability(double change){
        stability += change;
        return stability;
    }

    public double getPolicyBonus(int id, int index){

        return policies.get(id).getBonus(index);
    }

    public void save(){

        SignClick.getPlugin().getConfig().createSection("election");
        SignClick.getPlugin().getConfig().createSection("forbid_party."+name);
        SignClick.getPlugin().getConfig().createSection("aboard_military."+name);
        SignClick.getPlugin().getConfig().createSection("parties."+name);
        SignClick.getPlugin().getConfig().createSection("decision."+name);
        SignClick.getPlugin().getConfig().createSection("spawn."+name);
        SignClick.getPlugin().getConfig().createSection("members."+name);

        /*
        * Save balance
        * */
        SignClick.getPlugin().getConfig().set("bank." + name, balance);

        /*
        * Save owner
        * */
        List<String> f_list = new ArrayList<>();
        for (UUID uuid: owners){
            f_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set("owners." + name, f_list);

        /*
        * Save members
        * */
        f_list = new ArrayList<>();
        for (UUID uuid: members){
            f_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set("members." + name, f_list);

        SignClick.getPlugin().getConfig().set("pct." + name, taxRate);
        SignClick.getPlugin().getConfig().set("color." + name, memberColor.name());
        SignClick.getPlugin().getConfig().set("spawn." + name, spawnLocation);

        /*
        * add law enforcement
        * */
        f_list = new ArrayList<>();
        for (UUID uuid: lawEnforcement){
            f_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set("law_enforcement." + name, f_list);
        SignClick.getPlugin().getConfig().set("stability_map." + name, stability);

        for (Policy p: policies){
            p.Save(name);
        }

        for (Party p: parties){
            p.Save();
        }

        if (countryElection != null){
            countryElection.Save();
        }

        /*
        * save decisions
        * */
        int counter = 0;
        for (Decision d: decisions){
            d.Save(counter);
            counter++;
        }

        SignClick.getPlugin().getConfig().set("forbid_party." + name, forbidParty);
        SignClick.getPlugin().getConfig().set("aboard_military." + name, aboardMilitary);


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
        policies.get(id).level = level;

        if (id == 0){
            Double old_stab = policies.get(id).getBonusLevel(5, old_level);
            Double new_stab = policies.get(id).getBonusLevel(5, level);
            double change = new_stab-old_stab;
            stability += change;
        }

        if (id == 2){

            Double old_stab = policies.get(id).getBonusLevel(1, old_level);
            Double new_stab = policies.get(id).getBonusLevel(1, level);
            double change = new_stab-old_stab;
            stability += change;

            if (old_level == 4){
                forbidParty = false;
            }

        }

        if (id == 4){
            Double old_stab = policies.get(id).getBonusLevel(7, old_level);
            Double new_stab = policies.get(id).getBonusLevel(7, level);
            double change = new_stab-old_stab;
            stability += change;

        }

    }

    public boolean setPolicies(int id, int level){
        int old_level = policies.get(id).level;
        if (old_level == level){
            return false;
        }

        if (id == 0 || id == 3){
            int gov_cap = getPolicyRequire(id, 0, level);
            if (gov_cap > getBalance()){
                return false;
            }
        }

        if (id == 2 || id == 4){
            int gov_cap = getPolicyRequire(id, 1, level);
            if (gov_cap > getBalance()){
                return false;
            }

            int law_enfo = getPolicyRequire(id, 0, level);
            if (law_enfo > lawEnforcement.size()){
                return false;
            }
        }

        if (id == 4){
            int tax_rate = getPolicyRequire(id, 2, level);
            if (level < 2 && tax_rate > taxRate){
                return false;
            }

            if (level > 2 && tax_rate < taxRate){
                return false;
            }
        }

        double change = 0.5;
        if (level == 0 || level == 4){
            change = 0.7;
        }

        Decision d = new DecisionPolicy("§6Policy §9"+policies.get(id).titles.get(old_level)+
                "§6 to §9"+policies.get(id).titles.get(level), change, this.name, id, old_level, level);

        if (hasDecisionName(d.name)){
            return false;
        }

        decisions.add(d);
        return true;
    }

    public int getPolicyRequire(int id, int index, int level){
        return policies.get(id).getRequireLevel(index, level);
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


        DecimalFormat df = new DecimalFormat("###,###,###");
        player.sendMessage("§bBank: §7"+name+"\n" +
                "§bbalance: §7"+df.format(amount)+"\n" +
                "§bowners: §7"+p_list+"\n" +
                "§bmembers: §7"+m_list+"\n" +
                "§blaw enforcement: §7"+l_list+"\n" +
                "§btaxrate: §7"+ taxRate*100+"\n" +
                "§bstability: §7"+ df.format(getStability())+"\n" +
                "§bspawn: §7"+ spawnString);

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
