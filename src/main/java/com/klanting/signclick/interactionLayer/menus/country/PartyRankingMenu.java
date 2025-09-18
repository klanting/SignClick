package com.klanting.signclick.interactionLayer.menus.country;

import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.parties.Party;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyRankingMenu extends SelectionMenu {

    private final UUID uuid;
    /*
    * System to decide the location of the party based on the ranking
    * It forms a particular shape
    * */
    private static List<Integer> rankingPosition = List.of(22, 23, 21, 14, 30, 24, 20, 12, 32, 3, 41, 25, 19,
            16, 28, 7, 37);

    public PartyRankingMenu(UUID uuid){
        super(45, "Party influence", true);
        this.uuid = uuid;
        init();
    }

    public void init(){
        Country country = CountryManager.getCountry(uuid);
        if (country == null){
            return;
        }

        List<Party> sortedParties = country.getParties().stream().sorted((p, i) -> (int) p.getPCT()).toList();

        for (int i = 0; i<Math.min(sortedParties.size(),rankingPosition.size()); i++){

            Integer pos = rankingPosition.get(i);
            Party party = sortedParties.get(i);
            if (party.owners.isEmpty()){
                continue;
            }

            UUID uuid = party.owners.get(0);

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            meta.setOwningPlayer(player);

            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            List<String> l = new ArrayList<>();
            l.add("§7Owners:");
            for (UUID uuid1: party.owners){
                l.add("§7- "+Bukkit.getOfflinePlayer(uuid1).getName());
            }
            meta.setLore(l);
            meta.setDisplayName("§7"+party.name+": "+df.format(party.getPCT()*100)+"%");

            playerHead.setItemMeta(meta);

            getInventory().setItem(pos, playerHead);
        }

        super.init();
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        return false;
    }
}
