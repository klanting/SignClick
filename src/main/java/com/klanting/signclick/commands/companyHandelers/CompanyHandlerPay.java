package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CompanyHandlerPay extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 4, "§bplease enter /company pay <company> <player_name> <amount>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();
        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bbusiness name is invalid");

        String player_name = args[2];
        double amount = Double.parseDouble(args[3]);
        OfflinePlayer player_offline = Bukkit.getOfflinePlayer(player_name);

        CommandAssert.assertTrue(Market.getBusiness(stock_name).isOwner(player.getUniqueId()),
                "§byou must be a CEO of this company");

        CommandAssert.assertTrue(!player.getName().equals(player_name),
                "§byou can't pay out yourself");

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to pay §f" +amount+
                    "§b to §f"+ player_name+"\n§c/company pay "+stock_name+" "+player_name+" "+amount);
            return true;
        }

        if (Market.getBusiness(stock_name).removeBal(amount)){
            SignClick.getEconomy().depositPlayer(player_offline, amount);
            player.sendMessage("§bsuccesfully paid §f"+player_name+" "+amount);
            Player target = Bukkit.getPlayer(player_offline.getUniqueId());
            if (target != null){
                target.sendMessage("§bsuccesfully received §f"+amount+" §bfrom §f"+stock_name);
            }

        }else{
            player.sendMessage("§bbusiness does not have enough money, or you reached your monthly spending limit\ndo §c/company spendable "+
                    stock_name+"§b to see monthly available money");
        }

        return false;
    }
}
