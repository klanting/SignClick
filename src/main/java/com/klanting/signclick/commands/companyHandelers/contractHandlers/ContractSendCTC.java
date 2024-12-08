package com.klanting.signclick.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

public class ContractSendCTC extends CompanyHandler {
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 5, "§bplease enter /company send_contract_ctc <owncompany> <othercompany> <amount> <weeks> [reason]");

        String reason;
        if (args.length < 6){
            reason = "no_reason";
        }else{
            reason = args[5];
        }

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bbusiness name is invalid");

        CommandAssert.assertTrue(Market.getBusiness(stock_name).isOwner(player.getUniqueId()), "§byou must be CEO to send that request");

        String target_stock_name = args[2].toUpperCase();
        target_stock_name = target_stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(target_stock_name), "§bbusiness name is invalid");

        double amount = Double.parseDouble(args[3]);
        int weeks = Integer.parseInt(args[4]);

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to send a contract request to §f" +target_stock_name
                    +"\n§bfor an amount of §f"+ amount
                    +"\n§bfor a time of §f"+ weeks+
                    " weeks \n§c/company send_contract_ctc "+stock_name+" "+target_stock_name+" "+amount+ " "+ weeks);
            return true;
        }

        if (!Market.getBusiness(target_stock_name).hasPendingContractRequest()){
            Market.getBusiness(stock_name).sendOfferCompContract(target_stock_name, amount, weeks, reason);
        }else{
            player.sendMessage("§ccompany still has another offer pending, try again in 2 minutes");
        }
        return false;
    }
}
