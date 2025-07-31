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
        CommandAssert.assertTrue(args.length >= 3, "please enter /company give <stockname> <amount>");

        String stockName = args[1].toUpperCase();
        stockName = stockName.toUpperCase();

        double amount = CommandTools.parseDouble(args[2], "Please enter a valid double as amount");

        CommandAssert.assertTrue(Market.hasBusiness(stockName), "please enter a valid company stockname");

        CommandAssert.assertTrue(SignClick.getEconomy().has(player, amount), "§byou do not have enough money");

        DecimalFormat df = new DecimalFormat("###,###,###");

        if (firstEnter){
            player.sendMessage(SignClick.getPrefix()+"please re-enter your command to confirm\nthat you want to give §f" +df.format(amount)+
                    SignClick.getPrefix()+" to §f"+ stockName+"\n§c/company give "+stockName+" "+amount);
            return true;
        }

        player.sendMessage(SignClick.getPrefix()+"you succesfully gave §f"+df.format(amount)+SignClick.getPrefix()+" to §f"+stockName);

        Market.getCompany(stockName).addBal(amount);

        SignClick.getEconomy().withdrawPlayer(player, amount);

        Market.getCompany(stockName).update("Balance added",
                "§aPlayer paid "+ df.format(amount) + " to " + stockName, player.getUniqueId());

        Market.getCompany(stockName).getCOM().sendOwner(SignClick.getPrefix()+"your business §f"+stockName+" "+SignClick.getPrefix()+" received §f"+amount+" "+SignClick.getPrefix()+" from §f"+player.getName());

        return false;
    }
}
