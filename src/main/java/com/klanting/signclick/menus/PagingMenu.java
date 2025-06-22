package com.klanting.signclick.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.klanting.signclick.utils.ItemFactory;

import java.util.ArrayList;
import java.util.List;

abstract public class PagingMenu extends SelectionMenu{

    private int page = 0;
    private List<ItemStack> items = new ArrayList<>();

    private boolean backButton;

    private String searchKey = "";

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

        ItemStack prevPtr;
        if (page > 0){
            prevPtr = ItemFactory.create(Material.ARROW, "§7Previous Page: "+ (page-1));
        }else{
            prevPtr = ItemFactory.create(Material.RED_DYE, "§cNo Previous Page");
        }
        getInventory().setItem(startPos, prevPtr);

        ItemStack searchPtr;
        if (searchKey.isEmpty()){
            searchPtr = ItemFactory.create(Material.NAME_TAG, "§7Search");
        }else{
            searchPtr = ItemFactory.create(Material.RED_WOOL, "§7Cancel Search on "+searchKey);
        }
        getInventory().setItem(startPos+1, searchPtr);

        int usableSpace = getInventory().getSize()-9;

        int itemStartIndex = usableSpace*page;

        ItemStack nextPtr;
        if (itemStartIndex + usableSpace < items.size()){
            nextPtr = ItemFactory.create(Material.ARROW, "§7Next Page: "+ (page+1));
        }else{
            nextPtr = ItemFactory.create(Material.RED_DYE, "§cNo Next Page");
        }
        getInventory().setItem(startPos+2, nextPtr);

        int endPos = backButton ? getInventory().getSize()-1: getInventory().getSize();
        for (int i=startPos+3; i<endPos; i++){
            getInventory().setItem(i, ItemFactory.createGray());
        }

        for (int i=0; i<items.size()-usableSpace*page && getInventory().firstEmpty() != -1; i++){
            ItemStack item = items.get(itemStartIndex+i);
            if (!item.getItemMeta().getDisplayName().contains(searchKey)){
                continue;
            }

            getInventory().setItem(getInventory().firstEmpty(), item);
        }

        checkBackButton();
    }

    public void changePage(int change){
        page += change;
    }

    public int getItemIndex(ItemStack item){
        return items.indexOf(item);
    }

    public void setSearchKey(String searchKey){
        this.searchKey = searchKey;
        init();
    }
}
