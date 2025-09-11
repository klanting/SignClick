package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.menus.company.BoardMenu;
import com.klanting.signclick.menus.company.SelectionType;
import com.klanting.signclick.menus.company.Selector;
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
