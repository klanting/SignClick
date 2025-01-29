package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

public class CompanyHandlerSharetop extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company sharetop <stockname>");

        String stock_name = args[1].toUpperCase();

        if (Market.hasBusiness(stock_name)){
            Market.getCompany(stock_name).getShareTop(player);
        }else{
            player.sendMessage("§bplease enter a valid company stockname");
        }
        return false;
    }
}
