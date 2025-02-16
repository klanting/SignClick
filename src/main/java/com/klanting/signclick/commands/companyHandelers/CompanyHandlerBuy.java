package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.CommandTools;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerBuy extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bplease enter /company buy <stockname> <amount>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();
        int amount = CommandTools.parseInteger(args[2], "§bPlease enter a valid integer as amount");

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
