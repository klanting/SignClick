package com.klanting.signclick.commands.CompanyHandelers;

import com.klanting.signclick.commands.Exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

public class CompanyHandlerBalTop extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        Market.getMarketValueTop(player);
        return false;
    }
}
