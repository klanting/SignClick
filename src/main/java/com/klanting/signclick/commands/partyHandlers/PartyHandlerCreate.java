package com.klanting.signclick.commands.partyHandlers;

import com.klanting.signclick.commands.CommandTools;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;


public class PartyHandlerCreate extends PartyHandler {
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        CommandAssert.assertTrue(args.length >= 2,  "§bPlease enter /party create <name>");

        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null,  "§bYou need to be in a country");
        CommandAssert.assertTrue(!country.isForbidParty(), "§bCountry forbids create party");
        CommandAssert.assertTrue(!country.inParty(player.getUniqueId()), "§bYou are already in a party");
        CommandAssert.assertTrue(!country.hasPartyName(args[1]), "§bParty name already exists");

        String partyName = CommandTools.parseString(args[1], "§bPlease use allowed characters for the Party name");

        country.createParty(partyName, player.getUniqueId());
        country.addStability(3.0);
        player.sendMessage("§bParty created");
    }
}