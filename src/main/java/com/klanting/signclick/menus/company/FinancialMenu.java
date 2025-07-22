package com.klanting.signclick.menus.company;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FinancialMenu extends SelectionMenu {

    public UUID uuid;
    public CompanyI company;


    public FinancialMenu(UUID uuid, CompanyI company){
        super(45, "Company Financial", true);
        this.uuid = uuid;
        this.company = company;
        init();
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

        List<String> lores = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        DecimalFormat df2 = new DecimalFormat("###,###,##0");
        lores.add("§7Spendable: $"+ df.format(company.getSpendable()));

        ItemStack goldBlock = ItemFactory.create(Material.GOLD_BLOCK, "§7CFO Spendable Manager", lores);

        getInventory().setItem(22, goldBlock);

        /*
        * set the Pricing
        * */

        List<Triple<Integer, Integer, Material>> spendableButtons = new ArrayList<>();

        List<Integer> spendableAmount = SignClick.getPlugin().getConfig().getIntegerList("spendableAmount");
        assert spendableAmount.size() == 4;

        spendableButtons.add(Triple.of(1, 11, Material.LIME_DYE));
        spendableButtons.add(Triple.of(spendableAmount.get(0), 12, Material.EMERALD));
        spendableButtons.add(Triple.of(spendableAmount.get(1), 13, Material.LIME_STAINED_GLASS_PANE));
        spendableButtons.add(Triple.of(spendableAmount.get(2), 14, Material.LIME_STAINED_GLASS));
        spendableButtons.add(Triple.of(spendableAmount.get(3), 15, Material.LIME_CONCRETE));

        spendableButtons.add(Triple.of(-1, 29, Material.RED_DYE));
        spendableButtons.add(Triple.of(-spendableAmount.get(0), 30, Material.REDSTONE));
        spendableButtons.add(Triple.of(-spendableAmount.get(1), 31, Material.RED_STAINED_GLASS_PANE));
        spendableButtons.add(Triple.of(-spendableAmount.get(2), 32, Material.RED_STAINED_GLASS));
        spendableButtons.add(Triple.of(-spendableAmount.get(3),
                33, Material.RED_CONCRETE));

        for (Triple<Integer, Integer, Material> tup: spendableButtons){
            ItemStack button = new ItemStack(tup.getRight(), 1);
            ItemMeta m = button.getItemMeta();

            int amount = tup.getLeft();

            String message;
            if (amount > 0){
                message = "§a"+"Increase: "+df2.format(amount)+" Spendable";
            } else if (amount == 0) {
                message = "§fDOES NOTHING";
            } else{
                message = "§c"+"Decrease: "+df2.format(-amount)+" Spendable";
            }

            if (Math.abs(amount) == 1){
                /*
                * removes the last 's' of shares
                * */
                message = message.substring(0, message.length()-1);
            }

            m.setDisplayName(message);

            button.setItemMeta(m);

            getInventory().setItem(tup.getMiddle(), button);
        }

        /*
        * Set great gray glass
        * */
        ItemStack grayGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta  m = grayGlass.getItemMeta();
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

        CompanyI currentCompany = company;

        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Increase")){
            int amount = Integer.parseInt((event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]).replace(".", ""));
            currentCompany.setSpendable(currentCompany.getSpendable()+amount);
        }

        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Decrease")){
            int amount = Integer.parseInt((event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]).replace(".", ""));
            currentCompany.setSpendable(Math.max(currentCompany.getSpendable()-amount, 0));
        }

        init();

        return true;
    }
}
