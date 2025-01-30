package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;

public class CountryHandlerLeave extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        if (country.isOwner(player)){
            country.removeOwner(player);
        }else{
            country.removeMember(player);
        }
        player.sendMessage("§bcountry succesfully left");
    }
}
