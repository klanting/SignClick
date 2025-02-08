package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.menus.CompanyMarketSelector;
import com.klanting.signclick.commands.exceptions.CommandException;
import org.bukkit.entity.Player;

public class CompanyHandlerMarket extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CompanyMarketSelector screen = new CompanyMarketSelector(player.getUniqueId());
        player.openInventory(screen.getInventory());

        return false;
    }
}
