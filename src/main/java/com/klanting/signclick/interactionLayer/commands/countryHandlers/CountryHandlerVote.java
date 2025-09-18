package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.interactionLayer.menus.country.ElectionMenu;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import com.klanting.signclick.logicLayer.parties.Election;
import org.bukkit.entity.Player;

public class CountryHandlerVote extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);
        CommandAssert.assertTrue(country != null, "§bYou need to be in a country to do this");
        CommandAssert.assertTrue(country.getCountryElection() != null, "§bcountry is not in an election phase");
        Election e = country.getCountryElection();
        CommandAssert.assertTrue(!e.alreadyVoted.contains(player.getUniqueId()), "§byou can`t vote twice");

        ElectionMenu screen = new ElectionMenu(e, player.getUniqueId());
        player.openInventory(screen.getInventory());
    }
}
