package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerSell extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bplease enter /company sell <stockname> <amount>");

        String stockName = args[1].toUpperCase();
        stockName = stockName.toUpperCase();
        int amount = Integer.parseInt(args[2]);

        CommandAssert.assertTrue(Market.hasBusiness(stockName), "§bplease enter a valid company stockname");

        if (firstEnter){
            double v = Market.getSellPrice(stockName, amount);
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sell §f" +amount+
                    "§b from §f"+ stockName+"§b for a price of §6"+df.format(v)+" \n§c/company sell "+stockName+" "+amount);
            return true;
        }

        Account acc = Market.getAccount(player);
        acc.sellShare(stockName, amount, player);

        return false;
    }
}
