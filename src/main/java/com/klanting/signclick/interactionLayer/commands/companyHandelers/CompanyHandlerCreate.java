package com.klanting.signclick.interactionLayer.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.CommandTools;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandAssert;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import com.klanting.signclick.logicLayer.Market;
import com.klanting.signclick.interactionLayer.menus.company.TypeSelect;
import com.klanting.signclick.utils.PreciseNumberFormatter;
import org.bukkit.entity.Player;

public class CompanyHandlerCreate extends CompanyHandler{
    /*
    * Handle commands for creating a company
    * */

    public record companyCreationDetails(String companyName, String stockName, Player player,
                                         double creationCost, String companyType) {}

    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {

        CommandAssert.assertTrue(args.length >= 3,
                "§bplease enter /company create <name> <stockname>");



        String companyName = CommandTools.parseString(args[1], "§bPlease use allowed characters for the Name");
        String stockName = CommandTools.parseString(args[2], "§bPlease use allowed characters for the Stock name");

        CommandAssert.assertTrue(stockName.length() <= 4,
                "§bstockname has a max length of 4");

        Country country = CountryManager.getCountry(player);
        double discount_pct = 1.0;
        if (country != null){
            discount_pct = (1.0- country.getPolicyBonus("createDiscount"));
        }

        double baseCreationCost = SignClick.getConfigManager().getConfig("companies.yml").getDouble("companyCreateCost");
        double creationCost = baseCreationCost*discount_pct;

        String formattedCost = PreciseNumberFormatter.format(creationCost);

        CommandAssert.assertTrue(SignClick.getEconomy().has(player, creationCost),
                "§bmaking a company costs §c"+formattedCost);

        if (firstEnter){
            player.sendMessage("§bplease re-enter your command to confirm that you want to start a company" +
                    " and want to auto-transfer §6"+formattedCost+" §bto your business from your account"+
                    " If you agree, enter: §c/company create "+companyName+" "+stockName);
            return true;
        }

        stockName = stockName.toUpperCase();

        TypeSelect new_screen = new TypeSelect(new companyCreationDetails(companyName, stockName, player,
                creationCost, null));
        player.openInventory(new_screen.getInventory());

        return false;
    }

    public static void createCompany(companyCreationDetails details) throws CommandException {
        Boolean succes = Market.addCompany(details.companyName,
                details. stockName, Market.getAccount(details.player), details.creationCost, details.companyType);

        CommandAssert.assertTrue(succes, "§bcompany create failed: name/stock name already in use");

        details.player.sendMessage("§byou succesfully founded "+details.companyName+" good luck CEO "+
                details.player.getName());

        SignClick.getEconomy().withdrawPlayer(details.player, details.creationCost);

    }


}
