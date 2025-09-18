package com.klanting.signclick.interactionLayer.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.CommandTools;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerTransact extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 4, "please enter /company transact <stockname> <target_company> <amount>");


        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "business name is invalid");

        String target_stock_name = args[2].toUpperCase();
        target_stock_name = target_stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(target_stock_name), "business name is invalid");

        double amount = CommandTools.parseDouble(args[3], "Please enter a valid double as amount");

        CommandAssert.assertTrue(Market.getCompany(stock_name).getCOM().isOwner(player.getUniqueId()), "§byou must be a CEO of this company");

        if (firstEnter){
            player.sendMessage(SignClick.getPrefix()+"please re-enter the command to confirm");
            return true;
        }

        //still need refresh weekly the 20 % cap
        if (Market.getCompany(stock_name).removeBal(amount)){
            CompanyI comp_target = Market.getCompany(target_stock_name);
            comp_target.addBal(amount);
            player.sendMessage(SignClick.getPrefix()+"succesfully paid §f"+target_stock_name+" "+amount);
            comp_target.getCOM().sendOwner("§bsuccesfully received §f"+amount+" "+SignClick.getPrefix()+"from §f"+stock_name);

            DecimalFormat df = new DecimalFormat("###,###,###");

            Market.getCompany(stock_name).update("Balance removed",
                    "§cCompany paid "+ df.format(amount) + " to " + target_stock_name, player.getUniqueId());

            Market.getCompany(target_stock_name).update("Balance added",
                    "§aCompany"+ stock_name + " paid "+ df.format(amount), player.getUniqueId());

        }else{
            player.sendMessage(SignClick.getPrefix()+"business does not have enough money, or you reached your monthly spending limit\ndo §c/company spendable "+
                    stock_name+SignClick.getPrefix()+" to see monthly available money");
        }
        return false;
    }
}
