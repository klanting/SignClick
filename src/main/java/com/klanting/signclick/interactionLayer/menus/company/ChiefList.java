package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChiefList extends SelectionMenu {

    public CompanyI comp;

    public ChiefList(UUID uuid, CompanyI company){
        super(9, "Company Chief List", true);
        comp = company;

        assert comp.getCOM().getBoard().getBoardMembers().contains(uuid);

        init();
    }

    public void init(){

        List<String> chiefList = new ArrayList<>();
        chiefList.add("CEO");
        chiefList.add("CTO");
        chiefList.add("CFO");

        for(int i= 0; i<chiefList.size(); i++){
            UUID chief = comp.getCOM().getBoard().getChief(chiefList.get(i));

            ItemStack chiefButton;
            if (chief == null){
                chiefButton = ItemFactory.create(Material.IRON_HELMET, "§7"+chiefList.get(i)+": Unassigned");
            }else{
                chiefButton = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) chiefButton.getItemMeta();
                OfflinePlayer player = Bukkit.getOfflinePlayer(chief);
                meta.setOwningPlayer(player);

                meta.setDisplayName("§7"+chiefList.get(i)+": "+player.getName());

                chiefButton.setItemMeta(meta);
            }

            getInventory().setItem(2+i*2, chiefButton);
        }

        super.init();

    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        if (event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)){
            return false;
        }

        String option = event.getCurrentItem().getItemMeta().getDisplayName().substring(2, 5);

        ChiefMenu newScreen = new ChiefMenu(player.getUniqueId(), comp, option);
        player.openInventory(newScreen.getInventory());
        return true;
    }
}
