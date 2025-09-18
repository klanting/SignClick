package com.klanting.signclick.interactionLayer.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.Market;
import com.klanting.signclick.logicLayer.contractRequests.ContractRequest;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class ContractSignCTC extends CompanyHandler {
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "please enter /company sign_contract_ctc <owncompany>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();


        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "business name is invalid");

        CommandAssert.assertTrue(Market.getCompany(stock_name).getCOM().isOwner(player.getUniqueId()), "you must be CEO to sign that request");

        CompanyI comp = Market.getCompany(stock_name);
        ContractRequest cr = comp.getPendingContractRequest();

        CommandAssert.assertTrue(cr != null, "no contract pending");

        CommandAssert.assertTrue(comp.getSpendable() >= cr.getAmount(), "can't sign contract because lack of weekly spendable funds");

        if (firstEnter){

            DecimalFormat df = new DecimalFormat("###,###,###");
            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM"+SignClick.getPrefix()+") requested from §f" +cr.to()
                    +SignClick.getPrefix()+" \nfor an amount of §f"+ df.format(cr.getAmount())
                    +SignClick.getPrefix()+" \nfor a time of §f"+ cr.getWeeks() +
                    " weeks \n§c/company sign_contract_ctc "+stock_name);
            return true;
        }

        comp.acceptOfferCompContract();
        player.sendMessage("§bcontract confirmed");

        return false;
    }
}
