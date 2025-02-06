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
         * Outro Page
         * */
        pages.add("""
                  §9The End
                  §0The world is in your hands now, my dear visionary.
                  Good Luck §6""" + player.getName() + """
                  
                  §0May fortune and success find you!!!""");

        meta.setPages(pages);

        book.setItemMeta(meta);

        player.getInventory().setItem(empty, book);

        return false;
    }
}
