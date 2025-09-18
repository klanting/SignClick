package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.CountryCommands;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.entity.Player;

import static com.klanting.signclick.interactionLayer.events.CountryEvents.sortTab;

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
