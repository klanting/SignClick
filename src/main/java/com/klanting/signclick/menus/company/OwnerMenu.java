package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Board;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.menus.company.logs.LogList;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        ItemStack value = ItemFactory.create(Material.GOLD_BLOCK, "§6§lBalance", l);
        getInventory().setItem(13, value);

        /*
         * Products
         * */
        l = new ArrayList<>();
        l.add("§7List all the products");
        value = ItemFactory.create(Material.JUKEBOX, "§6§lProducts", l);
        getInventory().setItem(21, value);

        /*
        * Upgrades
        * */
        l = new ArrayList<>();
        l.add("§7Allows you to upgrade your company");
        value = ItemFactory.create(Material.EMERALD, "§6§lUpgrades", l);
        getInventory().setItem(22, value);

        /*
         * Chief Positions
         * */
        l = new ArrayList<>();
        l.add("§7Company chief position information");
        value = ItemFactory.create(Material.IRON_HELMET, "§6§lChief Positions", l);
        getInventory().setItem(23, value);

        /*
         * Machines list
         * */
        l = new ArrayList<>();
        l.add("§7List of all company machines");
        value = ItemFactory.create(Material.BLAST_FURNACE, "§6§lMachines List", l);
        getInventory().setItem(30, value);

        /*
         * Employees List
         * */
        l = new ArrayList<>();
        l.add("§7Information of all its employees");
        value = ItemFactory.create(Material.SKELETON_SKULL, "§6§lEmployees List", l);
        getInventory().setItem(31, value);

        /*
         * Board Info
         * */
        l = new ArrayList<>();
        l.add("§7Go to the board menu");
        value = ItemFactory.create(Material.OAK_SIGN, "§6§lBoard Info", l);
        getInventory().setItem(32, value);

        /*
        * patent
        * */
        if(board.getChiefPermission("CEO").equals(uuid)){
            l = new ArrayList<>();
            l.add("§7Allows you to create Gear with");
            l.add("§7custom properties");
            l.add("§7Combine Patent paper and gear item in");
            l.add("§7the crafting table to get started");
            value = ItemFactory.create(Material.NETHERITE_HELMET, "§6§lPatent", l);
            getInventory().setItem(9, value);
        }


        /*
        * Auction
        * */
        if(board.getChiefPermission("CEO").equals(uuid)){
            l = new ArrayList<>();
            l.add("§7Auction for patent upgrades");
            l.add("§7that can be applied to Gear");
            value = ItemFactory.create(Material.IRON_NUGGET, "§6§lPatent Auction", l);
            getInventory().setItem(27, value);
        }

        /*
        * Recipes
        * */
        if(board.getChiefPermission("CEO").equals(uuid)){
            l = new ArrayList<>();
            l.add("§7See the gear patent recipes");
            value = ItemFactory.create(Material.CRAFTING_TABLE, "§6§lRecipes", l);
            getInventory().setItem(18, value);
        }


        /*
         * Logs
         * */
        l = new ArrayList<>();
        l.add("§7See the logs of your company");
        value = ItemFactory.create(Material.PAPER, "§6§lLogs", l);
        getInventory().setItem(45, value);

        if (board.getChiefPermission("CTO").equals(uuid)){
            /*
             * Craft Products
             * */
            l = new ArrayList<>();
            l.add("§7Craft new products from the original products");
            value = ItemFactory.create(Material.CRAFTING_TABLE, "§6§lCraft Products", l);
            getInventory().setItem(39, value);
        }


        if (board.getChiefPermission("CFO").equals(uuid)){
            /*
             * set finances
             * */
            l = new ArrayList<>();
            l.add("§7Change finance constraints");
            value = ItemFactory.create(Material.GOLD_INGOT, "§6§lFinancials", l);
            getInventory().setItem(40, value);
        }

        if (board.getChiefPermission("CEO").equals(uuid)) {
            /*
             * Research
             * */
            l = new ArrayList<>();
            l.add("§7Discover new products");
            value = ItemFactory.create(Material.POTION, "§6§lResearch", l);

            PotionMeta meta = (PotionMeta) value.getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.NIGHT_VISION, false, false));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            value.setItemMeta(meta);

            getInventory().setItem(41, value);
        }

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        String option = event.getCurrentItem().getItemMeta().getDisplayName();
        OwnerMenu old_screen = (OwnerMenu) event.getClickedInventory().getHolder();
        if (option.equalsIgnoreCase("§6§lUpgrades")){
            UpgradeMenu new_screen = new UpgradeMenu(player.getUniqueId(), old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }else if(option.equalsIgnoreCase("§6§lPatent")){

            Country country = CountryManager.getCountry(old_screen.comp.getCountry());
            if (country != null && country.getStability() < 30){
                player.sendMessage("§bcan`t access patent auction with country stability under 30");
                return false;
            }
            PatentIDMenu new_screen = new PatentIDMenu(old_screen.comp, true);
            player.openInventory(new_screen.getInventory());

        }else if(option.equalsIgnoreCase("§6§lPatent Auction")){
            AuctionMenu new_screen = new AuctionMenu(old_screen.comp);
            player.openInventory(new_screen.getInventory());

        }else if(option.equalsIgnoreCase("§6§lRecipes")){
            PatentIDMenu new_screen = new PatentIDMenu(old_screen.comp, false);
            player.openInventory(new_screen.getInventory());

        }else if(option.equalsIgnoreCase("§6§lLogs")){
            LogList new_screen = new LogList(old_screen.comp);
            player.openInventory(new_screen.getInventory());

        }else if(option.equalsIgnoreCase("§6§lChief Positions")){
            ChiefList new_screen = new ChiefList(player.getUniqueId(), old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }else if(option.equalsIgnoreCase("§6§lBoard Info")){
            BoardMenu new_screen = new BoardMenu(old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }else if(option.equalsIgnoreCase("§6§lResearch")){
            ResearchMenu new_screen = new ResearchMenu(player.getUniqueId(), old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }else if(option.equalsIgnoreCase("§6§lCraft Products")){
            ProductCraftMenu new_screen = new ProductCraftMenu(player.getUniqueId(), old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }else if(option.equalsIgnoreCase("§6§lProducts")){
            ProductList new_screen = new ProductList(old_screen.comp, s -> {return null;}, true,
                    true, true);
            player.openInventory(new_screen.getInventory());
        }else if(option.equalsIgnoreCase("§6§lMachines List")){
            MachineList new_screen = new MachineList(old_screen.comp, s -> {return null;});
            player.openInventory(new_screen.getInventory());
        }else if(option.equalsIgnoreCase("§6§lFinancials")){
            FinancialMenu new_screen = new FinancialMenu(player.getUniqueId(), old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }else if(option.equalsIgnoreCase("§6§lEmployees List")){
            EmployeesList new_screen = new EmployeesList(comp);
            player.openInventory(new_screen.getInventory());
        }

        return true;
    }
}
