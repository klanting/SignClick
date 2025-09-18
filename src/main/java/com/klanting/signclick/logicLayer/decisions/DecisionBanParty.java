package com.klanting.signclick.logicLayer.decisions;

import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.parties.Party;

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
}
