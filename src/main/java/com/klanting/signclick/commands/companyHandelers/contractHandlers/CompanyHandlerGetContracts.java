package com.klanting.signclick.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

public class CompanyHandlerGetContracts extends CompanyHandler {
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company get_contracts <stockname>");

        String stockName = args[1].toUpperCase();
        stockName = stockName.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stockName), "§bbusiness name is invalid");

        Market.getContracts(stockName, player);

        return false;
    }
}
