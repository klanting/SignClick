package com.klanting.signclick.economy.contracts;

import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ContractSTC  extends Contract{

    private final Company to;
    private int delay;

    public int getDelay() {
        return delay;
    }

    public ContractSTC(Company to, double amount, int weeks, String reason, int delay) {
        super(amount, weeks, reason);

        this.to = to;
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

        to.getCOM().sendOwner("§aContract: from SERVER (S) to " + to.getStockName() + "(C) amount: " + amount);
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
}
