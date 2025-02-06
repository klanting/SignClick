package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;

public class CompanyHandlerGuide extends CompanyHandler{

    @Override
    public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException {
        /*
        * Make a company Guide book
        * */

        int empty = player.getInventory().firstEmpty();

        CommandAssert.assertTrue(empty != -1,
                "§bYou need an empty slot in your inventory to receive the guide");

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setAuthor("The SignClick Company Federation");
        meta.setTitle("§6SignClick Company Guide");

        ArrayList<String> pages = new ArrayList<>();

        /*
        * Intro Page
        * */
        pages.add("""
                §9SignClick Company Guide
                §0Hello You !
                Are you ready to tackle the economical world?
                No? hmmm... That might be a problem.
                But Your in luck, for just 5 min of your time, I will learn you how to work with the SignClick company system.
                """);

        /*
        * Company creation
        * */
        pages.add("""
                §9Company Creation
                §0With the command '§9/company create <name> <stockname>§0', you can create your own company.
                Creation a company might cost a fee, this fee will be automatically added to your company bank account.
                """);

        pages.add("""
                Name is the long name of the company.
                Stockname is the short abbreviation of the company to make it easy to use.
                When creating a company you will automatically start with all shares.
                """);

        /*
         * Company Shares
         * */
        pages.add("""
                §9Company Shares
                §0With the command '§9/company buy <stockname> <amount>§0', you can buy shares of the company.
                With the command '§9/company sell <stockname> <amount>§0', you can sell shares of the company.
                """);

        pages.add("""
                If you don't like the manual commands, you can use
                '§9/company market§0' to buy/sell shares using the UI.
                """);

        /*
         * Company menu
         * */
        pages.add("""
                §9Company management
                §0Buying and Selling shares is good, but making your company successful is better.
                Companies have many ways to acquire income. Check out
                '§9/company menu§0' to discover some company options.
                """);

        /*
         * Company Payments
         * */
        pages.add("""
                §9Company Payments
                §0'§9/company give <stockname> <amount>§0' Lets a player give money to a company, while
                '§9/company pay <stockname> <player> <amount>§0' gives money to a player from the company account.
                """);

        /*
        * Company Patent
        * */
        pages.add("""
                §9Company Patents
                §0Your company is special. But Why?
                Maybe it is the great goods you produce?
                You can make special armor that has never been seen before.
                """);

        pages.add("""
                §0A company patent is in this case a unique product, only the company can produce.
                A patent exists of a piece of gear combined with patent upgrades.
                patent upgrades can be acquired by an auction.
                """);
        pages.add("""
                §0The company who ends with the highest bid will after some time receive this patent upgrade.
                
                A patent includes patent upgrades that give special abilities/bonuses.
                """);

        /*
         * Outro Page
         * */
        pages.add("""
                  §9The End
                  
                  §0The world is in your hands now, my dear visionary.
                  Good Luck §6""" + player.getName() + """
                  §0!
                  May fortune and success find you!!!""");

        meta.setPages(pages);

        book.setItemMeta(meta);

        player.getInventory().setItem(empty, book);

        return false;
    }
}
