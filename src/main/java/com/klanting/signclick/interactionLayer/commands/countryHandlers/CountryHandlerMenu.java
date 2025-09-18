package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.interactionLayer.menus.country.Menu;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerMenu extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bYou need to be in a country to do this");
        CommandAssert.assertTrue(country.isOwner(player), "§bplayer is not the owner");

        Menu screen = new Menu(player.getUniqueId());
        player.openInventory(screen.getInventory());
    }
}
