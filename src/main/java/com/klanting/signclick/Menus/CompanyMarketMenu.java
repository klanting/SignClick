package com.klanting.signclick.Menus;

import com.klanting.signclick.economy.Account;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Market;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

public class CompanyMarketMenu extends SelectionMenu{

    public UUID uuid;
    public List<Company> companies = new ArrayList<>();

    public int currentIndex;

    public CompanyMarketMenu(UUID uuid){
        super(45, "Company Market", false);
        this.uuid = uuid;
        this.companies = Market.getTopMarketAvailable();
        this.currentIndex = 0;
        init();
    }

    public void changePtr(int amount){
        currentIndex += amount;
    }

    @Override
    public void init() {
        /*
        * Check if no companies available
        * */
        getInventory().clear();
        if (companies.isEmpty()){

            ItemStack gearItem = new ItemStack(Material.BARRIER, 1);
            ItemMeta m = gearItem.getItemMeta();
            m.setDisplayName("§cNo Companies available on the market");
            gearItem.setItemMeta(m);

            getInventory().setItem(22, gearItem);

            return;
        }

        /*
        * Set item symbol of company type in the center
        * */

        Map<String, Material> materialMap = new HashMap<>();
        materialMap.put("bank", Material.GOLD_INGOT);
        materialMap.put("transport", Material.MINECART);
        materialMap.put("product", Material.IRON_CHESTPLATE);
        materialMap.put("real estate", Material.QUARTZ_BLOCK);
        materialMap.put("military", Material.BOW);
        materialMap.put("building", Material.BRICKS);
        materialMap.put("other", Material.SUNFLOWER);

        Company currentCompany = companies.get(currentIndex);
        double pct = currentCompany.stockCompareGet();

        ItemStack gearItem = new ItemStack(materialMap.get(currentCompany.type), 1);
        ItemMeta m = gearItem.getItemMeta();

        List<String> lores = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        DecimalFormat df2 = new DecimalFormat("###,###,##0.##");
        lores.add("§7Type: "+currentCompany.type);
        lores.add("§7Current Value: "+df.format(currentCompany.getValue()));
        lores.add("§7Value Change: "+df.format(pct)+"%");
        lores.add("§7Patent Upgrades: "+currentCompany.upgrades.size());

        lores.add("§7Owned Shares: "+df2.format(currentCompany.getCOM().getShareHolders().getOrDefault(uuid, 0)));
        m.setLore(lores);

        m.setDisplayName("§6"+currentCompany.getStockName()+"-"+currentCompany.getName());
        gearItem.setItemMeta(m);

        getInventory().setItem(22, gearItem);

        /*
        * Make Back button and Forward Button
        * */

        if (currentIndex > 0){
            ItemStack backItem = new ItemStack(Material.ARROW, 1);
            m = backItem.getItemMeta();

            m.setDisplayName("Back");
            backItem.setItemMeta(m);

            getInventory().setItem(2*9, backItem);
        }

        if (currentIndex < companies.size()-1){
            ItemStack backItem = new ItemStack(Material.ARROW, 1);
            m = backItem.getItemMeta();

            m.setDisplayName("Next");
            backItem.setItemMeta(m);

            getInventory().setItem(2*9+8, backItem);
        }

        /*
        * set the Pricing
        * */

        List<Triple<Integer, Integer, Material>> buySellButtons = new ArrayList<>();

        buySellButtons.add(Triple.of(1, 11, Material.LIME_DYE));
        buySellButtons.add(Triple.of(10, 12, Material.EMERALD));
        buySellButtons.add(Triple.of(1000, 13, Material.LIME_STAINED_GLASS_PANE));
        buySellButtons.add(Triple.of(10000, 14, Material.LIME_STAINED_GLASS));
        buySellButtons.add(Triple.of(currentCompany.getMarketShares(), 15, Material.LIME_CONCRETE));

        buySellButtons.add(Triple.of(-1, 29, Material.RED_DYE));
        buySellButtons.add(Triple.of(-10, 30, Material.REDSTONE));
        buySellButtons.add(Triple.of(-1000, 31, Material.RED_STAINED_GLASS_PANE));
        buySellButtons.add(Triple.of(-10000, 32, Material.RED_STAINED_GLASS));
        buySellButtons.add(Triple.of(-currentCompany.getCOM().getShareHolders().getOrDefault(uuid, 0),
                33, Material.RED_CONCRETE));

        for (Triple<Integer, Integer, Material> tup: buySellButtons){
            ItemStack button = new ItemStack(tup.getRight(), 1);
            m = button.getItemMeta();

            int amount = tup.getLeft();

            if (amount > 0){
                m.setDisplayName("§a"+"BUY: "+tup.getLeft()+" Share");
            } else if (amount == 0) {
                m.setDisplayName("§fDOES NOTHING");
            } else{
                m.setDisplayName("§c"+"SELL: "+-tup.getLeft()+" Share");
            }

            button.setItemMeta(m);

            getInventory().setItem(tup.getMiddle(), button);
        }

        /*
        * Set great gray glass
        * */
        ItemStack grayGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        m = grayGlass.getItemMeta();
        m.setDisplayName("§f");
        grayGlass.setItemMeta(m);

        for (int i=0; i<5; i++){
            if (i == 2){
                continue;
            }
            getInventory().setItem(20+i, grayGlass);
        }

        grayGlass = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        m = grayGlass.getItemMeta();
        m.setDisplayName("§f");
        grayGlass.setItemMeta(m);
        while (getInventory().firstEmpty() != -1){
            getInventory().setItem(getInventory().firstEmpty(), grayGlass);
        }


    }
}
