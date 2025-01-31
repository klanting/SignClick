package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Election;
import org.bukkit.entity.Player;

import static com.klanting.signclick.economy.parties.ElectionTools.setupElectionDeadline;

public class CountryHandlerElection extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country.isOwner(player), "§bplayer is not the owner");

        CommandAssert.assertTrue(country.getCountryElection() == null, "§bcountry is already in an election phase");

        long system_end = System.currentTimeMillis()/1000 + 60*60*24*7;
        country.addStability(15.0);
        player.sendMessage("§belections started");

        country.setCountryElection(new Election(country.getName(), system_end));

        long time = 60*20*60*24*7L;
        setupElectionDeadline(country, time);
    }
}
