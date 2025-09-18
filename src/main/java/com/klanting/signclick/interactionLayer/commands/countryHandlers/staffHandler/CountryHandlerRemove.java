package com.klanting.signclick.interactionLayer.commands.countryHandlers.staffHandler;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerRemove extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bPlease enter /country remove <country>");

        String name = args[1];
        Country country = CountryManager.getCountry(name);
        CommandAssert.assertTrue(country != null, "§bThe country "+args[1]+" does not exists");
        CountryManager.delete(country.getName(), player);
    }
}
