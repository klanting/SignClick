package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.logicLayer.CompanyI;
import com.klanting.signclick.logicLayer.License;
import com.klanting.signclick.logicLayer.Produceable;
import com.klanting.signclick.logicLayer.Product;
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

    public Product[] products = new Product[9];

    public ProductCraftMenu(UUID uuid, CompanyI company){
        super(45, "Product Crafting: "+ company.getStockName(), true);
        comp = company;

        assert comp.getCOM().isOwner(uuid);
        init();
    }

    public Product getCrafted(){
        ItemStack recipe = Utils.simulateCraft(Arrays.stream(products).
                map(p -> p != null ? new ItemStack(p.getMaterial()): null).toArray(ItemStack[]::new));

        if (recipe == null){
            return null;
        }

        int time = 0;
        int cost = 0;

        for (Product product: products){
            if (product == null){
                continue;
            }
            time += product.getProductionTime();
            cost += product.getPrice();
        }

        return new Product(recipe.getType(), (double) cost/recipe.getAmount(), Math.max(time/recipe.getAmount(), 1));
    }

    public void init(){

        ItemStack recipe = Utils.simulateCraft(Arrays.stream(products).
                map(p -> p != null ? new ItemStack(p.getMaterial()): null).toArray(ItemStack[]::new));

        getInventory().clear();

        for (int i: List.of(0, 1, 2, 3, 4, 9, 13, 18, 22, 27, 31, 36, 37, 38, 39, 40)){
            getInventory().setItem(i, ItemFactory.create(Material.YELLOW_STAINED_GLASS_PANE, "§f"));
        }

        int counter = 0;
        for (int i: List.of(10, 11, 12, 19, 20, 21, 28, 29, 30)){
            if (products[counter] == null){
                getInventory().setItem(i, ItemFactory.create(Material.LIGHT_GRAY_DYE, "§7Crafting Slot"));
            }else{
                List<String> l = new ArrayList<>();
                l.add("§7Production Time: "+products[counter].getProductionTime()+"s");
                l.add("§7Cost: $"+products[counter].getPrice());
                getInventory().setItem(i,
                        ItemFactory.create(products[counter].getMaterial(),
                                "§7"+products[counter].getMaterial().name(), l));
            }

            counter += 1;
        }

        for (int i: List.of(15, 16, 17, 24, 26, 33, 34, 35)){
            getInventory().setItem(i, ItemFactory.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "§f"));
        }

        if (recipe != null){
            Product product = getCrafted();
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

        if (option.equals("§7Crafting Slot") || (1 <= event.getSlot()/9 && event.getSlot()/9 <= 3) &&
                (event.getSlot()-1)%3 <= 2 && 0 <= (event.getSlot()-1)%3){

            int slot = (event.getSlot()/9 -1)*3+((event.getSlot()-1)%3);

            if (products[slot] != null){
                products[slot] = null;
                init();
                return false;
            }

            Function<Produceable, Void> lambda = (p) -> {
                Product prod;
                if((p instanceof Product p2)){
                    prod = p2;
                }else{
                    prod = ((License) p).getProduct();
                }

                products[slot] = prod;
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

            Product product = getCrafted();

            /*
            * link new product to old
            * */
            for(Product product1: products){
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
