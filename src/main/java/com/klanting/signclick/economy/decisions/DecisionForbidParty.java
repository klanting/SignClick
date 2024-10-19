package com.klanting.signclick.economy.decisions;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.SignClick;

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

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        SignClick.getPlugin().getConfig().set(path+"b", b);
    }
}
