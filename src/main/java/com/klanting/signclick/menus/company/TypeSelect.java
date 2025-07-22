package com.klanting.signclick.menus.company;

import com.klanting.signclick.commands.companyHandelers.CompanyHandlerCreate;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TypeSelect extends SelectionMenu {

    public CompanyHandlerCreate.companyCreationDetails details;

    public TypeSelect(CompanyHandlerCreate.companyCreationDetails details){
        super(9, "Company Type Select", false);
        this.details = details;
        init();
    }

    public void init(){
        ItemStack value;

        value = ItemFactory.create(Material.GOLD_INGOT, "§6bank");
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = ItemFactory.create(Material.MINECART, "§6transport");
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = ItemFactory.create(Material.IRON_CHESTPLATE, "§6product");
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = ItemFactory.create(Material.QUARTZ_BLOCK, "§6real estate");
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = ItemFactory.create(Material.BOW, "§6military");
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = ItemFactory.create(Material.BRICKS, "§6building");
        getInventory().setItem(getInventory().firstEmpty(), value);

        value = ItemFactory.create(Material.SUNFLOWER, "§6other");
        getInventory().setItem(getInventory().firstEmpty(), value);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        TypeSelect old_screen = (TypeSelect) event.getClickedInventory().getHolder();
        String name = event.getCurrentItem().getItemMeta().getDisplayName();

        old_screen.details = new CompanyHandlerCreate.companyCreationDetails(
                old_screen.details.companyName(),
                old_screen.details.stockName(),
                old_screen.details.player(),
                old_screen.details.creationCost(),
                name.substring(2)
        );
        player.closeInventory();

        try{
            CompanyHandlerCreate.createCompany(old_screen.details);
        }catch (CommandException e){
            player.sendMessage(e.getMessage());
        }

        return true;
    }

}
