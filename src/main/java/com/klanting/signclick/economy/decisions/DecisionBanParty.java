package com.klanting.signclick.economy.decisions;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.SignClick;

public class DecisionBanParty extends Decision{
    public Party p;
    public DecisionBanParty(String name, double needed, String s, Party p){
        super(name, needed, 1, s);
        this.p =p;
    }

    public void DoEffect(){
        Country country = CountryManager.getCountry(p.country);
        country.removeParty(p);

    }

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        Country country = CountryManager.getCountry(s);
        SignClick.getPlugin().getConfig().set(path+"p", country.getParties().indexOf(p));
    }
}
