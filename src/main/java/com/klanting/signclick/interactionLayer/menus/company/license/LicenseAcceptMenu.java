package com.klanting.signclick.interactionLayer.menus.company.license;

import com.klanting.signclick.logicLayer.companyLogic.producible.License;
import com.klanting.signclick.logicLayer.companyLogic.producible.LicenseSingleton;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.klanting.signclick.interactionLayer.events.MenuEvents.loadStack;

public class LicenseAcceptMenu extends SelectionMenu {
    public final License license;

    public LicenseAcceptMenu(License license){
        super(27, "License request decision", true);
        this.license = license;

        init();
    }

    public void init(){

        DecimalFormat df = new DecimalFormat("###,###,##0.00");

        List<String> l = new ArrayList<>();
        l.add("§7Production Time: "+license.getProduct().getProductionTime()+"s");
        l.add("§7Weekly License cost: $"+license.getWeeklyCost());
        l.add("");
        l.add("§7Normal Cost: $"+license.getProduct().getPrice());
        l.add("§7Increased Cost: "+df.format(license.getCostIncrease()*100)+"%");
        l.add("§7Royalty Fee: "+df.format(license.getRoyaltyFee()*100)+"%");
        l.add("§7Production Cost: $"+license.getPrice());
        ItemStack productItem = ItemFactory.create(license.getProduct().getMaterial(), "§7License Request", l);

        getInventory().setItem(13, productItem);

        getInventory().setItem(12, ItemFactory.create(Material.RED_WOOL, "§c✘ Deny License"));
        getInventory().setItem(14, ItemFactory.create(Material.LIME_WOOL, "§a✓ Accept License"));

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        boolean chiefFrom = license.getFrom().getCOM().getBoard().getChiefPermission("CEO").equals(player.getUniqueId());

        if (!chiefFrom){
            player.sendMessage("§cOnly the CEO has the permissions for this");
            return false;
        }

        int slot = event.getSlot();

        if (slot == 12){
            LicenseSingleton.getInstance().getLicenseRequests().removeLicense(license);
            loadStack(player);

        }

        if (slot == 14){
            LicenseSingleton.getInstance().getLicenseRequests().removeLicense(license);
            LicenseSingleton.getInstance().getCurrentLicenses().addLicense(license);

            loadStack(player);
        }
        return false;
    }
}
