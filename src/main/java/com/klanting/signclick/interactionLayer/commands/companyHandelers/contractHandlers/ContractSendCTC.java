package com.klanting.signclick.interactionLayer.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.CommandTools;
import com.klanting.signclick.interactionLayer.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Market;
import org.bukkit.entity.Player;

public class ContractSendCTC extends CompanyHandler {
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 5, "please enter /company send_contract_ctc <owncompany> <othercompany> <amount> <weeks> [reason]");

        String reason;
        if (args.length < 6){
            reason = "no_reason";
        }else{
            reason = args[5];
        }

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "business name is invalid");

        CommandAssert.assertTrue(Market.getCompany(stock_name).getCOM().isOwner(player.getUniqueId()), "§byou must be CEO to send that request");

        String target_stock_name = args[2].toUpperCase();
        target_stock_name = target_stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(target_stock_name), "business name is invalid");


        double amount = CommandTools.parseDouble(args[3], "Please enter a valid double as amount");
        int weeks = CommandTools.parseInteger(args[4], "Please enter a valid integer as weeks");

        if (firstEnter){
            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to send a contract request to §f" +target_stock_name
                    +SignClick.getPrefix()+"\nfor an amount of §f"+ amount
                    +SignClick.getPrefix()+"\nfor a time of §f"+ weeks+
                    " weeks \n§c/company send_contract_ctc "+stock_name+" "+target_stock_name+" "+amount+ " "+ weeks);
            return true;
        }

        if (!Market.getCompany(target_stock_name).hasPendingContractRequest()){
            Market.getCompany(stock_name).sendOfferCompContract(target_stock_name, amount, weeks, reason);
        }else{
            player.sendMessage("§ccompany still has another offer pending, try again in 2 minutes");
        }
        return false;
    }
}
