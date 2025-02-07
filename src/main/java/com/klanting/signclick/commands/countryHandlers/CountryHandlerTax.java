package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerTax extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        int amount;
        try{
            amount = Integer.parseInt(args[1]);
        }catch (Exception e){
            player.sendMessage("§bplease enter /country tax <amount>");
            return;
        }

        Country country = CountryManager.getCountry(player);
        CommandAssert.assertTrue(country != null, "§bYou need to be in a country to do this");

        CommandAssert.assertTrue(country.isOwner(player), "§byou are not allowed to do this");

        CommandAssert.assertTrue(0 <= amount && amount <= 20, "§bpls enter an integer from 0 to 20");

        country.setTaxRate(amount/100.0);
        player.sendMessage("§bthe tax has been changed");
    }
}
