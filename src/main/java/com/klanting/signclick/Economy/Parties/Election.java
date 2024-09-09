package com.klanting.signclick.Economy.Parties;

import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.SignClick;
import org.bukkit.ChatColor;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class Election {

    public Map<String, Integer> vote_dict =  new HashMap<String, Integer>();

    public List<UUID> already_voted = new ArrayList<>();
    public String s;
    public long time_ended;
    public Election(String s, long time_ended){

        for (Party p: Banking.parties.getOrDefault(s, new ArrayList<>())){
            vote_dict.put(p.name, 0);
            this.s = s;
            this.time_ended = time_ended;
        }
    }

    public void vote(String name, UUID uuid){
        if (!already_voted.contains(uuid)){
            already_voted.add(uuid);
            int party_val = vote_dict.getOrDefault(name, 0);
            vote_dict.put(name, party_val+1);
        }
    }



    public void Save(){
        String path = "election." + s + ".";
        SignClick.getPlugin().getConfig().set(path+"vote_dict", vote_dict);

        List<String> f_list = new ArrayList<String>();
        for (UUID uuid: already_voted){
            f_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set(path+"voted", f_list);

        SignClick.getPlugin().getConfig().set(path+"to_wait", time_ended-System.currentTimeMillis()/1000);
    }
}
