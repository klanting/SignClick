package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.utils.PreciseNumberFormatter;
import org.bukkit.entity.Player;

public class CompanyHandlerCreate extends CompanyHandler{
    /*
    * Handle commands for creating a company
    * */
    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 3,
                "§bplease enter /company create <name> <stockname>");


        String company_name = args[1];
        String stock_name = args[2];

        CommandAssert.assertTrue(stock_name.length() <= 4,
                "§bstockname has a max length of 4");

        Country country = CountryManager.getCountry(player);
        double discount_pct = 1.0;
        if (country != null){
            discount_pct = (1.0- country.getPolicyBonus(1, 4));
        }

        double baseCreationCost = SignClick.getPlugin().getConfig().getDouble("companyCreateCost");
        double creationCost = baseCreationCost*discount_pct;

        String formattedCost = PreciseNumberFormatter.format(creationCost);

        CommandAssert.assertTrue(SignClick.getEconomy().has(player, creationCost),
                "§bmaking a company costs §c"+formattedCost);

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm that you want to start a company" +
                    " and want to auto-transfer §6"+formattedCost+" §bto your business from your account"+
                    " If you agree, enter: §c/company create "+company_name+" "+stock_name);
            return true;
        }

        stock_name = stock_name.toUpperCase();
        Boolean succes = Market.addCompany(company_name, stock_name, Market.getAccount(player), creationCost);

        CommandAssert.assertTrue(succes, "§bcompany create failed: name/stockName already in use");

        player.sendMessage("§byou succesfully found "+company_name+" good luck CEO "+player.getName());

        SignClick.getEconomy().withdrawPlayer(player, creationCost);

        return false;
    }


}
