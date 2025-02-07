package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountryHandlerAddEnforcement extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /country add_enforcement <player>");
        String player_name = args[1];

        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bYou need to be in a country to add law enforcement");
        CommandAssert.assertTrue(country.isOwner(player), "§byou are not country owner");

        Player target = Bukkit.getPlayer(player_name);

        CommandAssert.assertTrue(country.getMembers().contains(target.getUniqueId()), "§bOnly country members can be law enforcement");

        CommandAssert.assertTrue(target != null, "§bassigning failed");

        country.addLawEnforcement(target);
        player.sendMessage("§byou succesfully assigned an law enforcement agent");
    }
}
