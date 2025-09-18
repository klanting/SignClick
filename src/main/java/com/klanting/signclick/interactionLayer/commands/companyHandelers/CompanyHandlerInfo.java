package com.klanting.signclick.interactionLayer.commands.companyHandelers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Market;
import org.bukkit.entity.Player;

public class CompanyHandlerInfo extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company info <stockname>");

        String stock_name = args[1].toUpperCase();
        if (Market.hasBusiness(stock_name)){
            Market.getCompany(stock_name).info(player);;
        }else{
            player.sendMessage("§bplease enter a valid company stockname");
        }
        return false;
    }
}
