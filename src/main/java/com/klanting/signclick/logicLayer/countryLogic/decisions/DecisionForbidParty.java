package com.klanting.signclick.logicLayer.countryLogic.decisions;

import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;

public class DecisionForbidParty extends Decision{
    public boolean b;
    public DecisionForbidParty(String name, double needed, String s, boolean b){
        super(name, needed, 2, s);
        this.b =b;
    }

    public void DoEffect(){
        Country country = CountryManager.getCountry(s);
        country.setForbidParty(b);
    }
}
