package com.klanting.signclick.menus.country;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class Menu extends SelectionMenu {
    private final UUID uuid;

    public Menu(UUID uuid){
        super(54, "Country Menu", false);
        this.uuid = uuid;
        init();
    }

    public void init(){
        Country country = CountryManager.getCountry(uuid);
        
        ItemStack value;
        ItemMeta m;

        value = new ItemStack(Material.GOLD_BLOCK, 1);
        m = value.getItemMeta();
        DecimalFormat df = new DecimalFormat("###,###,###.00");
        m.setDisplayName("§6"+country.getName());
        ArrayList<String> l = new ArrayList<>();

        l.add("§6balance: §9"+ df.format(country.getBalance()));
        l.add("§6Stability: §9"+ df.format(country.getStability()));
        m.setLore(l);
        value.setItemMeta(m);
        getInventory().setItem(13, value);

        
        value = new ItemStack(Material.ANVIL, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Policy");
        value.setItemMeta(m);
        getInventory().setItem(22, value);

        value = new ItemStack(Material.PAPER, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Decisions");
        value.setItemMeta(m);
        getInventory().setItem(21, value);

        value = new ItemStack(Material.CLOCK, 1);
        m = value.getItemMeta();
        m.setDisplayName("§6Party Overview");
        value.setItemMeta(m);
        getInventory().setItem(23, value);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        String option = event.getCurrentItem().getItemMeta().getDisplayName();
        if (option.equalsIgnoreCase("§6Policy")){
            Policy screen = new Policy(player.getUniqueId());
            player.openInventory(screen.getInventory());
        }else if (option.equalsIgnoreCase("§6Decisions")){
            DecisionMenu screen = new DecisionMenu(player.getUniqueId());
            player.openInventory(screen.getInventory());
        }else if (option.equalsIgnoreCase("§6Party Overview")){
            PartyRankingMenu screen = new PartyRankingMenu(player.getUniqueId());
            player.openInventory(screen.getInventory());
        }

        return true;
    }

}
