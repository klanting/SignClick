package com.klanting.signclick.economy.decisions;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.SignClick;

import java.util.ArrayList;
import java.util.List;

public class Decision {
    public String name;
    public double needed;
    public int id;
    public String s;

    public List<Party> approved = new ArrayList<>();
    public List<Party> disapproved = new ArrayList<>();

    public Decision(String name, double needed, int id, String s){
        this.name = name;
        this.needed = needed;
        this.id = id;
        this.s = s;
    }

    public void DoEffect(){}

    public double getApproved(){
        double total = 0;

        for (Party p: approved){
            total += p.PCT;
        }

        return total;
    }

    public double getDisapproved(){
        double total = 0;

        for (Party p: disapproved){
            total += p.PCT;
        }

        return total;
    }

    public void vote(Party p, boolean pro){
        if (pro){
            approved.add(p);
        }else{
            disapproved.add(p);
        }

        checkApprove();
    }

    public boolean hasVoted(Party p){
        return approved.contains(p) || disapproved.contains(p);
    }

    public void checkApprove(){
        Country country = CountryManager.getCountry(s);
        if (getApproved() >= needed){
            country.removeDecision(this);
            DoEffect();
        }

        if (getDisapproved() > (1.0-needed)){
            country.removeDecision(this);
        }
    }
}
