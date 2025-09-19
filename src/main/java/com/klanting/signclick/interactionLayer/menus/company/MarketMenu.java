package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.Account;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.interactionLayer.menus.SelectionMenu;
import com.klanting.signclick.utils.Utils;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

public class MarketMenu extends SelectionMenu {

    public UUID uuid;
    public CompanyI currentCompany;

    public MarketMenu(UUID uuid, CompanyI company, boolean backButton){
        super(45, "Company Market", backButton);
        this.uuid = uuid;
        this.currentCompany = company;
        init();
    }


    public MarketMenu(UUID uuid, CompanyI company){
        this(uuid, company, true);
    }

    @Override
    public void init() {
        /*
        * Check if no companies available
        * */
        ItemStack backButton = getInventory().getItem(44);
        getInventory().clear();
        getInventory().setItem(44, backButton);

        /*
        * Set item symbol of company type in the center
        * */

        double pct = currentCompany.stockCompareGet();

        ItemStack gearItem = new ItemStack(Utils.getCompanyTypeMaterial(currentCompany.getType()), 1);
        ItemMeta m = gearItem.getItemMeta();

        DecimalFormat df2 = new DecimalFormat("###,###,##0.##");
        String marketShares = currentCompany.getCOM().getMarketShares() >= 0 ? df2.format(currentCompany.getCOM().getMarketShares()) : "inf";

        List<String> lores = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("###,###,##0.00");

        lores.add("§7Type: §f"+currentCompany.getType());
        lores.add("§7Owned Shares: §f"+df2.format(currentCompany.getCOM().getShareHolders().getOrDefault(uuid, 0)));
        lores.add("§7Market Shares: §f"+marketShares+"/"+df2.format(currentCompany.getCOM().getTotalShares()));
        lores.add("");
        lores.add("§7Current Value: §f"+df.format(currentCompany.getValue()));
        lores.add("§7Value Change: "+(pct > 0 ? "§a": "§c")+df.format(pct)+"%");
        lores.add("");
        lores.add("§7Products: §f"+currentCompany.getProducts().size());
        lores.add("§7Patent Upgrades: §f"+currentCompany.getPatentUpgrades().size());

        m.setLore(lores);

        m.setDisplayName("§6§l"+currentCompany.getStockName()+"-"+currentCompany.getName());
        gearItem.setItemMeta(m);

        getInventory().setItem(22, gearItem);

        /*
        * set the Pricing
        * */

        List<Triple<Integer, Integer, Material>> buySellButtons = new ArrayList<>();

        List<Integer> buySellAmount = SignClick.getConfigManager().getConfig("companies.yml").getIntegerList("stockBuySellAmount");
        assert buySellAmount.size() == 3;

        buySellButtons.add(Triple.of(1, 11, Material.LIME_DYE));
        buySellButtons.add(Triple.of(buySellAmount.get(0), 12, Material.EMERALD));
        buySellButtons.add(Triple.of(buySellAmount.get(1), 13, Material.LIME_STAINED_GLASS_PANE));
        buySellButtons.add(Triple.of(buySellAmount.get(2), 14, Material.LIME_STAINED_GLASS));
        buySellButtons.add(Triple.of(currentCompany.getCOM().isOpenTrade() ? 0:currentCompany.getMarketShares(), 15, Material.LIME_CONCRETE));

        buySellButtons.add(Triple.of(-1, 29, Material.RED_DYE));
        buySellButtons.add(Triple.of(-buySellAmount.get(0), 30, Material.REDSTONE));
        buySellButtons.add(Triple.of(-buySellAmount.get(1), 31, Material.RED_STAINED_GLASS_PANE));
        buySellButtons.add(Triple.of(-buySellAmount.get(2), 32, Material.RED_STAINED_GLASS));
        buySellButtons.add(Triple.of(-currentCompany.getCOM().getShareHolders().getOrDefault(uuid, 0),
                33, Material.RED_CONCRETE));

        for (Triple<Integer, Integer, Material> tup: buySellButtons){
            ItemStack button = new ItemStack(tup.getRight(), 1);
            m = button.getItemMeta();

            int amount = tup.getLeft();

            if (amount > 0 && !currentCompany.getCOM().isOpenTrade()){
                amount = Math.min(amount, currentCompany.getMarketShares());
            }

            if (amount < 0){
                amount = Math.max(amount, -currentCompany.getCOM().getShareHolders().getOrDefault(uuid, 0));
            }
            List<String> buttonLore = new ArrayList<>();

            String message;
            if (amount > 0){
                message = "§a§l"+"BUY: "+amount+" Shares";
                buttonLore.add("§7BUY PRICE: §f"+ df.format(Market.getBuyPrice(currentCompany.getStockName(), amount)));
            } else if (amount == 0) {
                message = "§f§lDOES NOTHING";
            } else{
                message = "§c§l"+"SELL: "+-amount+" Shares";
                buttonLore.add("§7SELL PRICE: §f"+ df.format(Market.getSellPrice(currentCompany.getStockName(),- amount)));
            }

            if (Math.abs(amount) == 1){
                /*
                * removes the last 's' of shares
                * */
                message = message.substring(0, message.length()-1);
            }

            m.setDisplayName(message);
            m.setLore(buttonLore);

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

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        Account acc = Market.getAccount(player);

        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("BUY")){
            int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);

            if(!currentCompany.getCOM().isOpenTrade()){
                amount = Math.min(amount, currentCompany.getMarketShares());
            }

            acc.buyShare(currentCompany.getStockName(), amount, player);
        }

        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("SELL")){
            int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);
            amount = Math.min(amount, currentCompany.getCOM().getShareHolders().getOrDefault(player.getUniqueId(), 0));
            acc.sellShare(currentCompany.getStockName(), amount, player);
        }

        init();
        return true;
    }
}
