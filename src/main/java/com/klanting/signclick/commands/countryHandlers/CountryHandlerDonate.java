package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountryHandlerDonate extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country;
        if (args.length == 3){
            country = CountryManager.getCountry(args[1]);
        }else{
            country = CountryManager.getCountry(player);
        }

        CommandAssert.assertTrue(country != null,
                SignClick.getPrefix()+"You are not in a country or your designated country does not exist");



        int amount;
        try{
            amount = Integer.parseInt(args[args.length-1]);
        }catch (Exception e){
            player.sendMessage(SignClick.getPrefix()+"please enter /country donate [country] <amount>");
            return;
        }

        CommandAssert.assertTrue(amount >= 0, "§bYou cannot donate negative amounts");
        CommandAssert.assertTrue(SignClick.getEconomy().has(player, amount), "§bYou have not enough money");

        country.deposit(amount);
        SignClick.getEconomy().withdrawPlayer(player, amount);
        player.sendMessage(SignClick.getPrefix()+"You paid " + amount + " to " + country.getName());

        for (Player pl : Bukkit.getServer().getOnlinePlayers()){

            if (country.isOwner(pl)){
                pl.sendMessage(SignClick.getPrefix()+player.getName()+" donated "+amount + " to your country");
            }
        }
    }
}
