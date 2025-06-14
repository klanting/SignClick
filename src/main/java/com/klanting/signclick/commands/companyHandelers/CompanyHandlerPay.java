package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.CommandTools;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerPay extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 4, "§bplease enter /company pay <stockname> <playername> <amount>");

        String stockName = args[1].toUpperCase();
        stockName = stockName.toUpperCase();
        CommandAssert.assertTrue(Market.hasBusiness(stockName), "§bbusiness name is invalid");

        String playerName = args[2];

        double amount = CommandTools.parseDouble(args[3], "§bPlease enter a valid double as amount");
        OfflinePlayer player_offline = Bukkit.getOfflinePlayer(playerName);

        CommandAssert.assertTrue(Market.getCompany(stockName).getCOM().isOwner(player.getUniqueId()),
                "§byou must be a CEO of this company");

        CommandAssert.assertTrue(!player.getName().equals(playerName),
                "§byou can't pay out yourself");

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to pay §f" +amount+
                    "§b to §f"+ playerName+"\n§c/company pay "+stockName+" "+playerName+" "+amount);
            return true;
        }

        if (Market.getCompany(stockName).removeBal(amount)){
            SignClick.getEconomy().depositPlayer(player_offline, amount);

            player.sendMessage("§bsuccesfully paid §f"+playerName+" "+amount);
            Player target = Bukkit.getPlayer(player_offline.getUniqueId());

            /*
             * Add log of payment
             * */
            DecimalFormat df = new DecimalFormat("###,###,###");
            Market.getCompany(stockName).update("Balance removed",
                    "§cCompany paid "+ df.format(amount) + " to " +playerName, player.getUniqueId());

            if (target != null){
                target.sendMessage("§bsuccesfully received §f"+amount+" §bfrom §f"+stockName);
            }

        }else{
            player.sendMessage("§bbusiness does not have enough money, or you reached your monthly spending limit\ndo §c/company spendable "+
                    stockName+"§b to see monthly available money");
        }

        return false;
    }
}
