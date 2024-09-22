package com.klanting.signclick.Economy.Decisions;

import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.Economy.CountryManager;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.SignClick;
import java.util.UUID;

public class DecisionCoup extends Decision{
    public String party_name;
    public DecisionCoup(String name, double needed, String s, String party_name){
        super(name, needed, 4, s);
        this.party_name =party_name;
    }

    public void DoEffect(){
        Country country = CountryManager.getCountry(s);
        Party p = country.getParty(party_name);
        Party ph = country.getRuling();

        p.PCT = ph.PCT;
        ph.PCT = 0;

        country.addStability(-40.0);

        for (UUID uuid: country.getOwners()){
            country.removeOwner(uuid);
            country.addMember(uuid);
        }

        for (UUID uuid: ph.owners){
            country.removeMember(uuid);
            country.addOwner(uuid);
        }

        for (Decision d: country.getDecisions()){
            d.checkApprove();
        }
    }

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        SignClick.getPlugin().getConfig().set(path+"party_name", party_name);
    }
}

