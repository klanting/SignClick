package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.logicLayer.CompanyI;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PatentIDMenu extends SelectionMenu {

    public CompanyI comp;

    public Boolean designer;


    public PatentIDMenu(CompanyI comp, Boolean designer){
        super(27, "Company Patent Selector", true);
        this.comp = comp;
        this.designer = designer;
        init();
    }

    public void init(){
        int size = comp.getUpgrades().get(0).getBonus();
        for (int i=0; i<size; i++){

            ItemStack parent;
            if (comp.getPatent().size() > i){
                parent = new ItemStack(comp.getPatent().get(i).item,1);

                ItemMeta m = parent.getItemMeta();
                m.setDisplayName("§6"+comp.getPatent().get(i).getName());
                parent.setItemMeta(m);
            }else{
                parent = new ItemStack(Material.LIGHT_GRAY_DYE,1);

                ItemMeta m = parent.getItemMeta();
                m.setDisplayName("§6Empty Patent");
                parent.setItemMeta(m);

            }

            getInventory().setItem(getInventory().firstEmpty(), parent);

        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        String option = event.getCurrentItem().getItemMeta().getDisplayName();

        if (designer){
            if (option.equalsIgnoreCase("§6Empty Patent")){
                PatentSelectorMenu new_screen = new PatentSelectorMenu(comp);
                player.openInventory(new_screen.getInventory());
            }else{
                PatentDesignerMenu new_screen = new PatentDesignerMenu(comp.getPatent().get(event.getSlot()), comp);
                player.openInventory(new_screen.getInventory());
            }
        }else{
            if (!option.equalsIgnoreCase("§6Empty Patent")){
                PatentCrafting new_screen = new PatentCrafting(comp, comp.getPatent().get(event.getSlot()));
                player.openInventory(new_screen.getInventory());
            }
        }
        return true;
    }
}
