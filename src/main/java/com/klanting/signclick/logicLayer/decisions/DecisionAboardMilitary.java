package com.klanting.signclick.logicLayer.decisions;

import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;

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
}
