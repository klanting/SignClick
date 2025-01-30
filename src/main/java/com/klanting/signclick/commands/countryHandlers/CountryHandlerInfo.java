package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerInfo extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country;
        if (args.length == 2){
            String name = args[1];
            country = CountryManager.getCountry(name);
        }else{
            country = CountryManager.getCountry(player);

        }

        CommandAssert.assertTrue(country != null, "§bprovided country is invalid, or the player did not specify a country name, while also not being inside one");

        country.info(player);
    }
}
