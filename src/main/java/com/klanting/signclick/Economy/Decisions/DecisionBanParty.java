package com.klanting.signclick.Economy.Decisions;

import com.klanting.signclick.Economy.CountryDep;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.SignClick;

public class DecisionBanParty extends Decision{
    public Party p;
    public DecisionBanParty(String name, double needed, String s, Party p){
        super(name, needed, 1, s);
        this.p =p;
    }

    public void DoEffect(){
        CountryDep.removeParty(p);
    }

    public void Save(Integer index){
        super.Save(index);
        String path = "decision." + s+"."+index+".";
        SignClick.getPlugin().getConfig().set(path+"p", CountryDep.parties.get(s).indexOf(p));
    }
}
