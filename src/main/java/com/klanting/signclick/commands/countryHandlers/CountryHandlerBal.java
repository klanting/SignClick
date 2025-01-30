package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CountryHandlerBal extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        DecimalFormat df = new DecimalFormat("###,###,###");

        double balance;
        if (args.length == 2){
            balance = CountryManager.getCountry(args[1]).getBalance();
        }else{
            balance = CountryManager.getCountry(player).getBalance();
        }
        player.sendMessage("§bsaldo: "+df.format(balance));
    }
}
