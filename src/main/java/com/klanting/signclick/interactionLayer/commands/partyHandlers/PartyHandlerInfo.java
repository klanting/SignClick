package com.klanting.signclick.interactionLayer.commands.partyHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.parties.Party;
import org.bukkit.entity.Player;

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