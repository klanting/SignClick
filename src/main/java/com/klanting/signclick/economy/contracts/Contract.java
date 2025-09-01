package com.klanting.signclick.economy.contracts;

import java.text.DecimalFormat;

abstract public class Contract {

    protected double amount;
    protected int weeks;
    protected String reason;

    protected static DecimalFormat df = new DecimalFormat("##0.0#");

    public int getWeeks() {
        return weeks;
    }

    public String getReason() {
        return reason;
    }

    public Contract(double amount, int weeks, String reason){
        this.amount = amount;
        this.weeks = weeks;
        this.reason = reason;

    }

    public double getAmount(){
        return amount;
    }

    abstract public boolean runContract();

    abstract public String from();
    abstract public String to();

    /*
    * Method to get a string representation showing the contract information
    * */
    abstract public String getContractStatus(boolean isIncome);
}
