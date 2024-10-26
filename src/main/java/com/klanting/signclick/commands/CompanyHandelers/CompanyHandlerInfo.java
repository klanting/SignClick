package com.klanting.signclick.commands.CompanyHandelers;

import com.klanting.signclick.commands.Exceptions.CommandAssert;
import com.klanting.signclick.commands.Exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;
import org.gradle.internal.impldep.com.esotericsoftware.kryo.io.Input;

public class CompanyHandlerInfo extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company info <stockname>");

        String stock_name = args[1].toUpperCase();
        if (Market.hasBusiness(stock_name)){
            Market.getBusiness(stock_name).info(player);;
        }else{
            player.sendMessage("§bplease enter a valid company stockname");
        }
        return false;
    }
}
