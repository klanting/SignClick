package com.klanting.signclick.logicLayer.contracts;


import com.klanting.signclick.logicLayer.CompanyI;

public class ContractCTC extends Contract {

    private final CompanyI from;
    private final CompanyI to;

    public ContractCTC(CompanyI from, CompanyI to, double amount, int weeks, String reason) {
        super(amount, weeks, reason);

        this.from = from.getRef();
        this.to = to.getRef();

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



        String message = "Contract: from " + from.getStockName() + "(C) to " + to.getStockName() + "(C) amount: " + df.format(amount);

        from.getCOM().sendOwner("§c"+message);
        to.getCOM().sendOwner("§a"+message);

        from.update("Contract Payment", "§c"+message, null);
        to.update("Contract Payment", "§a"+message, null);

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
                "(C) to " + to.getStockName() + "(C) amount: " + df.format(amount)
                + " for "+weeks+" weeks, " + "reason: "+ getReason();
    }
}
