package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.Menus.CompanySelector;
import com.klanting.signclick.commands.exceptions.CommandException;
import org.bukkit.entity.Player;

public class CompanyHandlerMenu extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CompanySelector screen = new CompanySelector(player.getUniqueId());
        player.openInventory(screen.getInventory());

        return false;
    }
}
