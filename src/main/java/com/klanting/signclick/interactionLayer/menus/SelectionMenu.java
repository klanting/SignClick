package com.klanting.signclick.interactionLayer.menus;

import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Bukkit.getServer;

abstract public class SelectionMenu implements InventoryHolder {
    protected Inventory menu;

    private final boolean backButton;
    private final int backPosition;

    public SelectionMenu(int size, String title, boolean backButton){
        this(size, title, backButton, size-1);
    }

    public SelectionMenu(int size, String title, boolean backButton, int backPosition){
        menu = Bukkit.createInventory(this, size, SignClick.getPrefixUI()+title);

        this.backButton = backButton;
        this.backPosition = backPosition;

        checkBackButton();

    }

    public void checkBackButton(){
        if (backButton){
            ItemStack back = new ItemStack(Material.BARRIER);
            ItemMeta itemMeta = back.getItemMeta();
            itemMeta.setDisplayName("§cBack");
            back.setItemMeta(itemMeta);
            menu.setItem(backPosition, back);
        }
    }

    public void onOpen() {

        while (menu.firstEmpty() != -1){
            menu.setItem(menu.firstEmpty(), com.klanting.signclick.utils.ItemFactory.createGray());
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }

    public void init(){
        checkBackButton();
        onOpen();
    }

    public void setTitle(String title, Player player){
        menu = Bukkit.createInventory(getInventory().getHolder(), getInventory().getSize(), SignClick.getPrefixUI()+title);
        init();
        player.openInventory(getInventory());
    }

    abstract public boolean onClick(InventoryClickEvent event);
}
