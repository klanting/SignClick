package com.klanting.signclick.Economy.Decisions;

import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.Economy.CountryManager;
import com.klanting.signclick.SignClick;

public class DecisionAboardMilitary extends Decision{
    public boolean b;
    public DecisionAboardMilitary(String name, double needed, String s, boolean b){
        super(name, needed, 3, s);
        this.b =b;
    }

    public void DoEffect(){
        Country country = CountryManager.getCountry(s);
        country.setAboardMilitary(b);
        if (b){
            country.addStability(-2.0);
        }
    }

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        SignClick.getPlugin().getConfig().set(path+"b", b);
    }
}
