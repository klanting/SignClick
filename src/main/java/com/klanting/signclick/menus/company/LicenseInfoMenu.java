package com.klanting.signclick.menus.company;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.License;
import com.klanting.signclick.economy.LicenseSingleton;
import com.klanting.signclick.economy.Machine;
import com.klanting.signclick.events.MachineLiveUpdateEvent;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.menus.company.machine.MachineMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.klanting.signclick.events.MenuEvents.activeMachines;
import static com.klanting.signclick.events.MenuEvents.loadStack;

public class LicenseInfoMenu extends SelectionMenu {
    public License license;

    public LicenseInfoMenu(License license){
        super(9, "License info", true);
        this.license = license;

        init();
    }

    public void init(){

        ItemStack cancel = ItemFactory.create(Material.RED_WOOL, "§cCancel License");
        getInventory().setItem(7, cancel);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        String option = event.getCurrentItem().getItemMeta().getDisplayName();

        boolean chiefFrom = license.getFrom().getCOM().getBoard().getChiefPermission("CEO").equals(player.getUniqueId());
        boolean chiefTo = license.getTo().getCOM().getBoard().getChiefPermission("CEO").equals(player.getUniqueId());

        if (!chiefFrom && !chiefTo){
            player.sendMessage("§cOnly the CEO has the permissions for this");
            return false;
        }

        if (option.equals("§cCancel License")){
            LicenseSingleton.getInstance().getCurrentLicenses().removeLicense(license);
            license.getProduct().onDelete(license.getTo());

            List<Machine> toRemove = new ArrayList<>();
            for (Machine machine: activeMachines){
                if (machine.isLicensed() && machine.getLicense() == license){
                    toRemove.add(machine);
                }
            }

            for (Machine machine: toRemove){
                activeMachines.remove(machine);
                machine.clearProgress();

                if (!(machine.getBlock().getState() instanceof TileState tileState)) continue;
                NamespacedKey productKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_product");
                tileState.getPersistentDataContainer().set(productKey, PersistentDataType.STRING, "");
                tileState.update();
            }

            for (MachineMenu mm: MachineLiveUpdateEvent.openMenus){
                mm.init();
            }

            loadStack(player);
        }
        return false;
    }
}
