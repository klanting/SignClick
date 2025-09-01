package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.events.AddEmployeeEvent;
import com.klanting.signclick.events.AddSupportEvent;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmployeesList extends PagingMenu {
    public CompanyI comp;

    public EmployeesList(CompanyI company){
        super(54, "Company Employees List", true);
        comp = company;
        init();
    }

    public void init(){
        clearItems();

        for(UUID uuid: comp.getCOM().getEmployees()){
            ItemStack value = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) value.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            meta.setOwningPlayer(player);

            meta.setDisplayName("§7"+player.getName());
            value.setItemMeta(meta);

            addItem(value);
        }

        ItemStack assignButton = ItemFactory.create(Material.WHITE_WOOL, "§7Add employee");
        addItem(assignButton);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        Material option = event.getCurrentItem().getType();

        if (option.equals(Material.WHITE_WOOL)){
            AddEmployeeEvent.waitForMessage.put(player, this);
            player.closeInventory();
            player.sendMessage("§bEnter the supported player its username");
        }else{
            String username = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
            UUID uuid = Bukkit.getOfflinePlayer(username).getUniqueId();
            comp.getCOM().removeEmployee(uuid);
            init();
        }

        return true;
    }
}
