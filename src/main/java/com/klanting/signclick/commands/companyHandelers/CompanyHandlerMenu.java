package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.menus.company.OwnerMenu;
import com.klanting.signclick.menus.company.Selector;
import com.klanting.signclick.commands.exceptions.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CompanyHandlerMenu extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        Function<Company, Void> func = (comp) -> {
            InventoryHolder screen = new OwnerMenu(player.getUniqueId(), comp);
            player.openInventory(screen.getInventory());
            return null;
        };


        Selector screen = new Selector(player.getUniqueId(), func);
        player.openInventory(screen.getInventory());

        return false;
    }
}
