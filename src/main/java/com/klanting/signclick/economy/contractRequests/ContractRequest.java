package com.klanting.signclick.economy.contractRequests;

public abstract class ContractRequest {

    protected double amount;
    protected int weeks;
    protected String reason;

    public ContractRequest(double amount, int weeks, String reason){
        this.amount = amount;
        this.weeks = weeks;
        this.reason = reason;

    }

    abstract public boolean accept();

    abstract public String to();

    public double getAmount() {
        return amount;
    }

    public int getWeeks() {
        return weeks;
    }

    public String getReason() {
        return reason;
    }
}
