package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.menus.company.Selector;
import com.klanting.signclick.commands.exceptions.CommandException;
import org.bukkit.entity.Player;

public class CompanyHandlerMenu extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        Selector screen = new Selector(player.getUniqueId(), "menu");
        player.openInventory(screen.getInventory());

        return false;
    }
}
