package com.klanting.signclick.Economy.Decisions;

import com.klanting.signclick.SignClick;

import static com.klanting.signclick.Economy.Country.setPoliciesReal;

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
        setPoliciesReal(s, policy_id, old_level, level);
    }

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        SignClick.getPlugin().getConfig().set(path+"policy_id", policy_id);
        SignClick.getPlugin().getConfig().set(path+"old_level", old_level);
        SignClick.getPlugin().getConfig().set(path+"level", level);
    }

}
