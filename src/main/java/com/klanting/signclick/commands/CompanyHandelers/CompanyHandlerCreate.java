package com.klanting.signclick.commands.CompanyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.Exceptions.CommandAssert;
import com.klanting.signclick.commands.Exceptions.CommandException;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import org.bukkit.entity.Player;

import static com.klanting.signclick.commands.Company.CompanyCommands.confirm;

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

        CommandAssert.assertTrue(SignClick.getEconomy().has(player, 40000000.0*discount_pct),
                "§bmaking a company costs §c40 million (or discount policy)");

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm that you want to start a company" +
                    " and want to auto-transfer §640 million §bto your business from your account"+
                    " If you agree, enter: §c/company create "+company_name+" "+stock_name);
            return true;
        }

        player.sendMessage("§byou succesfully found "+company_name+" good luck CEO "+player.getName());
        stock_name = stock_name.toUpperCase();
        Boolean succes = Market.addBusiness(company_name, stock_name, Market.getAccount(player));

        CommandAssert.assertTrue(succes, "§bcompany create failed: name/stockName already in use");

        SignClick.getEconomy().withdrawPlayer(player, 40000000.0*discount_pct);
        Company comp = Market.getBusiness(stock_name);
        comp.addBal(40000000.0*discount_pct);

        return false;
    }


}
