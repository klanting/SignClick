package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CountryHandlerPay extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        int amount;
        String p;
        try{
            amount = Integer.parseInt(args[2]);
            p = args[1];

        }catch (Exception e){
            player.sendMessage("§bplease enter /country pay <player> <amount>");
            return;
        }

        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bplease enter a valid country");
        CommandAssert.assertTrue(country.isOwner(player), "§bthis command can only be executed by a country owner");
        CommandAssert.assertTrue(country.has(amount), "§byou have not enough money");
        CommandAssert.assertTrue(amount >= 0, "§bYou cannot pay negative amounts");
        CommandAssert.assertTrue(!player.getName().equals(p), "§byou cannot pay yourself");

        try {
            Player target = Bukkit.getServer().getPlayer(p);
            SignClick.getEconomy().depositPlayer(target, amount);
            target.sendMessage("§byou got " + amount + " from " + country.getName());

        } catch (Exception e) {
            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(p);
            SignClick.getEconomy().depositPlayer(target, amount);
        }
        country.withdraw(amount);
        player.sendMessage("§byou paid " + amount + " to " + p);
    }
}
