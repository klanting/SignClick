package com.klanting.signclick.interactionLayer.commands.companyHandelers;

import com.klanting.signclick.interactionLayer.commands.CommandTools;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import org.bukkit.entity.Player;

public class CompanyHandlerBalTop extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        int page = 1;
        if (args.length >= 2){
            page = CommandTools.parseInteger(args[1], "Please enter a valid integer as amount");
        }



        Market.getMarketValueTop(player, page);
        return false;
    }
}
