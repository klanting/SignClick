package com.klanting.signclick.interactionLayer.commands.countryHandlers.staffHandler;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountryHandlerSetOwner extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bPlease enter /country setowner <country> <username>");

        Player p = Bukkit.getPlayer(args[2]);
        assert p != null;

        Country country = CountryManager.getCountry(args[1]);
        CommandAssert.assertTrue(country != null, "§bThe country "+args[1]+" does not exists");
        boolean suc6 = country.addOwner(p);

        if (suc6){
            p.sendMessage("§bYou are added as owner");
        }else{
            p.sendMessage("§bYou are already an owner");
        }

        player.sendMessage("§bOwner has been set");
    }
}
