package com.klanting.signclick.menus.country;

import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ElectionMenu extends PagingMenu {
    public Election e;
    public ElectionMenu(Election e){
        super(27, "Country Vote", true);
        this.e = e;
        init();
    }

    public void init(){

        clearItems();

        for (String name: e.voteDict.keySet()){

            ItemStack click = new ItemStack(Material.PAPER, 1);
            ItemMeta m = click.getItemMeta();
            m.setDisplayName("§6"+name);
            click.setItemMeta(m);

            addItem(click);
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }
        ItemStack item = event.getCurrentItem();
        ItemMeta m = item.getItemMeta();
        String party = m.getDisplayName().substring(2);

        ElectionMenu old_screen = (ElectionMenu) event.getClickedInventory().getHolder();
        old_screen.e.vote(party, event.getWhoClicked().getUniqueId());

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        player.closeInventory();

        return true;
    }

}
