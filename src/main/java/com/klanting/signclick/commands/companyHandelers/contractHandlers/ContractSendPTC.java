package com.klanting.signclick.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.CommandTools;
import com.klanting.signclick.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.klanting.signclick.economy.Market.getCompany;

public class ContractSendPTC extends CompanyHandler {

    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 5, "please enter /company send_contract_ptc <owncompany> <player> <amount> <weeks> [reason]");


        String reason;
        if (args.length < 6){
            reason = "no_reason";
        }else{
            reason = args[5];
        }

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), SignClick.getPrefix()+"business name is invalid");

        CommandAssert.assertTrue(getCompany(stock_name).getCOM().isOwner(player.getUniqueId()),
                SignClick.getPrefix()+"you must be CEO to send that request");

        UUID target_uuid = null;
        for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
            if (target.getName().equals(args[2])){
                target_uuid = target.getUniqueId();
            }
        }

        CommandAssert.assertTrue(target_uuid != null, SignClick.getPrefix()+"player doesn't exist");

        CommandAssert.assertTrue(Market.hasAccount(target_uuid), SignClick.getPrefix()+"player doesn't have an account");

        double amount = CommandTools.parseDouble(args[3], SignClick.getPrefix()+"Please enter a valid double as amount");
        int weeks = CommandTools.parseInteger(args[4], SignClick.getPrefix()+"Please enter a valid integer as weeks");

        if (firstEnter){
            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to send a contract request to §f" +args[2]
                    +SignClick.getPrefix()+" \n for an amount of §f"+ amount
                    +SignClick.getPrefix()+" \n for a time of §f"+ weeks+
                    " weeks \n§c/company send_contract_ptc "+stock_name+" "+args[2]+" "+amount+ " "+ weeks);
            return true;
        }

        if (Market.getAccount(target_uuid).compNamePending == null){
            Market.getAccount(target_uuid).receive_offer_comp_contract(stock_name, amount, weeks, reason);
        }else{
            player.sendMessage("§cplayer still has another offer pending, try again in 2 minutes");
        }
        return false;
        
    }
}
