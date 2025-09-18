package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerSetSpawn extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bYou need to be in a country to do this");
        CommandAssert.assertTrue(country.isOwner(player), "§bYou don't have the permissions to set the spawn");

        country.setSpawn(player.getLocation());

        player.sendMessage("§bspawn succesfully relocated");
    }
}
