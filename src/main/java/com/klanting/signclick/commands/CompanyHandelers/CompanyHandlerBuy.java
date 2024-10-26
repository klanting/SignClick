package com.klanting.signclick.commands.CompanyHandelers;

import com.klanting.signclick.commands.Exceptions.CommandAssert;
import com.klanting.signclick.commands.Exceptions.CommandException;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerBuy extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company buy <stockname> <amount>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();
        int amount = Integer.parseInt(args[2]);

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bplease enter a valid company stockname");

        if (firstEnter){
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to buy §f" +amount+
                    "§b from §f"+ stock_name+" for a price of §6"+df.format(Market.getBuyPrice(stock_name, amount))+" \n§c/company buy "+stock_name+" "+amount);

            return true;
        }

        Account acc = Market.getAccount(player);
        acc.buyShare(stock_name, amount, player);
        return false;
    }
}
