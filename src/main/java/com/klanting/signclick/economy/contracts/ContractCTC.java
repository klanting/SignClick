package com.klanting.signclick.economy.contracts;


import com.klanting.signclick.economy.Company;

public class ContractCTC extends Contract {

    private final Company from;
    private final Company to;

    public ContractCTC(Company from, Company to, double amount, int weeks, String reason) {
        super(amount, weeks, reason);

        this.from = from;
        this.to = to;

    }

    public boolean runContract(){
        /*
        * return true -> keep contract, else not
        * */

        if (from.removeBal(amount)) {
            weeks -= 1;
            to.addBal(amount);

            from.sendOwner("§cContract: from " + from.getStockName() + "(C) to " + to.getStockName() + "(C) amount: " + amount);
            to.sendOwner("§aContract: from " + from.getStockName() + "(C) to " + to.getStockName() + "(C) amount: " + amount);
            return weeks > 0;
        } else {

            return true;
        }
    }

    @Override
    public String from() {
        return from.getStockName() +"(C)";
    }

    @Override
    public String to() {
        return to.getStockName() +"(C)";
    }
}
