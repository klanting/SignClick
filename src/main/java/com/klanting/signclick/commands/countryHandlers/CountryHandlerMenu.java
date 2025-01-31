package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.Menus.CountryMenu;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerMenu extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country.isOwner(player), "§bplayer is not the owner");

        CountryMenu screen = new CountryMenu(player.getUniqueId());
        player.openInventory(screen.getInventory());
    }
}
