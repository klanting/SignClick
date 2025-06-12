package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.menus.company.TypeSelect;
import com.klanting.signclick.utils.PreciseNumberFormatter;
import org.bukkit.entity.Player;

public class CompanyHandlerCreate extends CompanyHandler{
    /*
    * Handle commands for creating a company
    * */

    public static record companyCreationDetails(String companyName, String stockName, Player player,
                                                double creationCost, String companyType) {}

    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 3,
                "§bplease enter /company create <name> <stockname>");


        String company_name = args[1];
        String stockName = args[2];

        CommandAssert.assertTrue(stockName.length() <= 4,
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
                    " If you agree, enter: §c/company create "+company_name+" "+stockName);
            return true;
        }

        stockName = stockName.toUpperCase();

        TypeSelect new_screen = new TypeSelect(new companyCreationDetails(company_name, stockName, player,
                creationCost, null));
        player.openInventory(new_screen.getInventory());

        return false;
    }

    public static void createCompany(companyCreationDetails details) throws CommandException {
        Boolean succes = Market.addCompany(details.companyName,
                details. stockName, Market.getAccount(details.player), details.creationCost, details.companyType);

        CommandAssert.assertTrue(succes, "§bcompany create failed: name/stockName already in use");

        details.player.sendMessage("§byou succesfully found "+details.companyName+" good luck CEO "+
                details.player.getName());

        SignClick.getEconomy().withdrawPlayer(details.player, details.creationCost);

    }


}
