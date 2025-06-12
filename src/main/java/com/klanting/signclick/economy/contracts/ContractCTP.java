package com.klanting.signclick.economy.contracts;

import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class ContractCTP  extends Contract{
    private final Company from;
    private final UUID to;

    public ContractCTP(Company from, UUID to, double amount, int weeks, String reason) {
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

        Account toAccount = Market.getAccount(to);
        weeks -= 1;
        toAccount.addBal(amount);

        OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(to);

        String message = "Contract: from " + from.getStockName() + "(C) to " + player.getName() + "(P) amount: " + amount;

        from.update("Contract Payment", "§c"+message, null);

        from.getCOM().sendOwner("§c"+message);
        if (player.getPlayer() != null){
            player.getPlayer().sendMessage("§a"+message);
        }

        return weeks > 0;
    }

    @Override
    public String from() {
        return from.getStockName();
    }

    @Override
    public String to() {
        OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(to);
        return player.getName();
    }


    @Override
    public String getContractStatus(boolean isIncome){
        String prefix = "§c";
        if (isIncome){
            prefix = "§a";
        }

        return prefix + "Contract: from " + from.getStockName() + "(C) to " + to() + "(P) amount: " + amount
                + " for "+weeks+" weeks, "+ "reason: "+ getReason();
    }
}
