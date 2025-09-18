package com.klanting.signclick.interactionLayer.commands.companyHandelers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.interactionLayer.menus.company.BoardMenu;
import com.klanting.signclick.interactionLayer.menus.company.SelectionType;
import com.klanting.signclick.interactionLayer.menus.company.Selector;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Function;

public class CompanyHandlerSupport extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        Function<CompanyI, Void> func = (comp) -> {
            InventoryHolder screen = new BoardMenu(comp);
            player.openInventory(screen.getInventory());
            return null;
        };

        Selector screen = new Selector(player.getUniqueId(), func, SelectionType.Shares);
        player.openInventory(screen.getInventory());

        return false;
    }
}
