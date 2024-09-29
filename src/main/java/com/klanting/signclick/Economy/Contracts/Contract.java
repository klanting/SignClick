package com.klanting.signclick.Economy.Contracts;

abstract public class Contract {

    protected double amount;
    protected int weeks;
    protected String reason;

    public Contract(double amount, int weeks, String reason){
        this.amount = amount;
        this.weeks = weeks;
        this.reason = reason;

    }

    abstract public boolean runContract();

    abstract public String from();
    abstract public String to();
}
