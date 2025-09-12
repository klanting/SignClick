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
import org.apache.commons.lang3.tuple.Pair;

public class ProductList extends PagingMenu {

    public final CompanyI comp;
    public final Function<Produceable, Void> func;
    public final boolean fullList;

    private final ProductType productType;

    public ProductList(CompanyI comp, Function<Produceable, Void> func, boolean fullList,
                       ProductType productType){
        super(54, "Product List: "+
                        (productType.equals(ProductType.allOwned) ?
                                (comp.getProducts().size()+"/"+comp.getUpgrades().get(2).getBonus()+" slots"): ""),
                true);
        this.comp = comp;
        this.func = func;
        this.fullList = fullList;

        this.productType = productType;

        init();
    }

    private Pair<List<Product>, List<License>> getProduceables(){
        List<Product> products = new ArrayList<>();
        List<License> licenses = new ArrayList<>();

        switch (productType){
            case allOwned:
                products = comp.getProducts();
                licenses = LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp);
                break;
            case allOwnedProducts:
                products = comp.getProducts();
                break;
            case allOwnedLicenses:
                licenses = LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp);
                break;
            case licenseRequestsFrom:
                licenses = LicenseSingleton.getInstance().getLicenseRequests().getLicensesFrom(comp);
                break;
            case licenseFrom:
                licenses = LicenseSingleton.getInstance().getCurrentLicenses().getLicensesFrom(comp);
                break;
        }

        return Pair.of(products, licenses);
    }

    public void init(){
        clearItems();

        List<Product> products = getProduceables().getLeft();
        List<License> licenses = getProduceables().getRight();

        for (Product product: products){
            List<String> l = new ArrayList<>();
            l.add("§7Production Time: "+product.getProductionTime()+"s");
            l.add("§7Cost: $"+product.getPrice());
            ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
            addItem(item);
        }

        for (License license: licenses){
            Product product = license.getProduct();

            DecimalFormat df = new DecimalFormat("###,###,##0.00");

            List<String> l = new ArrayList<>();
            l.add("§7Production Time: "+product.getProductionTime()+"s");
            l.add("§7Cost: $"+df.format(product.getPrice()*(1.0+license.getRoyaltyFee()+license.getCostIncrease())));

            if(productType.equals(ProductType.allOwned) || productType.equals(ProductType.allOwnedLicenses)){
                l.add("§cThis Product is Licensed from "+license.getFrom().getStockName());
            }

            if(productType.equals(ProductType.licenseRequestsFrom)){
                l.add("§cRequested by "+license.getTo().getStockName());
            }

            if(productType.equals(ProductType.licenseFrom)){
                l.add("§cGiven to: "+license.getTo().getStockName());
            }

            l.add("§7Weekly License cost: $"+license.getWeeklyCost());
            l.add("§7Cost increase: $"+df.format(license.getCostIncrease()*100)+"%");
            l.add("§7Royalty Fee: $"+df.format(license.getRoyaltyFee()*100)+"%");

            ItemStack item = ItemFactory.create(product.getMaterial(), "§7"+product.getMaterial().name(), l);
            addItem(item);
        }


        super.init();

        if (fullList){
            int licenseRequestSize = LicenseSingleton.getInstance().getLicenseRequests().getLicensesFrom(comp).size();
            int licenseGivenSize = LicenseSingleton.getInstance().getCurrentLicenses().getLicensesFrom(comp).size();
            int licenseReceivedSize = LicenseSingleton.getInstance().getCurrentLicenses().getLicensesTo(comp).size();

            ItemStack book = ItemFactory.create(Material.BOOK, "§7Request Licenses");
            ItemStack writeBook = ItemFactory.create(Material.WRITABLE_BOOK, "§7See License Requests",
                    List.of("§7Pending requests: "+licenseRequestSize));
            ItemStack bookShelf = ItemFactory.create(Material.BOOKSHELF, "§7Received License List",
                    List.of("§7Received licenses: "+licenseReceivedSize));
            ItemStack enchantmentTable = ItemFactory.create(Material.ENCHANTING_TABLE, "§7Given License List",
                    List.of("§7Given licenses: "+licenseGivenSize));

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

        List<Product> products = getProduceables().getLeft();
        List<License> licenses = getProduceables().getRight();

        if (event.getSlot() < 45){
            int index = (getPage()*45+item);
            int productSize = products.size();
            func.apply(index < productSize ?
                    products.get(index):
                    licenses.get(index-productSize));

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
                    false, ProductType.allOwnedLicenses);
            player.openInventory(new_screen.getInventory());
        }else if (event.getSlot() == 51){

            Function<Produceable, Void> func = (potentialLicense) -> {
                License license = (License) potentialLicense;

                LicenseAcceptMenu acceptMenu = new LicenseAcceptMenu(license);
                player.openInventory(acceptMenu.getInventory());
                return null;
            };

            ProductList newScreen = new ProductList(comp, func, false, ProductType.licenseRequestsFrom);
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
                        false, ProductType.allOwnedProducts);
                player.openInventory(new_screen2.getInventory());

                return null;
            }, comp);
            player.openInventory(new_screen.getInventory());
        }else if (event.getSlot() == 49){
            Function<Produceable, Void> func = (potentialLicense) -> {
                License license = (License) potentialLicense;
                LicenseInfoMenu screen = new LicenseInfoMenu(license);
                player.openInventory(screen.getInventory());
                return null;
            };
            ProductList newScreen = new ProductList(comp, func, false, ProductType.licenseFrom);
            player.openInventory(newScreen.getInventory());
        }else{
            return false;
        }
        return true;
    }

}
