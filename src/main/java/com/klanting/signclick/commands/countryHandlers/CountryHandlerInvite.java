package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.CountryCommands;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountryHandlerInvite extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country.isOwner(player), "§byou are not allowed to do this");

        String username;
        try{
            username = args[1];
        }catch (Exception e){
            player.sendMessage("§bplease enter /country invite <username>");
            return;
        }

        CountryCommands.countryInvites.put(username, country.getName());

        Player p = Bukkit.getPlayer(username);
        CommandAssert.assertTrue(p != null, "§bthe invite was unable to arrive at the player");


        p.sendMessage("§byou have an invite for §8"+country.getName()+ " §byou have 120s for accepting by \n" +
                "§c/country accept");

        Bukkit.getServer().getScheduler().runTaskLater(SignClick.getPlugin(), new Runnable() {
            public void run() {
                CountryCommands.countryInvites.remove(username);
            }
        }, 20*120L);

        player.sendMessage("§bthe invite to join the country has been send to "+username);
    }
}
