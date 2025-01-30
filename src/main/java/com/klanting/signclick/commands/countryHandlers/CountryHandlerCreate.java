package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CountryHandlerCreate extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(args.length >= 3, "§bplease enter /country create <name> <owner>");

        String countryName = args[1];
        OfflinePlayer user = Bukkit.getServer().getOfflinePlayer(args[2]);

        CommandAssert.assertTrue(!CountryManager.getCountriesString().contains(countryName), "§bthis country already exists");

        CommandAssert.assertTrue(CountryManager.getCountry(user) == null, "§bplayer cannot join a new country, because he/she is already part of one");

        CountryManager.create(countryName, player, user);
    }
}
