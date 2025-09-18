package com.klanting.signclick.interactionLayer.commands.partyHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.countryLogic.parties.Party;
import org.bukkit.entity.Player;

public class PartyHandlerLeave extends PartyHandler {
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bYou need to be in a country");

        Party p = country.getParty(player.getUniqueId());

        CommandAssert.assertTrue(p != null, "§bYou must be in a party");

        p.removeMember(player.getUniqueId());
        player.sendMessage("§bYou left the party");
    }
}