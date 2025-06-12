package com.klanting.signclick.economy.contracts;

import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.UUID;

public class ContractPTC extends Contract{

    private final Company to;
    private final UUID from;

    public ContractPTC(UUID from, Company to, double amount, int weeks, String reason) {
        super(amount, weeks, reason);

        this.to = to;
        this.from = from;

    }

    @Override
    public boolean runContract() {
        Account fromAcc = Market.getAccount(from);

        if (!fromAcc.removeBal(amount)) {
            return true;
        }

        weeks -= 1;
        to.addBal(amount);

        String message = "Contract: from " + fromAcc.getName() + "(P) to " + to.getStockName() + "(C) amount: " + amount;

        to.update("Contract Payment", "§a"+message, null);

        to.getCOM().sendOwner("§a"+message);
        if (fromAcc.getPlayer() != null){
            fromAcc.getPlayer().sendMessage("§c"+message);
        }

        return weeks > 0;
    }

    @Override
    public String from() {
        OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(from);
        return player.getName();
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

        return prefix + "Contract: from " + from() + "(P) to " + to() + "(C) amount: " + amount
                + " for "+weeks+" weeks, "+ "reason: "+ getReason();
    }

}
