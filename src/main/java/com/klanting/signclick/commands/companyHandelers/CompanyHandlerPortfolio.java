package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CompanyHandlerPortfolio extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        UUID target_uuid = null;

        if(args.length < 2){
            target_uuid = player.getUniqueId();
        }else{
            for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                if (target.getName().equals(args[1])){
                    target_uuid = target.getUniqueId();
                }
            }
        }

        CommandAssert.assertTrue(target_uuid != null, "§bplayer doesn't exist");

        CommandAssert.assertTrue(Market.hasAccount(target_uuid), "§bplayer doesn't have an account");

        Market.getAccount(target_uuid).getPortfolio(player);

        return false;
    }
}
