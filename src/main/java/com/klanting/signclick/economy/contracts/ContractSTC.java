package com.klanting.signclick.economy.contracts;

import com.klanting.signclick.economy.CompanyI;

public class ContractSTC  extends Contract{

    private final CompanyI to;
    private int delay;

    public int getDelay() {
        return delay;
    }

    public ContractSTC(CompanyI to, double amount, int weeks, String reason, int delay) {
        super(amount, weeks, reason);

        this.to = to.getRef();
        this.delay = delay;

    }

    @Override
    public boolean runContract(){
        /*
         * return true -> keep contract, else not
         * */

        if (delay != 0){
            delay -= 1;
            return true;
        }

        weeks -= 1;
        to.addBalNoPoint(amount);

        String message = "Contract: from SERVER (S) to " + to.getStockName() + "(C) amount: " + df.format(amount);

        to.update("Contract Payment", "§a"+message, null);

        to.getCOM().sendOwner("§a"+message);
        return weeks > 0;
    }

    @Override
    public String from() {
        return "SERVER";
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

        return prefix + "Contract: from SERVER(S) to " + to.getStockName() + "(C) amount: " + df.format(amount)
                + " for "+weeks+" weeks, " + "reason: "+getReason() + " delay: "+ getDelay();
    }
}
