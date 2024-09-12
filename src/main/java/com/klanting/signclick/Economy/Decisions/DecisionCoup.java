package com.klanting.signclick.Economy.Decisions;

import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.SignClick;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DecisionCoup extends Decision{
    public String party_name;
    public DecisionCoup(String name, double needed, String s, String party_name){
        super(name, needed, 4, s);
        this.party_name =party_name;
    }

    public void DoEffect(){
        Party p = Country.getParty(s, party_name);
        Party ph = Country.getRuling(s);

        p.PCT = ph.PCT;
        ph.PCT = 0;

        Country.add_stability(s, -40.0);

        List<UUID> old_owners = Country.owners.getOrDefault(s, new ArrayList<>());
        List<UUID> members = Country.members.getOrDefault(s, new ArrayList<>());
        for (UUID uuid: old_owners){
            members.add(uuid);
        }
        Country.members.put(s, members);

        Country.owners.put(s, ph.owners);

        for (UUID uuid: ph.owners){
            members.remove(uuid);
        }

        for (Decision d: Country.decisions.get(s)){
            d.checkApprove();
        }
    }

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        SignClick.getPlugin().getConfig().set(path+"party_name", party_name);
    }
}

