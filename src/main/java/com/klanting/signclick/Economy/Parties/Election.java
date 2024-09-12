package com.klanting.signclick.Economy.Parties;

import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.SignClick;

import java.util.*;

public class Election {

    public Map<String, Integer> vote_dict =  new HashMap<String, Integer>();

    public List<UUID> alreadyVoted = new ArrayList<>();
    public String s;
    public long timeEnded;
    public Election(String s, long timeEnded){

        for (Party p: Country.parties.getOrDefault(s, new ArrayList<>())){
            vote_dict.put(p.name, 0);
            this.s = s;
            this.timeEnded = timeEnded;
        }
    }

    public void vote(String name, UUID uuid){
        if (!alreadyVoted.contains(uuid)){
            alreadyVoted.add(uuid);
            int party_val = vote_dict.getOrDefault(name, 0);
            vote_dict.put(name, party_val+1);
        }
    }



    public void Save(){
        String path = "election." + s + ".";
        SignClick.getPlugin().getConfig().set(path+"vote_dict", vote_dict);

        List<String> f_list = new ArrayList<String>();
        for (UUID uuid: alreadyVoted){
            f_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set(path+"voted", f_list);

        SignClick.getPlugin().getConfig().set(path+"to_wait", timeEnded -System.currentTimeMillis()/1000);
    }
}
