package com.klanting.signclick.commands.countryHandlers.staffHandler;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CountryHandlerColor extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bPlease enter /country color <country> <color>");

        Country country = CountryManager.getCountry(args[1]);

        CommandAssert.assertTrue(country != null, "§bThe country "+args[1]+" does not exists");

        try {
            country.setColor(ChatColor.valueOf(args[2]));
            player.sendMessage("§bColor has been changed to "+args[2].toUpperCase());
        }catch (IllegalArgumentException e){
            player.sendMessage("§bColor "+args[2].toUpperCase()+" is not a valid color");
        }
    }
}
