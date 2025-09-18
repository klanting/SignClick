package com.klanting.signclick.interactionLayer.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.CommandTools;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Account;
import com.klanting.signclick.logicLayer.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerSell extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "please enter /company sell <stockname> <amount>");

        String stockName = args[1].toUpperCase();
        stockName = stockName.toUpperCase();
        int amount = CommandTools.parseInteger(args[2], "Please enter a valid positive integer as amount");

        CommandAssert.assertTrue(Market.hasBusiness(stockName), "please enter a valid company stockname");

        if (firstEnter){
            double v = Market.getSellPrice(stockName, amount);
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to sell §f" +amount+
                    SignClick.getPrefix()+" from §f"+ stockName+SignClick.getPrefix()+" for a price of §6"+df.format(v)+" \n§c/company sell "+stockName+" "+amount);
            return true;
        }

        Account acc = Market.getAccount(player);
        acc.sellShare(stockName, amount, player);

        return false;
    }
}
