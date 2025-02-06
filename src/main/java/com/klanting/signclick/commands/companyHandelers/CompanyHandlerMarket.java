package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.Menus.CompanyMarketMenu;
import com.klanting.signclick.Menus.CompanyMarketSelector;
import com.klanting.signclick.Menus.CompanySelector;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

public class CompanyHandlerMarket extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CompanyMarketSelector screen = new CompanyMarketSelector(player.getUniqueId());
        player.openInventory(screen.getInventory());

        return false;
    }
}
