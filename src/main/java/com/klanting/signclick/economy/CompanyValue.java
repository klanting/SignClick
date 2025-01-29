package com.klanting.signclick.economy;

public class CompanyValue {
    /*
    * This class represents the economic information of a company
    * */

    /*
    * Represents the share base value
    * */
    private double shareBase = 0.0;

    /*
    * Represents the amount of money in the company back account
    * */
    private double bal = 0.0;


    public CompanyValue(){

    }

    public double getShareBase() {
        return shareBase;
    }
    public void setShareBase(double shareBase) {
        this.shareBase = shareBase;
    }

    public double getBal() {
        return bal;
    }

    public boolean addBal(double bal) {
        this.bal += bal;
        return true;
    }

    public boolean removeBal(double bal) {
        this.bal -= bal;
        return true;
    }

    public boolean addBalNoPoint(double bal) {
        this.bal += bal;
        return true;
    }

}
