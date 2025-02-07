package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

import static com.klanting.signclick.economy.Market.getCompany;

public class CompanyHandlerShareBal extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company sharebal <owncompany>");


        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bbusiness name is invalid");

        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        Company comp = getCompany(stock_name);
        player.sendMessage("§b shareBal money: "+df.format(comp.getShareBalance()));

        return false;
    }
}
