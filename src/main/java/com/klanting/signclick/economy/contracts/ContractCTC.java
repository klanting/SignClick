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

    @Override
    public boolean runContract(){
        /*
        * return true -> keep contract, else not
        * */

        /*
        * In case we are unable to retrieve the money from the company account
        * */
        if (!from.removeBal(amount)) {
            return true;
        }

        weeks -= 1;
        to.addBal(amount);

        from.getCOM().sendOwner("§cContract: from " + from.getStockName() + "(C) to " + to.getStockName() + "(C) amount: " + amount);
        to.getCOM().sendOwner("§aContract: from " + from.getStockName() + "(C) to " + to.getStockName() + "(C) amount: " + amount);
        return weeks > 0;


    }

    @Override
    public String from() {
        return from.getStockName();
    }

    @Override
    public String to() {
        return to.getStockName();
    }

    @Override
    public String getContractStatus(boolean isIncome){
        String prefix = "§c";
        if (isIncome){
            prefix = "§a";
        }

        return prefix + "Contract: from " + from.getStockName() +
                "(C) to " + to.getStockName() + "(C) amount: " + amount
                + " for "+weeks+" weeks, " + "reason: "+ getReason();
    }
}
