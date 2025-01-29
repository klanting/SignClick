package com.klanting.signclick.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

public class ContractSendCTP  extends CompanyHandler {
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 4, "§bplease enter /company send_contract_ctp <othercompany> <amount> <weeks> [reason]");

        String reason;
        if (args.length < 5){
            reason = "no_reason";
        }else{
            reason = args[4];
        }

        String target_stock_name = args[1].toUpperCase();
        target_stock_name = target_stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(target_stock_name), "§bbusiness name is invalid");

        double amount = Double.parseDouble(args[2]);
        int weeks = Integer.parseInt(args[3]);

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to send a contract request to §f" +target_stock_name
                    +"§b \n for an amount of §f"+ amount
                    +"§b \n for a time of §f"+ weeks+
                    " weeks \n§c/company send_contract_ctp "+target_stock_name+" "+amount+ " "+ weeks);
            return true;
        }

        if (Market.getCompany(target_stock_name).playerNamePending == null){
            Market.getCompany(target_stock_name).receiveOfferPlayerContract(player.getUniqueId().toString(), amount, weeks, reason);
        }else{
            player.sendMessage("§ccompany still has another offer pending, try again in 2 minutes");
        }
        return false;
    }
}
