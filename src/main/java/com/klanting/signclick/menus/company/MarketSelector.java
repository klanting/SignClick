package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.klanting.signclick.events.MenuEvents.clearStack;

public class MarketSelector extends PagingMenu {
    public UUID uuid;

    public MarketSelector(UUID uuid){
        super(54, "Company Market Selector", false);
        this.uuid = uuid;
        init();
    }

    @Override
    public void init() {
        ItemStack item;

        clearItems();
        for(CompanyI c: Market.getTopMarketAvailable()){

            item = new ItemStack(Utils.getCompanyTypeMaterial(c.getType()),1);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName("§6"+c.getName()+" ["+c.getStockName()+"]");

            DecimalFormat df2 = new DecimalFormat("0.00");

            Integer shares = Market.getAccount(uuid).shares.getOrDefault(c.getStockName(), 0);

            List<String> lore = new ArrayList<>();
            lore.add("§7Owned Shares: "
                    + shares
                    + " ("+df2.format((shares.doubleValue()/c.getTotalShares().doubleValue()*100.0))+"%)\n");
            m.setLore(lore);
            item.setItemMeta(m);

            addItem(item);
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        clearStack(player);

        if (event.getCurrentItem().getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE){
            return false;
        }

        int startPos = event.getCurrentItem().getItemMeta().getDisplayName().indexOf("[");
        int endPos = event.getCurrentItem().getItemMeta().getDisplayName().length()-1;
        CompanyI company = Market.getCompany(event.getCurrentItem().getItemMeta().getDisplayName().substring(startPos+1, endPos));

        MarketMenu screen = new MarketMenu(player.getUniqueId(), company);

        player.openInventory(screen.getInventory());

        return true;

    }
}
