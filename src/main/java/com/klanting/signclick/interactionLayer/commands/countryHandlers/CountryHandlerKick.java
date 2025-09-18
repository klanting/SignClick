package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CountryHandlerKick extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null && country.isOwner(player), "§byou are not allowed to kick members");

        Player target;
        try{
            target = Bukkit.getServer().getPlayer(args[1]);
        }catch (Exception e){
            player.sendMessage("§bplease enter /country kick <> player");
            return;
        }

        country.removeMember(target);
        player.sendMessage("§btarget has been kicked from your country");
    }
}
