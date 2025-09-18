package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import com.klanting.signclick.logicLayer.parties.Election;
import org.bukkit.entity.Player;
import versionCompatibility.CompatibleLayer;

import static com.klanting.signclick.logicLayer.parties.ElectionTools.setupElectionDeadline;

public class CountryHandlerElection extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bYou need to be in a country to start an election");
        CommandAssert.assertTrue(country.isOwner(player), "§bplayer is not the owner");

        CommandAssert.assertTrue(country.getCountryElection() == null, "§bcountry is already in an election phase");

        long electionEnd = CompatibleLayer.getCurrentTick() + SignClick.getConfigManager().getConfig("countries.yml").getLong("electionTime")*20L;
        country.addStability(15.0);
        player.sendMessage("§belections started");

        country.setCountryElection(new Election(country.getName(), electionEnd));

        setupElectionDeadline(country);
    }
}
