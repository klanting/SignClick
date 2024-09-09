package com.klanting.signclick.Economy.Decisions;

import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.SignClick;

public class DecisionAboardMilitary extends Decision{
    public boolean b;
    public DecisionAboardMilitary(String name, double needed, String s, boolean b){
        super(name, needed, 3, s);
        this.b =b;
    }

    public void DoEffect(){
        Banking.aboard_military.put(s, b);
        if (b){
            Banking.add_stability(s, -2.0);
        }
    }

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        SignClick.getPlugin().getConfig().set(path+"b", b);
    }
}
