package com.klanting.signclick.commands.countryHandlers.staffHandler;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;

import static com.klanting.signclick.events.CountryEvents.sortTab;

public class CountryHandlerRemoveMember extends CountryStaffHandler{
    @Override
    public void handleStaffCommand(Player player, String[] args) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bPlease enter /country removemember <country> <username>");

        Country country = CountryManager.getCountry(args[1]);
        CommandAssert.assertTrue(country != null, "§bThe country "+args[1]+" does not exists");

        Player removedPlayer = Objects.requireNonNull(Bukkit.getPlayer(args[2]));

        country.removeMember(removedPlayer);
        player.sendMessage("§bplayer succesfully left this country");
        removedPlayer.setPlayerListName(ChatColor.WHITE+player.getName());
        sortTab();
    }
}
