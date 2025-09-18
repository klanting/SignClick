package com.klanting.signclick.interactionLayer.menus.country;

import com.klanting.signclick.logicLayer.Country;
import com.klanting.signclick.logicLayer.CountryManager;
import com.klanting.signclick.logicLayer.policies.PolicyOption;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.klanting.signclick.utils.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Policy extends SelectionMenu {

    private final UUID uuid;

    public Policy(UUID uuid){
        super(54, "Country Policy", true);
        this.uuid = uuid;
        init();
    }

    public void init(){
        List<String> policyLore = new ArrayList<>();

        Country country = CountryManager.getCountry(uuid);

        for(Map.Entry<String, Double> entry: country.getPolicyBonusMap().entrySet()){
            policyLore.add("§7"+ PolicyOption.translationMethod(entry.getKey(), entry.getValue()));
        }

        getInventory().setItem(4, ItemFactory.create(Material.DIAMOND, "§6Policy Modifiers", policyLore));

        int startIndex = 11;

        for (com.klanting.signclick.logicLayer.policies.Policy p: country.getPolicies()){
            ItemStack item = new ItemStack(p.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6"+p.getName());
            item.setItemMeta(meta);

            getInventory().setItem(startIndex-1, item);
            for (int i=0; i<5; i++){

                ItemStack color;
                if (i != p.getLevel()){
                    color = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                }else{
                    color = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                }

                ItemMeta m = color.getItemMeta();

                int visual_pct = 51;
                if (i == 0 || i == 4){
                    visual_pct = 70;
                }

                m.setDisplayName("§6"+p.getTitle(i));

                List<String> lore_list = new ArrayList<>();
                lore_list.add("§9"+visual_pct+"% Approval needed");

                lore_list.addAll(p.getDescription(i));

                m.setLore(lore_list);
                color.setItemMeta(m);

                getInventory().setItem(startIndex+i, color);

            }

            startIndex += 9;
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        event.setCancelled(true);
        Policy old_screen = (Policy) event.getClickedInventory().getHolder();
        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();
        int row = slot/9;
        int level = slot - 9*row-2;
        if (level < 0){
            return false;
        }

        Country country = CountryManager.getCountry(player);

        country.setPolicies(row-1, level);

        old_screen.init();
        player.sendMessage("§bPolicy change Decision has been passed on");
        return true;
    }

}
