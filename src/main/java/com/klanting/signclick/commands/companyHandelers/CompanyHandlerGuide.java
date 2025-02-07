package com.klanting.signclick.commands.companyHandelers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.utils.BookParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

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

        List<String> pages = BookParser.getPages("companyGuide.book", player);

        meta.setPages(pages);

        book.setItemMeta(meta);

        player.getInventory().setItem(empty, book);

        return false;
    }
}
