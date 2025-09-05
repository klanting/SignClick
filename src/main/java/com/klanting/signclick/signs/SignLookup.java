package com.klanting.signclick.signs;

import com.klanting.signclick.economy.companyPatent.PatentUpgrade;

import java.util.List;

public class SignLookup {

    private final String key;

    public SignLookup(String key){
        this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String s){

            List<String> options = List.of("signclick_", "sign_", "");
            for(String option: options){
                if (s.equalsIgnoreCase("§b["+option+this.key+"]")){
                    return true;
                }
            }

            return false;
        }

        if (obj instanceof SignLookup s){
            return this.key.equals(s.key);
        }

        return false;
    }

    public boolean preEquals(String s) {
        List<String> options = List.of("signclick_", "sign_", "");
        for(String option: options){
            if (s.equalsIgnoreCase("["+option+this.key+"]")){
                return true;
            }
        }

        return false;
    }


}
