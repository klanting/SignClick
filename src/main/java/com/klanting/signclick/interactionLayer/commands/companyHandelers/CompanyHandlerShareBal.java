package com.klanting.signclick.interactionLayer.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.CompanyI;
import com.klanting.signclick.logicLayer.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

import static com.klanting.signclick.logicLayer.Market.getCompany;

public class CompanyHandlerShareBal extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "please enter /company sharebal <owncompany>");


        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "business name is invalid");

        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        CompanyI comp = getCompany(stock_name);
        player.sendMessage(SignClick.getPrefix()+" shareBal money: "+df.format(comp.getShareBalance()));

        return false;
    }
}
