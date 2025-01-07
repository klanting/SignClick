package com.klanting.signclick.commands.companyHandelers.contractHandlers;

import com.klanting.signclick.commands.companyHandelers.CompanyHandler;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Market;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

import static com.klanting.signclick.economy.Market.getBusiness;

public class ContractSignPTC extends CompanyHandler {

    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        Account acc = Market.getAccount(player);

        CommandAssert.assertTrue(acc.getBal() >= acc.compAmountPending,
                "§bcan't sign contract because lack of money");

        if (firstEnter){
            DecimalFormat df = new DecimalFormat("###,###,###");

            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM§b) requested from §f" + acc.compNamePending
                    +"§b \nfor an amount of §f"+ df.format(acc.compAmountPending)
                    +"§b \nfor a time of §f"+ acc.compWeeksPending +
                    " weeks \n§c/company sign_contract_ptc");
            return true;
        }


        acc.accept_offer_comp_contract();
        player.sendMessage("§bcontract confirmed");

        return false;

    }
}
