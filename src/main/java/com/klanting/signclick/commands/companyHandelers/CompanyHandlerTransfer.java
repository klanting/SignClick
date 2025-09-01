package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.CommandTools;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CompanyHandlerTransfer extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 4, "please enter /company transfer <stockname> <playername> <amount>");

        String stockName = args[1].toUpperCase();
        stockName = stockName.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stockName), "business name is invalid");

        String player_name = args[2];
        OfflinePlayer playerOfflineName = null;

        for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
            if (target.getName().equals(player_name)){
                playerOfflineName = target;
            }
        }

        CommandAssert.assertTrue(playerOfflineName != null, "player doesn't exist");
        CommandAssert.assertTrue(Market.hasAccount(playerOfflineName.getUniqueId()), "player doesn't have an account");

        int amount = CommandTools.parseInteger(args[3], "Please enter a valid positive integer as amount");

        if (firstEnter){
            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to transfer §f" +amount+SignClick.getPrefix()+" shares to §f"+ player_name+
                    "\n§c/company transfer "+stockName+" "+player_name+" "+amount);
            return true;
        }

        Account target = Market.getAccount(playerOfflineName.getUniqueId());
        boolean suc6 = Market.getAccount(player).transfer(stockName, amount, target, player);
        if (suc6){
            Market.getCompany(stockName).getCOM().changeShareHolder(target, amount);
            Market.getCompany(stockName).getCOM().changeShareHolder(Market.getAccount(player), -amount);

            Market.getCompany(stockName).update("Shares transferred",
                    "§7Player transferred " + amount + " shares to " + playerOfflineName.getName(),
                    player.getUniqueId());
        }

        return false;
    }
}
