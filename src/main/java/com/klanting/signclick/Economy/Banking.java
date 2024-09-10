package com.klanting.signclick.Economy;

import com.klanting.signclick.Economy.Decisions.*;
import com.klanting.signclick.Economy.Parties.Election;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.Economy.Policies.*;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.BankCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

import static com.klanting.signclick.commands.BankCommands.CountryElections;
import static org.bukkit.Bukkit.getServer;

public class Banking {
    public static Map<String, Integer> banks = new HashMap<String, Integer>();
    public static Map<String, List<UUID>> owners = new HashMap<String, List<UUID>>();
    public static Map<String, List<UUID>> members = new HashMap<String, List<UUID>>();
    private static Map<UUID, String> Country = new HashMap<UUID, String>();
    private static Map<String, Integer> PCT = new HashMap<String, Integer>();
    private static Map<String, String> color_map = new HashMap<String, String>();
    private static Map<String, Location> spawn_loc = new HashMap<String, Location>();

    private static Map<String, List<UUID>> law_enforcement = new HashMap<String, List<UUID>>();

    private static Map<String, Double> stabilityMap = new HashMap<String, Double>();

    private static Map<String, List<Policy>> policies = new HashMap<String, List<Policy>>();

    public static Map<String, List<Party>> parties = new HashMap<String, List<Party>>();

    public static Map<String, List<Decision>> decisions = new HashMap<String, List<Decision>>();

    public static Map<String, Boolean> forbid_party = new HashMap<String, Boolean>();

    public static Map<String, Boolean> aboard_military = new HashMap<String, Boolean>();

    public static Double getStability(String s){
        if (!stabilityMap.containsKey(s)){
            stabilityMap.put(s, 70.0);
        }
        return stabilityMap.getOrDefault(s, 70.0);
    }

    public static void clear(){
        banks.clear();
        owners.clear();
        members.clear();
        Country.clear();
        PCT.clear();
        color_map.clear();
        spawn_loc.clear();
        law_enforcement.clear();
        stabilityMap.clear();
        policies.clear();
        parties.clear();
        decisions.clear();
        forbid_party.clear();
        aboard_military.clear();
    }

    public static int countryCount(){
        return banks.size();
    }

    public static Double add_stability(String s, Double change){
        return stabilityMap.put(s, stabilityMap.get(s)+change);
    }

    public static void create(String s, Player player){

        if (!player.hasPermission("signclick.staff")){
            player.sendMessage("§bplayer does not have permission to create a country");
            return;
        }

        if (!banks.containsKey(s)){
            banks.put(s, 0);

            List<UUID> owner_list = new ArrayList<>();
            owner_list.add(player.getUniqueId());

            owners.put(s, owner_list);
            Country.put(player.getUniqueId(), s);
            PCT.put(s, 0);
            policies.put(s, Arrays.asList(new PolicyEconomics(2), new PolicyMarket(2), new PolicyMilitary(2), new PolicyTourist(2), new PolicyTaxation(2)));
            stabilityMap.put(s, 70.0);

            player.sendMessage("§bcountry has been succesfully created");
        }else{
            player.sendMessage("§bthis country already exists");
        }
    }
    public static void delete(String s,Player player){
        if (banks.containsKey(s)){
            banks.remove(s);
            owners.remove(s);
            members.remove(s);
            PCT.remove(s);
            color_map.remove(s);
            spawn_loc.remove(s);
            new HashMap<UUID, String>(Country).keySet().forEach(key ->{

                if (Country.getOrDefault(key, "none").equals(s)){
                    Country.remove(key);
                }
            });

            player.sendMessage("bank removed");
        }else{
            player.sendMessage("this bank does not exists");
        }
    }
    public static boolean withdraw(String s, int amount){

        if (!banks.containsKey(s)){
            return false;
        }

        int balance = banks.get(s);
        int bal = balance - amount;

        if (bal < 0){
            return false;
        }

        banks.put(s, bal);
        changeCapital(s, balance, bal);
        return true;
    }
    public static void deposit(String s, int amount){
        if (banks.containsKey(s)){
            int balance = banks.get(s);
            int bal = balance + amount;
            banks.put(s, bal);
            changeCapital(s, balance, bal);
        }
    }
    public static boolean has(String s,int amount){
        if (banks.containsKey(s)){
            int balance = banks.get(s);
            int bal = balance - amount;
            if(bal >= 0){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    public static void addOwner(String s, Player player){
        List<UUID> owner_list = owners.get(s);
        if (!owner_list.contains(player.getUniqueId())){
            owner_list.add(player.getUniqueId());
            owners.put(s, owner_list);
            Country.put(player.getUniqueId(), s);
            player.sendMessage("you added as owner");

        }else{
            player.sendMessage("you are already an owner");
        }
    }

    public static void OfflineSetOwner(String s, UUID uuid){
        List<UUID> owner_list = owners.get(s);
        if (!owner_list.contains(uuid)){
            owner_list.add(uuid);
            owners.put(s, owner_list);
            Country.put(uuid, s);
        }

    }

    public static void removeOwner(String s, Player player){
        List<UUID> owner_list = owners.get(s);
        if (owner_list.contains(player.getUniqueId())){
            owner_list.remove(player.getUniqueId());
            owners.put(s, owner_list);
            Country.remove(player.getUniqueId());
        }
    }

    public static void OfflineRemoveOwner(String s, UUID uuid){
        List<UUID> owner_list = owners.get(s);
        if (owner_list.contains(uuid)){
            owner_list.remove(uuid);
            owners.put(s, owner_list);
            Country.remove(uuid);
        }
    }

    public static boolean isOwner(String s, Player player){
        List<UUID> owner_list = owners.get(s);
        return owner_list.contains(player.getUniqueId());
    }

    public static String Element(Player player){
        return Country.getOrDefault(player.getUniqueId(), "none");

    }

    public static String ElementUUID(UUID uuid){
        return Country.getOrDefault(uuid, "none");

    }

    public static String offlineElement(OfflinePlayer player){
        for (UUID p : Country.keySet()){
            if (p.equals(player.getUniqueId())) {
                return Country.getOrDefault(p, "none");
            }
        }
        return "none";
    }

    public static int bal (String s){
        return banks.get(s);

    }

    public static void addMember(String s, Player player){
        List<UUID> member_list;
        if (members.get(s) != null){
            member_list = members.get(s);
        }else{
            member_list = new ArrayList<>();
        }

        List<UUID> owner_list;
        if (owners.get(s) != null){
            owner_list = owners.get(s);
        }else{
            owner_list = new ArrayList<>();
        }
        if (!member_list.contains(player.getUniqueId()) || !owner_list.contains(player.getUniqueId())) {
            member_list.add(player.getUniqueId());
            members.put(s, member_list);
            Country.put(player.getUniqueId(), s);


            Banking.add_stability(s, 3.0*(1.0+Banking.getPolicyBonus(s, 2, 9)));
        }
    }

    public static void addLawEnforcement(String s, Player player){
        List<UUID> law_list;
        if (law_enforcement.get(s) != null){
            law_list = law_enforcement.get(s);
        }else{
            law_list = new ArrayList<>();
        }

        List<UUID> owner_list;
        if (owners.get(s) != null){
            owner_list = owners.get(s);
        }else{
            owner_list = new ArrayList<>();
        }
        if (!law_list.contains(player.getUniqueId()) || !owner_list.contains(player.getUniqueId())) {
            law_list.add(player.getUniqueId());
            law_enforcement.put(s, law_list);
        }
    }

    public static void removeLawEnforcement(String s, Player player){
        List<UUID> law_list = law_enforcement.get(s);
        if (law_list.contains(player.getUniqueId())) {
            law_list.remove(player.getUniqueId());
            law_enforcement.put(s, law_list);
        }
    }

    public static void offlineRemoveLawEnforcement(String s, UUID uuid){
        List<UUID> member_list = law_enforcement.get(s);
        if (member_list.contains(uuid)) {
            member_list.remove(uuid);
            law_enforcement.put(s, member_list);
        }
    }

    public static void offlineAddMember(String s, UUID uuid){
        List<UUID> member_list;
        if (members.get(s) != null){
            member_list = members.get(s);
        }else{
            member_list = new ArrayList<>();
        }

        List<UUID> owner_list;
        if (owners.get(s) != null){
            owner_list = owners.get(s);
        }else{
            owner_list = new ArrayList<>();
        }
        if (!member_list.contains(uuid) || !owner_list.contains(uuid)) {
            member_list.add(uuid);
            members.put(s, member_list);
            Country.put(uuid, s);
        }
    }

    public static void removeMember(String s, Player player){
        List<UUID> member_list = members.get(s);
        if (member_list.contains(player.getUniqueId())) {
            member_list.remove(player.getUniqueId());
            members.put(s, member_list);
            Country.remove(player.getUniqueId());
            Banking.add_stability(s, -3.0*(1.0-Banking.getPolicyBonus(s, 2, 10)));
        }
    }

    public static void offlineRemoveMember(String s, UUID uuid){
        List<UUID> member_list = members.get(s);
        if (member_list.contains(uuid)) {
            member_list.remove(uuid);
            members.put(s, member_list);
            Country.remove(uuid);
            Banking.add_stability(s, -3.0);
        }
    }

    public static List<String> GetBanks(){
        List<String> lst = new ArrayList<String>(banks.keySet());
        return lst;

    }

    public static int getPCT(String name){
        return PCT.getOrDefault(name, 0);

    }

    public static void setPCT(String name, int amount){
        PCT.put(name, amount);

    }

    public static void info(String name, Player player){
        int amount = banks.get(name);
        List<UUID> player_list = owners.get(name);
        List<UUID> member_list = members.get(name);
        List<UUID> law_list = law_enforcement.get(name);

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

        int pct = PCT.get(name);
        Location spawn = spawn_loc.getOrDefault(name, new Location(player.getWorld(),0, 0, 0));
        DecimalFormat df = new DecimalFormat("###,###,###");
        player.sendMessage("§bBank: §7"+name+"\n" +
                             "§bbalance: §7"+String.valueOf(df.format(amount))+"\n" +
                             "§bowners: §7"+p_list+"\n" +
                             "§bmembers: §7"+m_list+"\n" +
                             "§blaw enforcement: §7"+l_list+"\n" +
                             "§btaxrate: §7"+ pct+"\n" +
                             "§bstability: §7"+ df.format(getStability(name))+"\n" +
                             "§bspawn: §7"+ "\nX: "+spawn.getX()+ "\nY: "+spawn.getY()+ "\nZ: "+spawn.getZ());

    }

    public static List<String> getTop(){
        List<String> start = new ArrayList<String>(Banking.GetBanks());
        List<String> end = new ArrayList<String>();
        for (String s: start){
            int amount = banks.get(s);
            if (end.size() == 0){
                end.add(s);
            }
            int index = end.size();
            //int index = 0;
            for (String old: end){
                int value = banks.get(old);
                if (value < amount){
                    index = end.indexOf(old);
                    break;
                    //end.add(end.indexOf(old), s);
                }

            }
            if (!end.contains(s)){
                end.add(index ,s);
            }

        }
        return end;
    }

    public static ChatColor GetColor(String name){
        return ChatColor.valueOf(color_map.getOrDefault(name, "WHITE"));
    }

    public static List<UUID> GetOwners(String name){
        return owners.get(name);
    }

    public static List<UUID> getMembers(String name){
        return members.get(name);
    }

    public static void SetColor(String name, String color){
        color_map.put(name, color);

    }

    public static void SetSpawn(String name, Location location){
        spawn_loc.put(name, location);
    }

    public static Location GetSpawn(String name){
        return spawn_loc.getOrDefault(name, null);
    }

    public static void SaveData(){
        for (Map.Entry<String, Integer> entry : banks.entrySet()){
            SignClick.getPlugin().getConfig().set("bank." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, List<UUID>> entry : owners.entrySet()){

            List<String> f_list = new ArrayList<String>();
            for (UUID uuid: entry.getValue()){
                f_list.add(uuid.toString());
            }


            SignClick.getPlugin().getConfig().set("owners." + entry.getKey(), f_list);


        }

        for (Map.Entry<String, List<UUID>> entry : members.entrySet()){

            List<String> f_list = new ArrayList<String>();
            for (UUID uuid: entry.getValue()){
                f_list.add(uuid.toString());
            }


            SignClick.getPlugin().getConfig().set("members." + entry.getKey(), f_list);
        }

        for (Map.Entry<UUID, String> entry : Country.entrySet()){
            SignClick.getPlugin().getConfig().set("country." + entry.getKey().toString(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : PCT.entrySet()){
            SignClick.getPlugin().getConfig().set("pct." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, String> entry : color_map.entrySet()){
            SignClick.getPlugin().getConfig().set("color." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String , Location> entry : spawn_loc.entrySet()){
            SignClick.getPlugin().getConfig().set("spawn." + entry.getKey(), entry.getValue());

        }

        for (Map.Entry<String, List<UUID>> entry : law_enforcement.entrySet()){

            List<String> f_list = new ArrayList<String>();
            for (UUID uuid: entry.getValue()){
                f_list.add(uuid.toString());
            }


            SignClick.getPlugin().getConfig().set("law_enforcement." + entry.getKey(), f_list);
        }

        for (Map.Entry<String, Double> entry : stabilityMap.entrySet()){
            SignClick.getPlugin().getConfig().set("stability_map." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, List<Policy>> entry : policies.entrySet()){
            for (Policy p: entry.getValue()){
                p.Save(entry.getKey());
            }
        }

        SignClick.getPlugin().getConfig().set("parties", null);
        for (Map.Entry<String, List<Party>> entry : parties.entrySet()){
            for (Party p: entry.getValue()){
                p.Save();
            }
        }

        SignClick.getPlugin().getConfig().set("elections", null);
        for (Map.Entry<String, Election> entry : CountryElections.entrySet()){
            entry.getValue().Save();
        }

        SignClick.getPlugin().getConfig().set("decision", null);
        for (Map.Entry<String, List<Decision>> entry: decisions.entrySet()){
            int counter = 0;
            for (Decision d: entry.getValue()){
                d.Save(counter);
                counter++;
            }
        }

        for (Map.Entry<String, Boolean> entry: forbid_party.entrySet()){
            SignClick.getPlugin().getConfig().set("forbid_party." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Boolean> entry: aboard_military.entrySet()){
            SignClick.getPlugin().getConfig().set("aboard_military." + entry.getKey(), entry.getValue());
        }


        SignClick.getPlugin().getConfig().options().copyDefaults(true);
        SignClick.getPlugin().saveConfig();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SignClick save banking completed!");

    }

    public static void RestoreData(){

        if (SignClick.getPlugin().getConfig().contains("bank")){
            SignClick.getPlugin().getConfig().getConfigurationSection("bank").getKeys(true).forEach(key ->{
                int amount = (int) SignClick.getPlugin().getConfig().get("bank." + key);
                banks.put(key, amount);
            });
        }

        if (SignClick.getPlugin().getConfig().contains("owners")) {
            SignClick.getPlugin().getConfig().getConfigurationSection("owners").getKeys(true).forEach(key -> {

                List<UUID> f_list = new ArrayList<UUID>();
                for (String s: (List<String>) SignClick.getPlugin().getConfig().get("owners." + key)){
                    f_list.add(UUID.fromString(s));
                }

                owners.put(key, f_list);
            });
        }

        if (SignClick.getPlugin().getConfig().contains("members")){
            SignClick.getPlugin().getConfig().getConfigurationSection("members").getKeys(true).forEach(key ->{

                List<UUID> f_list = new ArrayList<UUID>();
                for (String s: (List<String>) SignClick.getPlugin().getConfig().get("members." + key)){
                    f_list.add(UUID.fromString(s));
                }

                members.put(key, f_list);
            });
        }

        if (SignClick.getPlugin().getConfig().contains("country")){
            SignClick.getPlugin().getConfig().getConfigurationSection("country").getKeys(true).forEach(key ->{
                Country.put(UUID.fromString(key), String.valueOf(SignClick.getPlugin().getPlugin().getConfig().get("country." + key)));
            });
        }

        if (SignClick.getPlugin().getConfig().contains("pct")){
            SignClick.getPlugin().getConfig().getConfigurationSection("pct").getKeys(true).forEach(key ->{
                PCT.put(key, (Integer) SignClick.getPlugin().getConfig().get("pct." + key));
            });
        }

        if (SignClick.getPlugin().getConfig().contains("color")){
            SignClick.getPlugin().getConfig().getConfigurationSection("color").getKeys(false).forEach(key ->{
                color_map.put(key, String.valueOf(SignClick.getPlugin().getConfig().get("color." + key)));
            });
        }

        if (SignClick.getPlugin().getConfig().contains("stability_map")){
            SignClick.getPlugin().getConfig().getConfigurationSection("stability_map").getKeys(false).forEach(key ->{
                stabilityMap.put(key, (double) SignClick.getPlugin().getConfig().get("stability_map." + key));
            });
        }

        if (SignClick.getPlugin().getConfig().contains("spawn")){
            SignClick.getPlugin().getConfig().getConfigurationSection("spawn").getKeys(false).forEach(key ->{
                Location location = (Location) SignClick.getPlugin().getConfig().get("spawn." + key);
                spawn_loc.put(key, location);
            });

        }

        if (SignClick.getPlugin().getConfig().contains("law_enforcement")){
            SignClick.getPlugin().getConfig().getConfigurationSection("law_enforcement").getKeys(true).forEach(key ->{

                List<UUID> f_list = new ArrayList<UUID>();
                for (String s: (List<String>) SignClick.getPlugin().getConfig().get("law_enforcement." + key)){
                    f_list.add(UUID.fromString(s));
                }

                law_enforcement.put(key, f_list);
            });
        }

        List<String> checked = new ArrayList<>();
        if (SignClick.getPlugin().getConfig().contains("policies")){

            SignClick.getPlugin().getConfig().getConfigurationSection("policies").getKeys(false).forEach(key ->{

                List<Policy> p_list = new ArrayList<>();
                for (int i=0; i<5; i++){
                    int level = (int) SignClick.getPlugin().getConfig().get("policies." + key+"."+ i);
                    if (i == 0){
                        p_list.add(new PolicyEconomics(level));
                    }
                    if (i == 1){
                        p_list.add(new PolicyMarket(level));
                    }
                    if (i == 2){
                        p_list.add(new PolicyMilitary(level));
                    }
                    if (i == 3){
                        p_list.add(new PolicyTourist(level));
                    }
                    if (i == 4){
                        p_list.add(new PolicyTaxation(level));
                    }
                    policies.put(key, p_list);
                }

                checked.add(key);
            });

        }

        for (String name: banks.keySet()){
            if (!checked.contains(name)){

                policies.put(name, Arrays.asList(new PolicyEconomics(2), new PolicyMarket(2), new PolicyMilitary(2), new PolicyTourist(2), new PolicyTaxation(2)));
            }
        }

        if (SignClick.getPlugin().getConfig().contains("parties") && SignClick.getPlugin().getConfig().get("parties") != null) {
            List<String> done_countries = new ArrayList<>();
            SignClick.getPlugin().getConfig().getConfigurationSection("parties").getKeys(false).forEach(country_vs -> {
                done_countries.add(country_vs);
                List<Party> party_list = new ArrayList<>();
                SignClick.getPlugin().getConfig().getConfigurationSection("parties."+country_vs).getKeys(false).forEach(party -> {
                    double pct = (double) SignClick.getPlugin().getConfig().get("parties."+country_vs+"."+party+".PCT");

                    List<UUID> m_list = new ArrayList<UUID>();
                    for (String s: (List<String>) SignClick.getPlugin().getConfig().get("parties."+country_vs+"."+party+".members")){
                        m_list.add(UUID.fromString(s));
                    }

                    List<UUID> o_list = new ArrayList<UUID>();
                    for (String s: (List<String>) SignClick.getPlugin().getConfig().get("parties."+country_vs+"."+party+".owners")){
                        o_list.add(UUID.fromString(s));
                    }

                    Party p = new Party(party, country_vs, pct, o_list, m_list);
                    party_list.add(p);

                });

                parties.put(country_vs, party_list);

            });

            for (String country: Banking.banks.keySet()){
                if (done_countries.contains(country)){
                    continue;
                }

                List<Party> party_list = new ArrayList<>();
                Party p = new Party("current government", country, 1.0, Banking.owners.get(country), new ArrayList<>());
                party_list.add(p);

                parties.put(country, party_list);
            }
        }

        if (SignClick.getPlugin().getConfig().contains("election") && SignClick.getPlugin().getConfig().get("election") != null) {
            SignClick.getPlugin().getConfig().getConfigurationSection("election").getKeys(false).forEach(country_vs -> {
                Map<String, Integer> vote_dict = new HashMap<>();
                SignClick.getPlugin().getConfig().getConfigurationSection("election."+country_vs+".vote_dict").getKeys(false).forEach(vote_key -> {
                    vote_dict.put(vote_key, (int) SignClick.getPlugin().getConfig().get("election."+country_vs+".vote_dict."+vote_key));
                });

                List<UUID> already_voted = new ArrayList<UUID>();
                for (String s: (List<String>) SignClick.getPlugin().getConfig().get("election."+country_vs+".voted")){
                    already_voted.add(UUID.fromString(s));
                }

                long time = (int) SignClick.getPlugin().getConfig().get("election."+country_vs+".to_wait");
                Election e = new Election(country_vs, time+(System.currentTimeMillis()/1000));
                e.vote_dict = vote_dict;
                e.already_voted = already_voted;
                BankCommands.CountryElections.put(country_vs, e);

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
                    public void run() {
                        Election e = CountryElections.get(country_vs);
                        CountryElections.remove(country_vs);

                        double total = 0.0;
                        for (float f : e.vote_dict.values()) {
                            total += f;
                        }

                        if (total == 0.0){
                            return;
                        }

                        for (Party p: Banking.parties.getOrDefault(country_vs, new ArrayList<>())){
                            double pct = (double) e.vote_dict.getOrDefault(p.name, 0)/total;
                            p.PCT = pct;
                        }

                        double highest_pct = -0.1;
                        Party highest_party = null;

                        for (Party p: Banking.parties.getOrDefault(country_vs, new ArrayList<>())){
                            double pct = (double) e.vote_dict.getOrDefault(p.name, 0)/total;
                            p.PCT = pct;

                            if (pct > highest_pct){
                                highest_pct = pct;
                                highest_party = p;
                            }
                        }

                        if (highest_party != Banking.getRuling(country_vs)){
                            double base = 2.0*(1.0-Banking.getPolicyBonus(country_vs, 2, 8));
                            Banking.add_stability(country_vs, -base);
                        }

                        List<UUID> old_owners = Banking.owners.getOrDefault(country_vs, new ArrayList<>());
                        List<UUID> members = Banking.members.getOrDefault(country_vs, new ArrayList<>());
                        for (UUID uuid: old_owners){
                            members.add(uuid);
                        }
                        Banking.members.put(country_vs, members);

                        Banking.owners.put(country_vs, highest_party.owners);

                        for (UUID uuid: highest_party.owners){
                            members.remove(uuid);
                        }

                        for (Decision d: Banking.decisions.get(country_vs)){
                            d.checkApprove();
                        }

                    }
                }, 20*time);

            });
        }

        if (SignClick.getPlugin().getConfig().contains("decision") && SignClick.getPlugin().getConfig().get("decision") != null) {
            SignClick.getPlugin().getConfig().getConfigurationSection("decision").getKeys(false).forEach(country_vs -> {
                List<Decision> d_list = new ArrayList<>();
                SignClick.getPlugin().getConfig().getConfigurationSection("decision."+country_vs).getKeys(false).forEach(index -> {
                    String name = (String) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".name");
                    double needed = (double) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".needed");
                    int id = (int) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".id");
                    List<String> approved_index = (List<String>) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".approved_index");
                    List<Party> approved = new ArrayList<>();
                    for (String a: approved_index){
                        approved.add(Banking.parties.get(country_vs).get(Integer.valueOf(a)));
                    }

                    List<String> disapproved_index = (List<String>) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".disapproved_index");
                    List<Party> disapproved = new ArrayList<>();
                    for (String d: disapproved_index){
                        disapproved.add(Banking.parties.get(country_vs).get(Integer.valueOf(d)));
                    }

                    Decision d = null;
                    if (id == 0){
                        int policy_id = (int) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".policy_id");
                        int old_level = (int) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".old_level");
                        int level = (int) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".level");

                        d = new DecisionPolicy(name, needed, country_vs, policy_id, old_level, level);
                        d_list.add(d);
                    }else if (id == 1){
                        int p = (int) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".p");
                        d = new DecisionBanParty(name, needed, country_vs, Banking.parties.get(country_vs).get(p));
                        d_list.add(d);
                    }else if (id == 2){
                        boolean b = (boolean) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".b");
                        d = new DecisionForbidParty(name, needed, country_vs, b);
                        d_list.add(d);
                    }else if (id == 3){
                        boolean b = (boolean) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".b");
                        d = new DecisionAboardMilitary(name, needed, country_vs, b);
                        d_list.add(d);
                    }else if (id == 4){
                        String party_name = (String) SignClick.getPlugin().getConfig().get("decision."+country_vs+"."+index+".party_name");
                        d = new DecisionCoup(name, needed, country_vs, party_name);
                        d_list.add(d);
                    }

                    d.approved = approved;
                    d.disapproved = disapproved;
                });

                Banking.decisions.put(country_vs, d_list);

            });

        }

        if (SignClick.getPlugin().getConfig().contains("forbid_party")){
            SignClick.getPlugin().getConfig().getConfigurationSection("forbid_party").getKeys(false).forEach(country_vs -> {
                boolean b = (boolean) SignClick.getPlugin().getConfig().get("forbid_party."+country_vs);
                forbid_party.put(country_vs, b);
            });
        }

        if (SignClick.getPlugin().getConfig().contains("aboard_military")){
            SignClick.getPlugin().getConfig().getConfigurationSection("aboard_military").getKeys(false).forEach(country_vs -> {
                boolean b = (boolean) SignClick.getPlugin().getConfig().get("aboard_military."+country_vs);
                aboard_military.put(country_vs, b);
            });
        }


    }

    public static void RunLawSalary(){
        for (Map.Entry<String , List<UUID>> entry : law_enforcement.entrySet()){
            for (UUID uuid : entry.getValue()){
                double base = 0;
                if (Banking.getStability(entry.getKey()) < 50){
                    base += 2000.0;
                }

                if (Banking.getStability(entry.getKey()) < 30){
                    base += 3000.0;
                }

                Banking.withdraw(entry.getKey(), (int) Banking.getPolicyBonus(entry.getKey(), 2, 0)+(int) base);
                SignClick.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(uuid), (int) Banking.getPolicyBonus(entry.getKey(), 2, 0)+(int) base);
            }
        }
    }

    public static List<Policy> getPolicies(String s){
        return policies.get(s);
    }

    public static void setPolicies(String s, int id, int level){
        int old_level = policies.get(s).get(id).level;
        if (old_level == level){
            return;
        }

        if (id == 0 || id == 3){
            int gov_cap = Banking.getPolicyRequire(s, id, 0, level);
            if (gov_cap > Banking.bal(s)){
                return;
            }
        }

        if (id == 2 || id == 4){
            int gov_cap = Banking.getPolicyRequire(s, id, 1, level);
            if (gov_cap > Banking.bal(s)){
                return;
            }

            int law_enfo = Banking.getPolicyRequire(s, id, 0, level);
            if (law_enfo > law_enforcement.get(s).size()){
                return;
            }
        }

        if (id == 4){
            int tax_rate = Banking.getPolicyRequire(s, id, 2, level);
            if (level < 2 && tax_rate > PCT.get(s)){
                return;
            }

            if (level > 2 && tax_rate < PCT.get(s)){
                return;
            }
        }

        double change = 0.5;
        if (level == 0 || level == 4){
            change = 0.7;
        }

        Decision d = new DecisionPolicy("§6Policy §9"+policies.get(s).get(id).titles.get(old_level)+
                "§6 to §9"+policies.get(s).get(id).titles.get(level), change, s, id, old_level, level);

        if (Banking.hasDecisionName(s, d.name)){
            return;
        }

        List<Decision> d_list = decisions.getOrDefault(s, new ArrayList<>());
        d_list.add(d);
        decisions.put(s, d_list);
    }

    public static void setPoliciesReal(String s, int id, int old_level, int level){
        policies.get(s).get(id).level = level;


        if (id == 0){
            Double country_stability = stabilityMap.getOrDefault(s, 70.0);
            Double old_stab = policies.get(s).get(id).getBonusLevel(5, old_level);
            Double new_stab = policies.get(s).get(id).getBonusLevel(5, level);
            double change = new_stab-old_stab;
            stabilityMap.put(s, country_stability+change);
        }

        if (id == 2){
            Double country_stability = stabilityMap.getOrDefault(s, 70.0);
            Double old_stab = policies.get(s).get(id).getBonusLevel(1, old_level);
            Double new_stab = policies.get(s).get(id).getBonusLevel(1, level);
            double change = new_stab-old_stab;
            stabilityMap.put(s, country_stability+change);

            if (old_level == 4){
                forbid_party.put(s, false);
            }

        }

        if (id == 4){
            Double country_stability = stabilityMap.getOrDefault(s, 70.0);
            Double old_stab = policies.get(s).get(id).getBonusLevel(7, old_level);
            Double new_stab = policies.get(s).get(id).getBonusLevel(7, level);
            double change = new_stab-old_stab;
            stabilityMap.put(s, country_stability+change);

        }

    }

    public static double getPolicyBonus(String s, int id, int index){
        return policies.get(s).get(id).getBonus(index);
    }

    public static int getPolicyRequire(String s, int id, int index, int level){
        return policies.get(s).get(id).getRequireLevel(index, level);
    }

    public static Boolean inParty(String s, UUID uuid){
        for (Party p : parties.getOrDefault(s, new ArrayList<>())){
            if (p.inParty(uuid)){
                return true;
            }
        }
        return false;
    }

    public static void createParty(String s, String name, UUID owner){
        Party p = new Party(name, s, owner);
        List<Party> p_list = parties.getOrDefault(s, new ArrayList<>());
        p_list.add(p);
        parties.put(s, p_list);
    }

    public static Party getParty(String s, UUID uuid){
        for (Party p : parties.getOrDefault(s, new ArrayList<Party>())){
            if (p.inParty(uuid)){
                return p;
            }
        }

        return null;
    }

    public static Party getParty(String s, String name){
        for (Party p : parties.getOrDefault(s, new ArrayList<Party>())){
            if (p.name.equals(name)){
                return p;
            }
        }

        return null;
    }

    public static Boolean hasPartyName(String s, String name){
        for (Party p : parties.getOrDefault(s, new ArrayList<Party>())){
            if (p.name.equals(name)){
                return true;
            }
        }

        return false;
    }

    public static Party getRuling(String s){
        /*
        * First Ruling party is automatic the highest, when no elections occurred
        * */
        double highest_pct = -0.1;
        Party highest_party = null;

        for (Party p: Banking.parties.getOrDefault(s, new ArrayList<>())){

            if (p.PCT > highest_pct){
                highest_pct = p.PCT;
                highest_party = p;
            }
        }

        return highest_party;
    }

    public static Boolean hasDecisionName(String s, String name){
        for (Decision d : decisions.getOrDefault(s, new ArrayList<Decision>())){
            if (d.name.equals(name)){
                return true;
            }
        }

        return false;
    }

    public static void runStability(){
        //no election
        for (String s: Banking.banks.keySet()){
            double base = 1.0;
            base -= Banking.getPolicyBonus(s, 2, 3);
            Banking.add_stability(s, -base);

        }

        for (Map.Entry<String, Boolean> entry: Banking.forbid_party.entrySet()){
            if (entry.getValue()){
                Banking.add_stability(entry.getKey(), -3.0);
            }

        }
    }

    private static void changeCapital(String s, int old_cap, int new_cap){
        Map<Integer, Double> d = new HashMap<>();

        d.put(5000000, 2.0);
        d.put(20000000, 2.0);
        d.put(40000000, 1.0);
        d.put(60000000, 2.0);
        d.put(80000000, 1.0);
        d.put(100000000, 2.0);

        for (Map.Entry<Integer, Double> entry: d.entrySet()){
            if (!(Math.min(old_cap, new_cap) < entry.getKey() && entry.getKey() < Math.max(old_cap, new_cap))){
                continue;
            }

            boolean grow = new_cap-old_cap > 0;

            if (grow){
                Banking.add_stability(s, entry.getValue());
            }else{
                Banking.add_stability(s, -entry.getValue());
            }
        }
    }

    public static void removeParty(Party p){
        String country = p.country;
        Party ph = Banking.getRuling(country);

        ph.PCT += p.PCT;
        Banking.parties.get(country).remove(p);

        add_stability(country, -10.0);
    }

}
