package com.klanting.signclick.commands.companyHandelers;

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

        CommandAssert.assertTrue(args.length >= 4, "§bplease enter /company transfer <company> <player_name> <amount>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bbusiness name is invalid");

        String player_name = args[2];
        OfflinePlayer player_offline = null;

        for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
            if (target.getName().equals(player_name)){
                player_offline = target;
            }
        }

        CommandAssert.assertTrue(player_offline != null, "§bplayer doesn't exist");
        CommandAssert.assertTrue(Market.hasAccount(player_offline.getUniqueId()), "§bplayer doesn't have an account");

        int amount = Integer.parseInt(args[3]);

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to transfer §f" +amount+"§b shares to §f"+ player_name+
                    "\n§c/company transfer "+stock_name+" "+player_name+" "+amount);
            return true;
        }

        Account target = Market.getAccount(player_offline.getUniqueId());
        boolean suc6 = Market.getAccount(player).transfer(stock_name, amount, target, player);
        if (suc6){
            Market.getCompany(stock_name).getCOM().changeShareHolder(target, amount);
            Market.getCompany(stock_name).getCOM().changeShareHolder(Market.getAccount(player), -amount);
        }

        return false;
    }
}
