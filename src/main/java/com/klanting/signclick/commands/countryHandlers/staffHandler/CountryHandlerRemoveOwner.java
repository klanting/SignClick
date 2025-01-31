package com.klanting.signclick.commands.countryHandlers.staffHandler;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountryHandlerRemoveOwner extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bPlease enter /country removeowner <country> <username>");

        Player p = Bukkit.getPlayer(args[2]);
        assert p != null;
        Country country = CountryManager.getCountry(args[1]);
        country.removeOwner(p);
        player.sendMessage("§bowner has been set");
    }
}
