package com.klanting.signclick.logicLayer;

public class CountryNull extends Country{
    /*
    * Object in case a country would equal to Null.
    * To avoid many checks, by inheritance, when a country is Null, this object will be provided
    * This makes sure, that it provides the default information where needed
    * */

    public CountryNull() {
        super();
    }

    @Override
    public double getPolicyBonus(String s){

        return 0.0;
    }

    @Override
    public double getStability(){
        return 100.0;
    }
}
