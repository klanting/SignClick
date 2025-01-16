package com.klanting.signclick.economy.parties;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.SignClick;

import java.lang.reflect.Field;
import java.util.*;

public class Election {

    public Map<String, Integer> vote_dict =  new HashMap<String, Integer>();

    public List<UUID> alreadyVoted = new ArrayList<>();
    public String s;
    public long timeEnded;
    public Election(String s, long timeEnded){

        Country country = CountryManager.getCountry(s);

        for (Party p: country.getParties()){
            vote_dict.put(p.name, 0);

        }

        this.s = s;
        this.timeEnded = timeEnded;
    }

    public Election(String s, long timeEnded, Map<String, Integer> vote_dict, List<UUID> alreadyVoted){
        this.s = s;
        this.timeEnded = timeEnded;
        this.vote_dict = vote_dict;
        this.alreadyVoted = alreadyVoted;
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

    public JsonObject toJson(JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();

        List<String> f_list = new ArrayList<String>();
        for (UUID uuid: alreadyVoted){
            f_list.add(uuid.toString());
        }

        jsonObject.add("vote_dict", context.serialize(vote_dict));
        jsonObject.add("voted", context.serialize(f_list));
        jsonObject.add("to_wait", new JsonPrimitive(timeEnded -System.currentTimeMillis()/1000));
        jsonObject.add("name", new JsonPrimitive(s));

        return jsonObject;
    }

    public long getToWait(){
        return timeEnded-System.currentTimeMillis()/1000;
    }
}
