package com.klanting.signclick.commands.countryHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.utils.BookParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class CountryHandlerGuide extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        /*
         * Make a country Guide book
         * */

        int empty = player.getInventory().firstEmpty();

        CommandAssert.assertTrue(empty != -1,
                "§bYou need an empty slot in your inventory to receive the guide");

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setAuthor("The SignClick Country Federation");
        meta.setTitle("§6SignClick Country Guide");

        List<String> pages = BookParser.getPages("countryGuide.book", player);

        meta.setPages(pages);

        book.setItemMeta(meta);

        player.getInventory().setItem(empty, book);
    }
}
