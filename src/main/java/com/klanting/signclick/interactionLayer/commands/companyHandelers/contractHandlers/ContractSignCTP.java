package com.klanting.signclick.interactionLayer.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.logicLayer.companyLogic.contractRequests.ContractRequest;
import com.klanting.signclick.logicLayer.companyLogic.contractRequests.ContractRequestCTP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class ContractSignCTP extends CompanyHandler {
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, SignClick.getPrefix()+"please enter /company sign_contract_ctp <owncompany>");


        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), SignClick.getPrefix()+"business name is invalid");

        CommandAssert.assertTrue(Market.getCompany(stock_name).getCOM().isOwner(player.getUniqueId()), SignClick.getPrefix()+"you must be CEO to sign that request");

        CompanyI comp = Market.getCompany(stock_name);
        ContractRequest cr = comp.getPendingContractRequest();

        CommandAssert.assertTrue(cr != null, "no contract pending");
        CommandAssert.assertTrue(cr instanceof ContractRequestCTP, "incorrect contract type");

        CommandAssert.assertTrue(comp.getSpendable() >= cr.getAmount(), SignClick.getPrefix()+"can't sign contract because lack of weekly spendable funds");

        CommandAssert.assertTrue(!comp.getCOM().isOwner(UUID.fromString(cr.to())) || !player.getUniqueId().equals(UUID.fromString(cr.to())),
                SignClick.getPrefix()+"you can't' make a contract with yourself");

        if (firstEnter){
            DecimalFormat df = new DecimalFormat("###,###,###");

            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM"+SignClick.getPrefix()+") requested from §f" + Bukkit.getOfflinePlayer(UUID.fromString(cr.to())).getName()
                    +SignClick.getPrefix()+" \nfor an amount of §f"+ df.format(cr.getAmount())
                    +SignClick.getPrefix()+" \nfor a time of §f"+ cr.getWeeks() +
                    " weeks \n§c/company sign_contract_ctp "+stock_name);

            return true;
        }

        comp.acceptOfferPlayerContract();
        player.sendMessage(SignClick.getPrefix()+"contract confirmed");

        return false;
    }
}
