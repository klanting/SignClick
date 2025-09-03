package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.CountryCommands;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;

import static com.klanting.signclick.events.CountryEvents.sortTab;

public class CountryHandlerAccept extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(CountryCommands.countryInvites.containsKey(player.getName()), "§bNo pending invites");

        String countryName = CountryCommands.countryInvites.get(player.getName());
        Country country = CountryManager.getCountry(countryName);
        country.addMember(player);
        player.sendMessage("§byou succesfully joint this country");
        player.setPlayerListName(country.getColor()+player.getName());
        sortTab();
    }
}
