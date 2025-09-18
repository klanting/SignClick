package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.interactionLayer.events.AddSupportEvent;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BoardSupportMenu extends SelectionMenu {
    public CompanyI comp;
    private UUID uuid;

    public BoardSupportMenu(UUID uuid, CompanyI company){
        super(54, "Company Board Menu: "+ company.getStockName(), true);
        comp = company;
        this.uuid = uuid;

        init();
    }

    public void init(){
        getInventory().clear();
        for(UUID supported: comp.getCOM().getBoard().getBoardSupport(uuid)){
            ItemStack value = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) value.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(supported);
            meta.setOwningPlayer(player);

            meta.setDisplayName("§6"+player.getName());

            List<String> l = new ArrayList<>();

            DecimalFormat df = new DecimalFormat("##0.00");
            l.add("§7"+df.format((1.0/comp.getCOM().getBoard().getBoardSupport(uuid).size())*100)+"% of your support");

            l.add(comp.getCOM().getBoard().getBoardMembers().contains(supported) ? "§aOn the board":
                    "§cNot on the board");

            meta.setLore(l);

            value.setItemMeta(meta);
            getInventory().setItem(getInventory().firstEmpty(), value);
        }

        ItemStack addButton = ItemFactory.create(Material.WHITE_WOOL, "§7Add support");
        getInventory().setItem(getInventory().firstEmpty(), addButton);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        Material option = event.getCurrentItem().getType();
        if (option.equals(Material.WHITE_WOOL)){
            AddSupportEvent.waitForMessage.put(player, this);
            player.closeInventory();
            player.sendMessage("§bEnter the supported player its username");
        }else{
            String username = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
            UUID uuid = Bukkit.getOfflinePlayer(username).getUniqueId();
            comp.getCOM().getBoard().removeBoardSupport(player.getUniqueId(), uuid);
            init();
        }

        return false;
    }
}
