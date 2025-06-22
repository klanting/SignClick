package com.klanting.signclick.menus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

abstract public class PagingMenu extends SelectionMenu{

    private int page = 0;
    private List<ItemStack> items = new ArrayList<>();

    private boolean backButton;

    public PagingMenu(int size, String title, boolean backButton) {
        this(size, title, backButton, size-1);
    }

    public PagingMenu(int size, String title, boolean backButton, int backPosition) {
        super(size, title, backButton, backPosition);
        this.backButton = backButton;
    }

    public void addItem(ItemStack item){
        items.add(item);
    }

    public void clearItems(){
        items.clear();
    }

    public void init(){
        getInventory().clear();

        int startPos = getInventory().getSize()-9;

        ItemMeta itemMeta;
        if (page > 0){
            ItemStack prevPage = new ItemStack(Material.ARROW);
            itemMeta = prevPage.getItemMeta();
            itemMeta.setDisplayName("§7Previous Page: "+ (page-1));
            prevPage.setItemMeta(itemMeta);
            getInventory().setItem(startPos, prevPage);
        }else{
            ItemStack grayGlass = new ItemStack(Material.RED_DYE, 1);
            ItemMeta m = grayGlass.getItemMeta();
            m.setDisplayName("§cNo Previous Page");
            grayGlass.setItemMeta(m);
            getInventory().setItem(startPos, grayGlass);
        }


        ItemStack searchWord = new ItemStack(Material.NAME_TAG);
        itemMeta = searchWord.getItemMeta();
        itemMeta.setDisplayName("§7Search");
        searchWord.setItemMeta(itemMeta);
        getInventory().setItem(startPos+1, searchWord);

        int usableSpace = getInventory().getSize()-9;

        int itemStartIndex = usableSpace*page;

        if (itemStartIndex + usableSpace < items.size()){
            ItemStack nextPage = new ItemStack(Material.ARROW);
            itemMeta = nextPage.getItemMeta();
            itemMeta.setDisplayName("§7Next Page: "+ (page+1));
            nextPage.setItemMeta(itemMeta);
            getInventory().setItem(startPos+2, nextPage);
        }else{
            ItemStack grayGlass = new ItemStack(Material.RED_DYE, 1);
            ItemMeta m = grayGlass.getItemMeta();
            m.setDisplayName("§cNo Next Page");
            grayGlass.setItemMeta(m);
            getInventory().setItem(startPos+2, grayGlass);
        }

        int endPos = backButton ? getInventory().getSize()-1: getInventory().getSize();
        for (int i=startPos+3; i<endPos; i++){
            ItemStack grayGlass = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta m = grayGlass.getItemMeta();
            m.setDisplayName("§f");
            grayGlass.setItemMeta(m);
            getInventory().setItem(i, grayGlass);
        }

        for (int i=0; i<Math.min(usableSpace, items.size()-usableSpace*page); i++){
            getInventory().setItem(i, items.get(itemStartIndex+i));
        }

        checkBackButton();
    }

    public int getPage(){
        return page;
    }

    public void changePage(int change){
        page += change;
    }
}
