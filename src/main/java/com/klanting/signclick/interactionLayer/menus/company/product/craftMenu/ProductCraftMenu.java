package com.klanting.signclick.interactionLayer.menus.company.product.craftMenu;

import com.klanting.signclick.interactionLayer.menus.company.product.ProductList;
import com.klanting.signclick.interactionLayer.menus.company.product.ProductType;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.producible.License;
import com.klanting.signclick.logicLayer.companyLogic.producible.Producible;
import com.klanting.signclick.logicLayer.companyLogic.producible.Product;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import com.klanting.signclick.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.klanting.signclick.interactionLayer.events.MenuEvents.loadStack;

public class ProductCraftMenu extends SelectionMenu {
    public CompanyI comp;
    private CraftState state = new CraftStateCraftingTable();

    public ProductCraftMenu(UUID uuid, CompanyI company){
        super(45, "Product Crafting: "+ company.getStockName(), true);
        comp = company;

        assert comp.getCOM().isOwner(uuid);
        init();
    }



    public void init(){
        getInventory().clear();

        ItemStack recipe = Utils.simulateCraft(Arrays.stream(state.getProducts()).
                map(p -> p != null ? new ItemStack(p.getMaterial()): null).toArray(ItemStack[]::new));

        for (int i: state.getCraftCoverSlots()){
            getInventory().setItem(i, ItemFactory.create(Material.YELLOW_STAINED_GLASS_PANE, "§f"));
        }

        int counter = 0;
        for (int i: state.getCraftSlots()){
            if (state.getProducts()[counter] == null){
                getInventory().setItem(i, ItemFactory.create(Material.LIGHT_GRAY_DYE, "§7Crafting Slot"));
            }else{
                List<String> l = new ArrayList<>();
                l.add("§7Production Time: "+state.getProducts()[counter].getProductionTime()+"s");
                l.add("§7Cost: $"+state.getProducts()[counter].getPrice());
                getInventory().setItem(i,
                        ItemFactory.create(state.getProducts()[counter].getMaterial(),
                                "§7"+state.getProducts()[counter].getMaterial().name(), l));
            }

            counter += 1;
        }

        for (int i: List.of(15, 16, 17, 24, 26, 33, 34, 35)){
            getInventory().setItem(i, ItemFactory.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§f"));
        }

        if (recipe != null){
            Product product = state.getCrafted();
            List<String> l = new ArrayList<>();
            l.add("§7Production Time: "+product.getProductionTime()+"s");
            l.add("§7Cost: $"+product.getPrice());
            getInventory().setItem(25, ItemFactory.create(recipe.getType(), "§7"+recipe.getType().name(),
                    l));
        }

        getInventory().setItem(43, ItemFactory.create(Material.LIME_WOOL, "§a✓ Save Product"));

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        String option = event.getCurrentItem().getItemMeta().getDisplayName();

        if (option.equals("§7Crafting Slot") || state.getCraftSlots().contains(event.getSlot())){

            int slot = (event.getSlot()/9 -1)*3+((event.getSlot()-1)%3);

            if (state.getProducts()[slot] != null){
                state.setCrafted(slot, null);
                init();
                return false;
            }

            Function<Producible, Void> lambda = (p) -> {
                Product prod;
                if((p instanceof Product p2)){
                    prod = p2;
                }else{
                    prod = ((License) p).getProduct();
                }

                state.setCrafted(slot, prod);
                init();
                loadStack(player);
                return null;};

            ProductList new_screen = new ProductList(comp, lambda, false, ProductType.allOwned);
            player.openInventory(new_screen.getInventory());
        }else if(option.equals("§a✓ Save Product")) {

            if (comp.getProducts().size() >= comp.getUpgrades().get(2).getBonus()){
                player.sendMessage("§cYou don't have any free product slots. Used: "+comp.getProducts().size()
                        +"/"+comp.getUpgrades().get(2).getBonus()+" (Research products are always added)");
                player.closeInventory();
                return false;
            }

            Product product = state.getCrafted();

            /*
            * link new product to old
            * */
            for(Product product1: state.getProducts()){
                if(product1 == null){
                    continue;
                }

                product1.addUsedFor(product);
            }

            if (product != null){
                comp.addProduct(product);
                loadStack(player);
            }

            return false;

        }else{
            /*
             * Only return if no slot selected
             * */
            return false;
        }

        return true;
    }
}
