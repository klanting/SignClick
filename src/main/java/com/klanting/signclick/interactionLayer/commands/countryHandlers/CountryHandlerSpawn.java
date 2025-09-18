package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CountryHandlerSpawn extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country;
        if ((player.hasPermission("signclick.staff")) && (args.length == 2)){
            String countryName = args[1];
            country = CountryManager.getCountry(countryName);
        }else{
            country = CountryManager.getCountry(player);
        }

        CommandAssert.assertTrue(country != null, "§byou are not in a country");

        Location loc = country.getSpawn();

        CommandAssert.assertTrue(loc != null, "§bno country spawn has been set, owners can set it by entering /country setspawn");

        player.teleport(loc);
        player.sendMessage("§bteleported to country spawn");
    }
}
