package com.klanting.signclick.interactionLayer.commands.partyHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PartyHandlerAdd extends PartyHandler {
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bYou need to be in a country");

        CommandAssert.assertTrue(args.length >= 2, "§bPlease enter /party add <username>");

        String player_name = args[1];

        Player target_player = Bukkit.getPlayer(player_name);

        CommandAssert.assertTrue(country.getName() == CountryManager.getCountry(player).getName(),
                "§bPlayer is in a different country");

        Party p = country.getParty(player.getUniqueId());
        Party p2 = country.getParty(target_player.getUniqueId());

        CommandAssert.assertTrue(p != null, "§bYou must be in a party");
        CommandAssert.assertTrue(p2 == null, "§bTarget already in a party");
        CommandAssert.assertTrue(p.isOwner(player.getUniqueId()), "§bYou must be party owner");

        p.addMember(target_player.getUniqueId());
        player.sendMessage("§bPlayer added to the party");
    }
}