package com.klanting.signclick.logicLayer.countryLogic.decisions;

import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;

public class DecisionPolicy extends Decision{

    public int policy_id;
    public int old_level;
    public int level;
    public DecisionPolicy(String name, double needed, String s, int policy_id, int old_level, int level){
        super(name, needed, 0, s);
        this.policy_id = policy_id;
        this.old_level = old_level;
        this.level = level;
    }

    public void DoEffect(){
        Country country = CountryManager.getCountry(s);
        country.setPoliciesReal(policy_id, old_level, level);
    }

}
