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

public class PartyHandlerPromote extends PartyHandler {
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bYou need to be in a country");

        CommandAssert.assertTrue(args.length >= 2, "§bPlease enter /party promote <username>");

        String player_name = args[1];
        Player target_player = Bukkit.getPlayer(player_name);

        Party p = country.getParty(player.getUniqueId());
        CommandAssert.assertTrue(p != null, "§bYou must be in a party");
        CommandAssert.assertTrue(p.isOwner(player.getUniqueId()), "§bYou must be party owner");

        p.promote(target_player.getUniqueId());
        player.sendMessage("§bPlayer is promoted");
    }
}