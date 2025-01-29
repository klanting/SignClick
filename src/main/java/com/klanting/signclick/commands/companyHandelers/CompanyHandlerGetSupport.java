package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CompanyHandlerGetSupport extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        UUID target_uuid = null;

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company get_support <company> [player_name]");

        if(args.length < 3){
            target_uuid = player.getUniqueId();
        }else{
            OfflinePlayer target_player = Bukkit.getOfflinePlayer(args[2]);
            CommandAssert.assertTrue(target_player != null, "§bplayer doesn't exist");
            target_uuid = target_player.getUniqueId();
        }

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bbusiness name is invalid");

        UUID result = Market.getCompany(stock_name).getCOM().getSupport(target_uuid);
        String name;

        if (result == null){
            name = "neutral";
        }else{
            name = Bukkit.getOfflinePlayer(result).getName();
        }

        player.sendMessage("§bplayer supports §7"+name);

        return false;
    }
}
