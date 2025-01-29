package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CompanyHandlerOpenTrade extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company open_trade <Company> [TRUE/FALSE]");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bbusiness name is invalid");

        if (args.length < 3){
            player.sendMessage("§bopen trade is "+ Market.getCompany(stock_name).openTrade);
            return false;
        }

        CommandAssert.assertTrue(Market.getCompany(stock_name).isOwner(player.getUniqueId()), "§byou must be a CEO of this com.company");

        boolean to_open = Objects.equals(args[2], "TRUE");
        Market.getCompany(stock_name).openTrade = to_open;

        if (!to_open){
            Market.getCompany(stock_name).marketShares = 0;
        }else{

            Company comp = Market.getCompany(stock_name);

            comp.setTotalShares(comp.getTotalShares()-comp.getMarketShares());

            comp.marketShares = 0;
        }

        player.sendMessage("§bopen trade set to "+ Market.getCompany(stock_name).openTrade);

        return false;
    }
}
