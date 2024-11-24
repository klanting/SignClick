package com.klanting.signclick.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class ContractSignCTC extends CompanyHandler {
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company sign_contract_ctc <owncompany>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();


        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bbusiness name is invalid");

        CommandAssert.assertTrue(Market.getBusiness(stock_name).isOwner(player.getUniqueId()), "§byou must be CEO to sign that request");

        Company comp = Market.getBusiness(stock_name);
        CommandAssert.assertTrue(comp.spendable >= comp.compAmountPending, "§bcan't sign contract because lack of weekly spendable funds");

        if (firstEnter){
            DecimalFormat df = new DecimalFormat("###,###,###");
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM§b) requested from §f" +comp.compNamePending
                    +"§b \nfor an amount of §f"+ df.format(comp.compAmountPending)
                    +"§b \nfor a time of §f"+ comp.compWeeksPending +
                    " weeks \n§c/company sign_contract_ctc "+stock_name);
            return true;
        }

        comp.acceptOfferCompContract();
        player.sendMessage("§bcontract confirmed");

        return false;
    }
}
