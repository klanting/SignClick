package com.klanting.signclick.menus.company;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class AuctionMenu extends SelectionMenu {

    public CompanyI comp;

    public AuctionMenu(CompanyI comp, UUID uuid){
        super(9, "Auction: "+ Utils.formatDuration(Auction.getInstance().getWaitTime()/20), true);

        this.comp = comp;
        init();
        startTitleUpdater(Bukkit.getPlayer(uuid));

    }

    public void init(){
        for (int i = 0; i<Auction.getInstance().toBuy.size(); i++){
            PatentUpgrade up = Auction.getInstance().toBuy.get(i);
            ItemStack upgradeItem = new ItemStack(up.material, 1);
            ItemMeta m = upgradeItem.getItemMeta();
            List<String> lores = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###,###,###");
            lores.addAll(up.description());
            lores.add("§7Current Bid: "+df.format(Auction.getInstance().getBit(i)));
            String comp = Auction.getInstance().bitsOwner.get(i);
            if (comp == null){
                comp = "None";
            }
            lores.add("§7Bid by: "+ comp);
            m.setDisplayName(up.name+" "+up.level);
            m.setLore(lores);
            upgradeItem.setItemMeta(m);
            getInventory().setItem(i, upgradeItem);
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        AuctionMenu old_screen = (AuctionMenu) event.getClickedInventory().getHolder();
        int location = event.getSlot();

        int add_price =  SignClick.getConfigManager().getConfig("companies.yml").getInt("auctionBitIncrease");
        if (Auction.getInstance().bitsOwner.get(location) == null){
            add_price = 0;
        }

        int currentBit = Auction.getInstance().getBit(location)+add_price;
        double compValue = Math.min(old_screen.comp.getValue(), old_screen.comp.getSpendable());

        /*
         * Subtract other bits of the max allowed bit
         * */
        for (Map.Entry<Integer, String> entry :Auction.getInstance().bitsOwner.entrySet()){
            if (!Objects.equals(entry.getValue(), old_screen.comp.getStockName())){
                continue;
            }
            if (entry.getKey() == location){
                continue;
            }

            compValue -= Auction.getInstance().getBit(entry.getKey());
        }

        if (compValue < currentBit){
            player.sendMessage("§bCompany is not valued enough to place the current Bid or does not have enough spendable");
            return false;
        }

        Auction.getInstance().setBit(location, currentBit, old_screen.comp.getStockName());
        old_screen.init();

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

                String title = "Auction: "+ Utils.formatDuration(Auction.getInstance().getWaitTime()/20);
                setTitle(title, player);

            }
        };

        titleUpdater.runTaskTimer(SignClick.getPlugin(), 0L, 20L); // every second
    }

}
