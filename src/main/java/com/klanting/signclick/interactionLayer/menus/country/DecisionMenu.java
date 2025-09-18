package com.klanting.signclick.interactionLayer.menus.country;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.countryLogic.decisions.Decision;
import com.klanting.signclick.logicLayer.countryLogic.decisions.DecisionAboardMilitary;
import com.klanting.signclick.logicLayer.countryLogic.decisions.DecisionForbidParty;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DecisionMenu extends SelectionMenu {
    private UUID uuid;

    public DecisionMenu(UUID uuid){
        super(27, "Country Decision Menu", true);
        this.uuid = uuid;
        init();
    }

    public void init(){
        ItemStack value;

        List<String> lores = new ArrayList<>();
        lores.add("§7REQUIRES 40 stability");
        value = ItemFactory.create(Material.RED_BANNER, "§6Ban party", lores);
        getInventory().setItem(12, value);

        String name = "§6Forbid party";
        Country country = CountryManager.getCountry(uuid);

        if (country.isForbidParty()){
            name = "§6Allow party";
        }

        lores = new ArrayList<>();
        lores.add("§7REQUIRES 30 stability");
        value = ItemFactory.create(Material.IRON_BARS, name, lores);
        getInventory().setItem(13, value);

        name = "§6Abort military payments";

        if (country.isAboardMilitary()){
            name = "§6Allow military payments";
        }
        value = ItemFactory.create(Material.IRON_SWORD, name);
        getInventory().setItem(14, value);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        String option = event.getCurrentItem().getItemMeta().getDisplayName();
        if (option.equalsIgnoreCase("§6Ban party")){
            Country country = CountryManager.getCountry(player);

            if (country.getCountryElection() != null){
                player.sendMessage(SignClick.getPrefix()+"you can`t ban parties during elections");
                return false;
            }
            PartyBan screen = new PartyBan(player.getUniqueId());
            player.openInventory(screen.getInventory());
        }else if (option.equalsIgnoreCase("§6Forbid party") || option.equalsIgnoreCase("§6Allow party")){
            String name;
            if (option.equalsIgnoreCase("§6Allow party")){
                name = "§6Allow Parties";
            }else{
                name = "§6Forbid Parties";
            }

            Country country = CountryManager.getCountry(player);


            boolean go_to = !country.isForbidParty();

            if (country.getStability() < 30.0){
                player.sendMessage("§brequired stability is 30");
                return false;
            }

            Decision d = new DecisionForbidParty(name, 0.5, country.getName(), go_to);
            country.addDecision(d);
        }else if (option.equalsIgnoreCase("§6Abort military payments") || option.equalsIgnoreCase("§6Allow military payments")){
            String name;
            if (option.equalsIgnoreCase("§6Allow military payments")){
                name = "§6Allow military payments";
            }else{
                name = "§6Abort military payments";
            }

            Country country = CountryManager.getCountry(player);

            boolean go_to = !country.isAboardMilitary();

            Decision d = new DecisionAboardMilitary(name, 0.5, country.getName(), go_to);
            country.addDecision(d);

        }

        return true;
    }

}
