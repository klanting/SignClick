package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CountryHandlerBaltop extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        String line = "§bBaltop:§0";
        int index = 1;

        for (Country country : CountryManager.getTop()){
            if (index <= 10){
                int amount = country.getBalance();
                DecimalFormat df = new DecimalFormat("###,###,###");
                line += "\n";
                line += "§b"+index+".§3 ";
                line += country.getName();
                line += ": §7";
                line += df.format(amount);

                index += 1;
            }

        }
        player.sendMessage(line);
    }
}
