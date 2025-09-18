package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.CommandTools;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerCreate extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /country create <name>");

        String countryName = CommandTools.parseString(args[1], "§bPlease use allowed characters for the Country name");

        CommandAssert.assertTrue(!CountryManager.getCountriesString().contains(countryName), "§bthis country already exists");
        CommandAssert.assertTrue(CountryManager.getCountry(player) == null, "§bPlayer cannot create a new country, because he/she is already part of one");

        CountryManager.create(countryName, player);
    }
}
