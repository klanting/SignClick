package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.Menus.CountryElectionMenu;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Election;
import org.bukkit.entity.Player;

public class CountryHandlerVote extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country.getCountryElection() != null, "§bcountry is not in an election phase");
        Election e = country.getCountryElection();
        CommandAssert.assertTrue(!e.alreadyVoted.contains(player.getUniqueId()), "§byou can`t vote twice");

        CountryElectionMenu screen = new CountryElectionMenu(e);
        player.openInventory(screen.getInventory());
    }
}
