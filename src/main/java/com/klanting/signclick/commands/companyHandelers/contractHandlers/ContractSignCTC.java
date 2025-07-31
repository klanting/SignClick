package com.klanting.signclick.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.contractRequests.ContractRequest;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class ContractSignCTC extends CompanyHandler {
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, SignClick.getPrefix()+"please enter /company sign_contract_ctc <owncompany>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();


        CommandAssert.assertTrue(Market.hasBusiness(stock_name), SignClick.getPrefix()+"business name is invalid");

        CommandAssert.assertTrue(Market.getCompany(stock_name).getCOM().isOwner(player.getUniqueId()), SignClick.getPrefix()+"you must be CEO to sign that request");

        CompanyI comp = Market.getCompany(stock_name);
        ContractRequest cr = comp.getPendingContractRequest();

        CommandAssert.assertTrue(cr != null, SignClick.getPrefix()+"no contract pending");

        CommandAssert.assertTrue(comp.getSpendable() >= cr.getAmount(), SignClick.getPrefix()+"can't sign contract because lack of weekly spendable funds");

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
