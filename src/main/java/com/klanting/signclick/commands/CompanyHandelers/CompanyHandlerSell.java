package com.klanting.signclick.commands.CompanyHandelers;

import com.klanting.signclick.commands.Exceptions.CommandAssert;
import com.klanting.signclick.commands.Exceptions.CommandException;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerSell extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bplease enter /company sell <stockname> <amount>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();
        int amount = Integer.parseInt(args[2]);

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bplease enter a valid company stockname");

        if (firstEnter){
            double v = Market.getSellPrice(stock_name, amount);
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sell §f" +amount+
                    "§b from §f"+ stock_name+"§b for a price of §6"+df.format(v)+" \n§c/company sell "+stock_name+" "+amount);
            return true;
        }

        Account acc = Market.getAccount(player);
        acc.sellShare(stock_name, amount, player);
        
        return false;
    }
}
