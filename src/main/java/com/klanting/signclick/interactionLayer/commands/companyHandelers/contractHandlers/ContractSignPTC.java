package com.klanting.signclick.interactionLayer.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Account;
import com.klanting.signclick.logicLayer.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class ContractSignPTC extends CompanyHandler {

    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        Account acc = Market.getAccount(player);

        CommandAssert.assertTrue(acc.getBal() >= acc.compAmountPending,
                SignClick.getPrefix()+"can't sign contract because lack of money");

        if (firstEnter){
            DecimalFormat df = new DecimalFormat("###,###,###");

            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM"+SignClick.getPrefix()+") requested from §f" + acc.compNamePending
                    +SignClick.getPrefix()+" \nfor an amount of §f"+ df.format(acc.compAmountPending)
                    +SignClick.getPrefix()+" \nfor a time of §f"+ acc.compWeeksPending +
                    " weeks \n§c/company sign_contract_ptc");
            return true;
        }


        acc.accept_offer_comp_contract();
        player.sendMessage(SignClick.getPrefix()+"contract confirmed");

        return false;

    }
}
