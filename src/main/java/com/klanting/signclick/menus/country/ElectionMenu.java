package com.klanting.signclick.menus.country;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ElectionMenu extends PagingMenu {
    public Election e;
    public ElectionMenu(Election e, UUID uuid){
        super(27, "Election: "+ Utils.formatDuration(e.getToWait()/20) +" left", true);
        this.e = e;
        init();
        startTitleUpdater(Bukkit.getPlayer(uuid));
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

    public void startTitleUpdater(Player player) {
        BukkitRunnable titleUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                Inventory top = player.getOpenInventory().getTopInventory();

                if (top == null ||!top.equals(getInventory())) {
                    cancel();
                    return;
                }

                String title = "Election: "+ Utils.formatDuration(e.getToWait()/20) +" left";
                setTitle(title, player);

            }
        };

        titleUpdater.runTaskTimer(SignClick.getPlugin(), 0L, 20L); // every second
    }

}
