package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.CommandTools;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CompanyHandlerGive extends CompanyHandler{
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        CommandAssert.assertTrue(args.length >= 3, "§bplease enter /company give <stockname> <amount>");

        String stock_name = args[1].toUpperCase();
        stock_name = stock_name.toUpperCase();

        double amount = CommandTools.parseDouble(args[2], "§bPlease enter a valid double as amount");

        CommandAssert.assertTrue(Market.hasBusiness(stock_name), "§bplease enter a valid company stockname");

        CommandAssert.assertTrue(SignClick.getEconomy().has(player, amount), "§byou do not have enough money");

        DecimalFormat df = new DecimalFormat("###,###,###");

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm\nthat you want to give §f" +df.format(amount)+
                    "§b to §f"+ stock_name+"\n§c/company give "+stock_name+" "+amount);
            return true;
        }

        player.sendMessage("§byou succesfully gave §f"+df.format(amount)+"§b to §f"+stock_name);

        Market.getCompany(stock_name).addBal(amount);

        SignClick.getEconomy().withdrawPlayer(player, amount);

        Market.getCompany(stock_name).getCOM().sendOwner("§byour business §f"+stock_name+" §b received §f"+amount+" §b from §f"+player.getName());

        return false;
    }
}
