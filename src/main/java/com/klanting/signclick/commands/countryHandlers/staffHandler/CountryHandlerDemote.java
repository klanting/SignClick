package com.klanting.signclick.commands.countryHandlers.staffHandler;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CountryHandlerDemote extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bPlease enter /country demote <username>");

        try{
            Player p = Bukkit.getPlayer(args[1]);
            Country country = CountryManager.getCountry(p);
            country.removeOwner(p);
            country.addMember(p);
        }catch (Exception e){

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            CommandAssert.assertTrue(target != null, "§bTarget player not found");

            Country country = CountryManager.getCountry(target);
            country.removeOwner(target);
            country.addMember(target);

        }
    }
}
