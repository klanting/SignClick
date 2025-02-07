package com.klanting.signclick.commands.countryHandlers.staffHandler;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountryHandlerAddMember extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(args.length >= 3, "§bPlease enter /country addmember <country> <username>");

        Country country = CountryManager.getCountry(args[1]);
        CommandAssert.assertTrue(country != null, "§bThe country "+args[1]+" does not exists");

        Player addedPlayer = Bukkit.getPlayer(args[2]);
        country.addMember(addedPlayer);
        player.sendMessage("§bplayer succesfully joint this country");

        addedPlayer.setPlayerListName(country.getColor()+player.getName());
    }
}
