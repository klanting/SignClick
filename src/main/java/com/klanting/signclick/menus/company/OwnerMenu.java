package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Board;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class OwnerMenu extends SelectionMenu {
    public CompanyI comp;
    private UUID uuid;

    public OwnerMenu(UUID uuid, CompanyI company){
        super(54, "Company Menu: "+ company.getStockName(), true);
        comp = company;
        this.uuid = uuid;

        assert comp.getCOM().isOwner(uuid);
        init();
    }

    public void init(){
        Board board = comp.getCOM().getBoard();

        /*
        * balance block
        * */
        DecimalFormat df = new DecimalFormat("###,###,###");
        ArrayList<String> l = new ArrayList<>();
        l.add("§6Value: §9"+ df.format(comp.getValue()));
        l.add("§6Spendable: §9"+ df.format(comp.getSpendable()));
        l.add("§6Type: §9"+ comp.getType());
        ItemStack value = ItemFactory.create(Material.GOLD_BLOCK, "§6Balance", l);
        getInventory().setItem(13, value);

        /*
         * Products
         * */
        l = new ArrayList<>();
        l.add("§7List all the products");
        value = ItemFactory.create(Material.JUKEBOX, "§6Products", l);
        getInventory().setItem(21, value);

        /*
        * Upgrades
        * */
        l = new ArrayList<>();
        l.add("§7Allows you to upgrade your company");
        value = ItemFactory.create(Material.EMERALD, "§6Upgrades", l);
        getInventory().setItem(22, value);

        /*
         * Chief Positions
         * */
        l = new ArrayList<>();
        l.add("§7Company chief position information");
        value = ItemFactory.create(Material.IRON_HELMET, "§6Chief Positions", l);
        getInventory().setItem(23, value);

        /*
         * Machines list
         * */
        l = new ArrayList<>();
        l.add("§7List of all company machines");
        value = ItemFactory.create(Material.BLAST_FURNACE, "§6Machines List", l);
        getInventory().setItem(30, value);

        /*
         * Employees List
         * */
        l = new ArrayList<>();
        l.add("§7Information of all its employees");
        value = ItemFactory.create(Material.SKELETON_SKULL, "§6Employees List", l);
        getInventory().setItem(31, value);

        /*
         * Board Info
         * */
        l = new ArrayList<>();
        l.add("§7Go to the board menu");
        value = ItemFactory.create(Material.OAK_SIGN, "§6Board Info", l);
        getInventory().setItem(32, value);

        /*
        * patent
        * */
        l = new ArrayList<>();
        l.add("§7Allows you to create Gear with");
        l.add("§7custom properties");
        l.add("§7Combine Patent paper and gear item in");
        l.add("§7the crafting table to get started");
        value = ItemFactory.create(Material.NETHERITE_HELMET, "§6Patent", l);
        getInventory().setItem(9, value);

        /*
        * Auction
        * */
        l = new ArrayList<>();
        l.add("§7Auction for patent upgrades");
        l.add("§7that can be applied to Gear");
        value = ItemFactory.create(Material.IRON_NUGGET, "§6Patent Auction", l);
        getInventory().setItem(27, value);

        /*
        * Recipes
        * */
        l = new ArrayList<>();
        l.add("§7See the gear patent recipes");
        value = ItemFactory.create(Material.CRAFTING_TABLE, "§6Recipes", l);
        getInventory().setItem(18, value);

        /*
         * Logs
         * */
        l = new ArrayList<>();
        l.add("§7See the logs of your company");
        value = ItemFactory.create(Material.PAPER, "§6Logs", l);
        getInventory().setItem(45, value);

        if (board.getChiefPermission("CTO").equals(uuid)){
            /*
             * Craft Products
             * */
            l = new ArrayList<>();
            l.add("§7Craft new products from the original products");
            value = ItemFactory.create(Material.CRAFTING_TABLE, "§6Craft Products", l);
            getInventory().setItem(39, value);
        }


        if (board.getChiefPermission("CFO").equals(uuid)){
            /*
             * set finances
             * */
            l = new ArrayList<>();
            l.add("§7Change finance constraints");
            value = ItemFactory.create(Material.GOLD_INGOT, "§6Financials", l);
            getInventory().setItem(40, value);
        }

        if (board.getChiefPermission("CEO").equals(uuid)) {
            /*
             * Research
             * */
            l = new ArrayList<>();
            l.add("§7Discover new products");
            value = ItemFactory.create(Material.POTION, "§6Research", l);

            PotionMeta meta = (PotionMeta) value.getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.NIGHT_VISION, false, false));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            value.setItemMeta(meta);

            getInventory().setItem(41, value);
        }



        super.init();
    }
}
