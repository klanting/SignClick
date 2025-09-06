package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.*;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ProductList extends PagingMenu {

    public final CompanyI comp;
    public final Function<Produceable, Void> func;
    public final boolean fullList;

    public final boolean showProducts;
    public final boolean showLicenses;

    public ProductList(CompanyI comp, Function<Produceable, Void> func){
        this(comp, func, false);
    }

    public ProductList(CompanyI comp, Function<Produceable, Void> func, boolean fullList){
        this(comp, func, fullList, true, false);
    }

    public ProductList(CompanyI comp, Function<Produceable, Void> func, boolean fullList,
                       boolean showProducts, boolean showLicenses){
        super(54, "Product List: "+comp.getProducts().size()+"/"+comp.getUpgrades().get(2).getBonus()+" slots",
                true);
        this.comp = comp;
        this.func = func;
        this.fullList = fullList;
        this.showProducts = showProducts;
        this.showLicenses = showLicenses;

        init();
    }

    public void init(){
        clearItems();

        if (showProducts){
            for (Product product: comp.getProducts()){
                List<String> l = new ArrayList<>();
                l.add("§7Production Time: "+product.getProductionTime()+"s");
                l.add("§7Cost: $"+product.getPrice());
                ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
                addItem(item);
            }
        }

        if (showLicenses){
            for (License license: LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp)){
                Product product = license.getProduct();

                DecimalFormat df = new DecimalFormat("###,###,##0.00");

                List<String> l = new ArrayList<>();
                l.add("§7Production Time: "+product.getProductionTime()+"s");
                l.add("§7Cost: $"+df.format(product.getPrice()*(1.0+license.getRoyaltyFee()+license.getCostIncrease())));
                l.add("§cThis Product is Licensed from "+license.getFrom().getStockName());
                l.add("§7Weekly License cost: $"+license.getWeeklyCost());
                ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
                addItem(item);
            }
        }


        super.init();

        if (fullList){

            ItemStack book = ItemFactory.create(Material.BOOK, "§7Request Licenses");
            ItemStack writeBook = ItemFactory.create(Material.WRITABLE_BOOK, "§7See License Requests");
            ItemStack bookShelf = ItemFactory.create(Material.BOOKSHELF, "§7Received License List");
            ItemStack enchantmentTable = ItemFactory.create(Material.ENCHANTING_TABLE, "§7Given License List");

            getInventory().setItem(52, book);
            getInventory().setItem(51, writeBook);
            getInventory().setItem(50, bookShelf);
            getInventory().setItem(49, enchantmentTable);
        }
    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        int item = event.getSlot();

        if(event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)){
            return false;
        }

        if (event.getSlot() < 45){
            int index = (getPage()*45+item);
            int productSize = comp.getProducts().size();
            func.apply(index < productSize ?
                    comp.getProducts().get(index):
                    LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp).get(index-productSize));

            init();
            return false;
        }

        if (event.getSlot() == 50){
            ProductList new_screen = new ProductList(comp, s -> {

                if (!(s instanceof License l)){
                    return null;
                }

                LicenseInfoMenu screen = new LicenseInfoMenu(l);
                player.openInventory(screen.getInventory());
                return null;
            },
                    false, false, true);
            player.openInventory(new_screen.getInventory());
        }else if (event.getSlot() == 51){

            Function<License, Void> func = (license) -> {
                LicenseAcceptMenu acceptMenu = new LicenseAcceptMenu(license);
                player.openInventory(acceptMenu.getInventory());
                return null;
            };

            LicenseRequestList newScreen = new LicenseRequestList(comp, func);
            player.openInventory(newScreen.getInventory());
        }else if (event.getSlot() == 52){
            Selector new_screen = new Selector(player.getUniqueId(), comp2 -> {

                ProductList new_screen2 = new ProductList(comp2, p -> {
                    if(!(p instanceof Product s)){
                        return null;
                    }
                    LicenseRequestMenu newScreen = new LicenseRequestMenu(player.getUniqueId(), comp2, comp, s);
                    player.openInventory(newScreen.getInventory());
                    return null;
                },
                        false, true, false);
                player.openInventory(new_screen2.getInventory());

                return null;
            }, comp);
            player.openInventory(new_screen.getInventory());
        }else if (event.getSlot() == 49){
            Function<License, Void> func = (license) -> {
                LicenseInfoMenu screen = new LicenseInfoMenu(license);
                player.openInventory(screen.getInventory());
                return null;
            };
            LicenseGivenList newScreen = new LicenseGivenList(comp, func);
            player.openInventory(newScreen.getInventory());
        }else{
            return false;
        }
        return true;
    }

}
