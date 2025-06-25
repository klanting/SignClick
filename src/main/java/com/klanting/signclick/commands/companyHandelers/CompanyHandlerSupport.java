package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.menus.company.Selector;
import org.bukkit.entity.Player;

public class CompanyHandlerSupport extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        Selector screen = new Selector(player.getUniqueId(), "support");
        player.openInventory(screen.getInventory());

        return false;
    }
}
