package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CountryHandlerRemoveEnforcement extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /country remove_enforcement <player>");

        String player_name = args[1];

        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country.isOwner(player), "§byou are not country owner");

        Player target = Bukkit.getPlayer(player_name);
        if (target != null){
            country.removeLawEnforcement(target);
            player.sendMessage("§byou succesfully resigned an law enforcement agent");
        }else{
            for (OfflinePlayer op: Bukkit.getOfflinePlayers()){
                if (op.getName().equals(player_name)){
                    country.removeLawEnforcement(op);
                    break;
                }
            }
            player.sendMessage("§byou succesfully resigned an law enforcement agent");
        }
    }
}
