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

        if (from.remove_bal(amount)) {
            weeks -= 1;
            to.addBal(amount);

            from.send_owner("§cContract: from " + from.Sname + "(C) to " + to.Sname + "(C) amount: " + amount);
            to.send_owner("§aContract: from " + from.Sname + "(C) to " + to.Sname + "(C) amount: " + amount);
            return weeks > 0;
        } else {

            return true;
        }
    }

    @Override
    public String from() {
        return from.Sname+"(C)";
    }

    @Override
    public String to() {
        return to.Sname+"(C)";
    }
}
