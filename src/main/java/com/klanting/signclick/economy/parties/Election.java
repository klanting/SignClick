package com.klanting.signclick.economy.parties;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.SignClick;

import java.util.*;

public class Election {

    public Map<String, Integer> voteDict =  new HashMap<String, Integer>();

    public List<UUID> alreadyVoted = new ArrayList<>();
    public String s;
    public long timeEnded;
    public Election(String s, long timeEnded){

        Country country = CountryManager.getCountry(s);

        for (Party p: country.getParties()){
            voteDict.put(p.name, 0);

        }

        this.s = s;
        this.timeEnded = timeEnded;
    }

    public Election(String s, long timeEnded, Map<String, Integer> voteDict, List<UUID> alreadyVoted){
        this.s = s;
        this.timeEnded = timeEnded;
        this.voteDict = voteDict;
        this.alreadyVoted = alreadyVoted;
    }

    public void vote(String name, UUID uuid){
        if (!alreadyVoted.contains(uuid)){
            alreadyVoted.add(uuid);
            int party_val = voteDict.getOrDefault(name, 0);
            voteDict.put(name, party_val+1);
        }
    }

    public JsonObject toJson(JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();

        List<String> f_list = new ArrayList<String>();
        for (UUID uuid: alreadyVoted){
            f_list.add(uuid.toString());
        }

        jsonObject.add("vote_dict", context.serialize(voteDict));
        jsonObject.add("voted", context.serialize(f_list));
        jsonObject.add("to_wait", new JsonPrimitive(timeEnded -System.currentTimeMillis()/1000));
        jsonObject.add("name", new JsonPrimitive(s));

        return jsonObject;
    }

    public long getToWait(){
        return timeEnded-System.currentTimeMillis()/1000;
    }
}
