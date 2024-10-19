package com.klanting.signclick.economy.decisions;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.SignClick;

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

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        SignClick.getPlugin().getConfig().set(path+"policy_id", policy_id);
        SignClick.getPlugin().getConfig().set(path+"old_level", old_level);
        SignClick.getPlugin().getConfig().set(path+"level", level);
    }

}
