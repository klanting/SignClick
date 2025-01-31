package com.klanting.signclick.commands.countryHandlers.staffHandler;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerRemove extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bPlease enter /country remove <country>");

        String name = args[1];
        Country country = CountryManager.getCountry(name);
        CountryManager.delete(country.getName(), player);
    }
}
