package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerGetSellPrice extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 2, "§bplease enter /company get_sell_price <stockname> [amount]");

        int amount = 1;
        if (args.length == 3){
            amount = Integer.parseInt(args[2]);
        }

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bbusiness name is invalid");

        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        player.sendMessage("§f"+amount+"§b share(s) costs §f"+ df.format(Market.getSellPrice(stock_name, amount)));

        return false;
    }
}
