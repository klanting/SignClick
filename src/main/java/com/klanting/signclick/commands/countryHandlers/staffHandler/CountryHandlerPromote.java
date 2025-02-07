package com.klanting.signclick.commands.countryHandlers.staffHandler;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CountryHandlerPromote extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bPlease enter /country promote <username>");

        try{
            Player p = Bukkit.getPlayer(args[1]);

            Country country = CountryManager.getCountry(p);
            CommandAssert.assertTrue(country != null, "§bThe country "+args[1]+" does not exists");
            country.removeMember(p);
            boolean suc6 = country.addOwner(p);
            if (suc6){
                p.sendMessage("you are promoted to owner");
            }else{
                p.sendMessage("you are already owner, and so cannot be promoted");
            }

        }catch (Exception e){
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            CommandAssert.assertTrue(target != null, "§bTarget player not found");

            Country country = CountryManager.getCountry(target);
            CommandAssert.assertTrue(country != null, "§bThe country "+args[1]+" does not exists");
            country.removeMember(target);
            country.addOwner(target);

        }
    }
}
