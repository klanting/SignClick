package com.klanting.signclick.commands.partyHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyHandlerInfo extends PartyHandler {
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Party p;
        Country country = CountryManager.getCountry(player);
        if (args.length == 2){
            CommandAssert.assertTrue(country != null, "§bYou need to be in a country");
            p = country.getParty(args[1]);

        }else if (args.length >= 3){
            country = CountryManager.getCountry(args[1]);
            CommandAssert.assertTrue(country != null, "§bYou need to be in a country");
            p = country.getParty(args[2]);
        }else{
            CommandAssert.assertTrue(country != null, "§bYou need to be in a country");
            p = country.getParty(player.getUniqueId());
        }

        CommandAssert.assertTrue(p != null, "§bParty does not exist");

        p.info(player);
    }
}