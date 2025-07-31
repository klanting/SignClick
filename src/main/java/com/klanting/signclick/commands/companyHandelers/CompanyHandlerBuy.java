package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.SignClick;
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
        CommandAssert.assertTrue(args.length >= 3, "please enter /company buy <stockname> <amount>");

        String stockName = args[1].toUpperCase();
        stockName = stockName.toUpperCase();
        int amount = CommandTools.parseInteger(args[2], "Please enter a valid integer as amount");

        CommandAssert.assertTrue(Market.hasBusiness(stockName), "please enter a valid company stockname");

        if (firstEnter){
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to buy §f" +amount+
                    SignClick.getPrefix()+" from §f"+ stockName+" for a price of §6"+df.format(Market.getBuyPrice(stockName, amount))+" \n§c/company buy "+stockName+" "+amount);

            return true;
        }

        Account acc = Market.getAccount(player);
        acc.buyShare(stockName, amount, player);

        return false;
    }
}
