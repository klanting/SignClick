package com.klanting.signclick.interactionLayer.commands.countryHandlers.staffHandler;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CountryHandlerColor extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bPlease enter /country color <country> <color>");

        Country country = CountryManager.getCountry(args[1]);

        CommandAssert.assertTrue(country != null, "§bThe country "+args[1]+" does not exists");

        String colorString = args[2].toUpperCase();

        try {
            country.setColor(ChatColor.valueOf(colorString));
            player.sendMessage("§bColor has been changed to "+colorString);
        }catch (IllegalArgumentException e){
            player.sendMessage("§bColor "+colorString+" is not a valid color");
        }
    }
}
