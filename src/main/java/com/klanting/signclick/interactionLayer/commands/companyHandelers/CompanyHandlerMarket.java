package com.klanting.signclick.interactionLayer.commands.companyHandelers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.interactionLayer.menus.company.MarketSelector;
import org.bukkit.entity.Player;

public class CompanyHandlerMarket extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        MarketSelector screen = new MarketSelector(player.getUniqueId());
        player.openInventory(screen.getInventory());

        return false;
    }
}
