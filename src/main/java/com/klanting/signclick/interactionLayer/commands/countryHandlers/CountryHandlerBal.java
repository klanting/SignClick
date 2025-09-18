package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CountryHandlerBal extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        DecimalFormat df = new DecimalFormat("###,###,###");

        Country country;
        if (args.length == 2){
            country = CountryManager.getCountry(args[1]);
        }else{
            country = CountryManager.getCountry(player);
        }

        CommandAssert.assertTrue(country != null, "§bYou need to have a valid country, either provide one or join one");

        double balance = country.getBalance();

        player.sendMessage("§bsaldo: "+df.format(balance));
    }
}
