package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerSupport extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 3, "§bplease enter /company support <company> <player_name>");

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

        if (player_name.equals("neutral")){
            Account acc = Market.getAccount(player);
            Market.getBusiness(stock_name).supportUpdate(acc, null);
            player.sendMessage("§bsupport changed to §e"+player_name);
            return false;
        }

        CommandAssert.assertTrue(player_offline != null, "§bplayer doesn't exist");

        Account acc = Market.getAccount(player);
        Market.getBusiness(stock_name).supportUpdate(acc, player_offline.getUniqueId());
        player.sendMessage("§bsupport changed to §f"+player_name);
        return false;
    }
}
