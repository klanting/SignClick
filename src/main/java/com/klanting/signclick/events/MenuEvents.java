package com.klanting.signclick.events;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.commands.companyHandelers.CompanyHandlerCreate;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.*;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.decisions.DecisionAboardMilitary;
import com.klanting.signclick.economy.decisions.DecisionBanParty;
import com.klanting.signclick.economy.decisions.DecisionForbidParty;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.menus.*;
import com.klanting.signclick.menus.company.*;
import com.klanting.signclick.menus.company.logs.LogList;
import com.klanting.signclick.menus.company.logs.LogMessages;
import com.klanting.signclick.menus.company.machine.MachineMenu;
import com.klanting.signclick.menus.country.*;
import com.klanting.signclick.menus.party.DecisionChoice;
import com.klanting.signclick.menus.party.DecisionVote;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Function;

public class MenuEvents implements Listener {

    private static final HashMap<Player, Stack<SelectionMenu>> menuStack = new HashMap<>();

    public static final List<Machine> furnaces = new ArrayList<>();

    private static void storeStack(Player player, SelectionMenu sm){
        Stack<SelectionMenu> playerStack = menuStack.getOrDefault(player, new Stack<>());
        playerStack.push(sm);
        menuStack.put(player, playerStack);
    }

    private static void loadStack(Player player){
        Stack<SelectionMenu> playerStack = menuStack.getOrDefault(player, new Stack<>());
        SelectionMenu sm = playerStack.pop();
        sm.getInventory().clear();
        sm.init();
        player.openInventory(sm.getInventory());
    }

    private static void clearStack(Player player){
        menuStack.put(player, new Stack<>());
    }

    public static void checkMachines(){
        Bukkit.getScheduler().runTaskTimer(SignClick.getPlugin(), () -> {

            for (Machine machine: furnaces){
                machine.productionUpdate();

            }

            for (MachineMenu mm: MachineLiveUpdateEvent.openMenus){
                mm.update();
            }

        }, 0L, 20L);
    }

    @EventHandler
    public static void OnClick(InventoryClickEvent event){

        if (event.getClickedInventory() == null || event.getCurrentItem() == null){
            return;
        }

        if (event.getClickedInventory().getHolder() instanceof SelectionMenu && event.getCurrentItem().getType().equals(Material.BARRIER)){
            Player player = (Player) event.getWhoClicked();
            loadStack(player);
            return;
        }

        if (event.getClickedInventory().getHolder() instanceof PagingMenu currentScreen){
            event.setCancelled(true);

            if (event.getCurrentItem().getType().equals(Material.RED_DYE)){
                return;
            }

            boolean prevArrow = event.getCurrentItem().getItemMeta().getDisplayName().contains("Previous Page");
            boolean nextArrow = event.getCurrentItem().getItemMeta().getDisplayName().contains("Next Page");

            boolean cancelSearch = event.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel Search");
            boolean doSearch = event.getCurrentItem().getItemMeta().getDisplayName().contains("Search");

            if (cancelSearch){
                currentScreen.setSearchKey("");
                return;
            }


            if (doSearch){
                Player player = (Player) event.getWhoClicked();
                PagingSearchEvent.waitForMessage.put(player, currentScreen);
                player.closeInventory();
                player.sendMessage("§bEnter the search keyword");
                return;
            }

            if (prevArrow){
                currentScreen.changePage(-1);
                currentScreen.init();
                return;
            }

            if (nextArrow){
                currentScreen.changePage(1);
                currentScreen.init();
                return;
            }
        }

        if (event.getClickedInventory().getHolder() instanceof MarketSelector currentScreen){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            clearStack(player);

            if (event.getCurrentItem().getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE){
                return;
            }

            String compName = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
            MarketMenu screen = new MarketMenu(player.getUniqueId(), Market.getCompany(compName));

            player.openInventory(screen.getInventory());

        }

        if (event.getClickedInventory().getHolder() instanceof MarketMenu currentScreen){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            Company currentCompany = currentScreen.currentCompany;

            Account acc = Market.getAccount(player);

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("BUY")){
                int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);
                amount = Math.min(amount, currentCompany.getMarketShares());
                acc.buyShare(currentCompany.getStockName(), amount, player);
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("SELL")){
                int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);
                amount = Math.min(amount, currentCompany.getCOM().getShareHolders().getOrDefault(player.getUniqueId(), 0));
                acc.sellShare(currentCompany.getStockName(), amount, player);
            }

            currentScreen.init();


        }

        if (event.getClickedInventory().getHolder() instanceof MachineMenu machineMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equals("§7Product Slot")){

                Function<Product, Void> lambda = (prod) -> {

                    if (!(machineMenu.machine.getBlock().getState() instanceof TileState tileState)) {
                        return null;
                    }
                    NamespacedKey productKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_product");

                    /*
                    * set current item
                    * */
                    tileState.getPersistentDataContainer().set(productKey, PersistentDataType.STRING, prod.getMaterial().name());
                    tileState.update();

                    /*
                    * Start furnace
                    * */
                    machineMenu.machine.clearProgress();
                    machineMenu.machine.setProduct(prod);

                    furnaces.add(machineMenu.machine);

                    loadStack(player);
                    return null;};

                ProductList new_screen = new ProductList(machineMenu.comp, lambda);
                player.openInventory(new_screen.getInventory());
            }

            if (event.getSlot() == 10){
                if (!(machineMenu.machine.getBlock().getState() instanceof TileState tileState)){
                    return;
                }
                NamespacedKey productKey = new NamespacedKey(SignClick.getPlugin(), "signclick_company_machine_product");

                /*
                 * set current item
                 * */
                tileState.getPersistentDataContainer().set(productKey, PersistentDataType.STRING, "");
                tileState.update();

                furnaces.remove(machineMenu.machine);

                machineMenu.init();
            }
        }


        if (event.getClickedInventory().getHolder() instanceof Selector selector){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            clearStack(player);

            if (event.getCurrentItem().getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE){
                return;
            }

            int startPos = event.getCurrentItem().getItemMeta().getDisplayName().indexOf("[");
            int endPos = event.getCurrentItem().getItemMeta().getDisplayName().length()-1;
            Company company = Market.getCompany(event.getCurrentItem().getItemMeta().getDisplayName().substring(startPos+1, endPos));

            selector.funcType.apply(company);
        }

        if (event.getClickedInventory().getHolder() instanceof BoardMenu boardMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.contains("Assign Board")){
                BoardSupportMenu screen = new BoardSupportMenu(player.getUniqueId(), boardMenu.comp);

                player.openInventory(screen.getInventory());
            }
        }

        if (event.getClickedInventory().getHolder() instanceof BoardSupportMenu boardSupportMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            Material option = event.getCurrentItem().getType();
            if (option.equals(Material.WHITE_WOOL)){
                AddSupportEvent.waitForMessage.put(player, boardSupportMenu);
                player.closeInventory();
                player.sendMessage("§bEnter the supported player its username");
            }else{
                String username = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                UUID uuid = Bukkit.getOfflinePlayer(username).getUniqueId();
                boardSupportMenu.comp.getCOM().getBoard().removeBoardSupport(player.getUniqueId(), uuid);
                boardSupportMenu.init();
            }

            return;
        }

        if (event.getClickedInventory().getHolder() instanceof OwnerMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            OwnerMenu old_screen = (OwnerMenu) event.getClickedInventory().getHolder();
            if (option.equalsIgnoreCase("§6Upgrades")){
                UpgradeMenu new_screen = new UpgradeMenu(player.getUniqueId(), old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }else if(option.equalsIgnoreCase("§6Patent")){

                Country country = CountryManager.getCountry(old_screen.comp.getCountry());
                if (country != null && country.getStability() < 30){
                    player.sendMessage("§bcan`t access patent auction with country stability under 30");
                    return;
                }
                PatentIDMenu new_screen = new PatentIDMenu(old_screen.comp, true);
                player.openInventory(new_screen.getInventory());

            }else if(option.equalsIgnoreCase("§6Patent Auction")){
                AuctionMenu new_screen = new AuctionMenu(old_screen.comp);
                player.openInventory(new_screen.getInventory());

            }else if(option.equalsIgnoreCase("§6Recipes")){
                PatentIDMenu new_screen = new PatentIDMenu(old_screen.comp, false);
                player.openInventory(new_screen.getInventory());

            }else if(option.equalsIgnoreCase("§6Logs")){
                LogList new_screen = new LogList(old_screen.comp);
                player.openInventory(new_screen.getInventory());

            }else if(option.equalsIgnoreCase("§6Chief Positions")){
                ChiefList new_screen = new ChiefList(player.getUniqueId(), old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }else if(option.equalsIgnoreCase("§6Board Info")){
                BoardMenu new_screen = new BoardMenu(old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }else if(option.equalsIgnoreCase("§6Research")){
                ResearchMenu new_screen = new ResearchMenu(player.getUniqueId(), old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }else if(option.equalsIgnoreCase("§6Craft Products")){
                ProductCraftMenu new_screen = new ProductCraftMenu(player.getUniqueId(), old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }else if(option.equalsIgnoreCase("§6Products")){
                ProductList new_screen = new ProductList(old_screen.comp, s -> {return null;});
                player.openInventory(new_screen.getInventory());
            }else if(option.equalsIgnoreCase("§6Machines List")){
                MachineList new_screen = new MachineList(old_screen.comp, s -> {return null;});
                player.openInventory(new_screen.getInventory());
            }else if(option.equalsIgnoreCase("§6Financials")){
                FinancialMenu new_screen = new FinancialMenu(player.getUniqueId(), old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }

        }

        if (event.getClickedInventory().getHolder() instanceof FinancialMenu financialMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            Company currentCompany = financialMenu.company;

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Increase")){
                int amount = Integer.parseInt((event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]).replace(".", ""));
                currentCompany.setSpendable(currentCompany.getSpendable()+amount);
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Decrease")){
                int amount = Integer.parseInt((event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]).replace(".", ""));
                currentCompany.setSpendable(Math.max(currentCompany.getSpendable()-amount, 0));
            }

            financialMenu.init();
        }

        if (event.getClickedInventory().getHolder() instanceof MachineList machineList){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            int item = event.getSlot();

            if(event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)){
                return;
            }

            int index = (machineList.getPage()*45+item);
            machineList.func.apply(machineList.comp.getProducts().get(index));

            return;
        }

        if (event.getClickedInventory().getHolder() instanceof ProductCraftMenu productCraftMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            String option = event.getCurrentItem().getItemMeta().getDisplayName();

            if (option.equals("§7Crafting Slot") || (1 <= event.getSlot()/9 && event.getSlot()/9 <= 3) &&
                    (event.getSlot()-1)%3 <= 2 && 0 <= (event.getSlot()-1)%3){

                int slot = (event.getSlot()/9 -1)*3+((event.getSlot()-1)%3);

                if (productCraftMenu.products[slot] != null){
                    productCraftMenu.products[slot] = null;
                    productCraftMenu.init();
                    return;
                }

                Function<Product, Void> lambda = (prod) -> {
                    productCraftMenu.products[slot] = prod;
                    productCraftMenu.init();
                    loadStack(player);
                    return null;};

                ProductList new_screen = new ProductList(productCraftMenu.comp, lambda);
                player.openInventory(new_screen.getInventory());
            }else if(option.equals("§aSave Product")) {
                Product product = productCraftMenu.getCrafted();

                if (product != null){
                    productCraftMenu.comp.addProduct(product);
                    loadStack(player);
                }
                return;

            }else{
                /*
                 * Only return if no slot selected
                 * */
                return;
            }

        }

        if (event.getClickedInventory().getHolder() instanceof ProductList productList){
            event.setCancelled(true);
            int item = event.getSlot();

            if(event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)){
                return;
            }

            int index = (productList.getPage()*45+item);
            productList.func.apply(productList.comp.getProducts().get(index));

            return;
        }

        if (event.getClickedInventory().getHolder() instanceof ResearchMenu researchMenu){
            event.setCancelled(true);


            int slot = event.getSlot() % 9;
            if (!(slot >= 2 && slot <= 7)){
                return;
            }
            int index = researchMenu.getPage()*5+(event.getSlot()/9);
            researchMenu.comp.getResearch().getResearchOptions().get(index).setModifierIndex(slot-2);
            researchMenu.init();

            return;
        }


        if (event.getClickedInventory().getHolder() instanceof LogList){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            LogList old_screen = (LogList) event.getClickedInventory().getHolder();

            LogMessages new_screen = new LogMessages(old_screen.comp, old_screen.comp.getLogObservers().get(event.getSlot()));
            player.openInventory(new_screen.getInventory());
        }

        if (event.getClickedInventory().getHolder() instanceof LogMessages){
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory().getHolder() instanceof UpgradeMenu){
            UpgradeMenu screen = (UpgradeMenu) event.getClickedInventory().getHolder();
            event.setCancelled(true);
            int id = event.getSlot()-11;
            boolean suc6 = screen.comp.doUpgrade(id);
            screen.init();

            Player player = (Player) event.getWhoClicked();
            if (!suc6){
                player.sendMessage("§bNot enough Money or Points to do the upgrade");
            }
        }

        if (event.getClickedInventory().getHolder() instanceof ChiefList chiefList){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            if (event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)){
                return;
            }

            String option = event.getCurrentItem().getItemMeta().getDisplayName().substring(2, 5);

            ChiefMenu newScreen = new ChiefMenu(player.getUniqueId(), chiefList.comp, option);
            player.openInventory(newScreen.getInventory());
        }

        if (event.getClickedInventory().getHolder() instanceof ChiefMenu chiefMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            if(event.getSlot() == 24){
                AddChiefSupportEvent.waitForMessage.put(player, chiefMenu);
                player.closeInventory();
                player.sendMessage("§bEnter the supported player its username");
            }


            return;
        }

        if (event.getClickedInventory().getHolder() instanceof PatentIDMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            PatentIDMenu old_screen = (PatentIDMenu) event.getClickedInventory().getHolder();

            if (old_screen.designer){
                if (option.equalsIgnoreCase("§6Empty Patent")){
                    PatentSelectorMenu new_screen = new PatentSelectorMenu(old_screen.comp);
                    player.openInventory(new_screen.getInventory());
                }else{
                    PatentDesignerMenu new_screen = new PatentDesignerMenu(old_screen.comp.patent.get(event.getSlot()), old_screen.comp);
                    player.openInventory(new_screen.getInventory());
                }
            }else{
                if (!option.equalsIgnoreCase("§6Empty Patent")){
                    PatentCrafting new_screen = new PatentCrafting(old_screen.comp, old_screen.comp.patent.get(event.getSlot()));
                    player.openInventory(new_screen.getInventory());
                }
            }

        }

        if (event.getClickedInventory().getHolder() instanceof PatentSelectorMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();

            PatentSelectorMenu old_screen = (PatentSelectorMenu) event.getClickedInventory().getHolder();
            Patent pat = new Patent("Nameless", item.getType(), new ArrayList<PatentUpgrade>());
            PatentDesignerMenu new_screen = new PatentDesignerMenu(pat, old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }

        if (event.getClickedInventory().getHolder() instanceof PatentDesignerMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            PatentDesignerMenu old_screen = (PatentDesignerMenu) event.getClickedInventory().getHolder();
            Patent pat = old_screen.patent;

            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equalsIgnoreCase("§aSave")){
                if (!old_screen.comp.patent.contains(old_screen.patent)){
                    old_screen.comp.patent.add(old_screen.patent);
                    old_screen.patent.createCraft(old_screen.comp);
                }

                PatentIDMenu new_screen = new PatentIDMenu(old_screen.comp, true);
                player.openInventory(new_screen.getInventory());
                return;
            }
            if (event.getCurrentItem().getType().equals(Material.NAME_TAG)){
                pat.setName(event.getCurrentItem().getItemMeta().getDisplayName());
                old_screen.init();
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_DYE)){
                PatentDesignerUpgrade new_screen = new PatentDesignerUpgrade(pat, old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }
        }

        if (event.getClickedInventory().getHolder() instanceof AuctionMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            AuctionMenu old_screen = (AuctionMenu) event.getClickedInventory().getHolder();
            int location = event.getSlot();

            int add_price = SignClick.getPlugin().getConfig().getInt("auctionBitIncrease");
            if (Auction.getInstance().bitsOwner.get(location) == null){
                add_price = 0;
            }

            int currentBit = Auction.getInstance().getBit(location)+add_price;
            double compValue = old_screen.comp.getValue();

            /*
            * Subtract other bits of the max allowed bit
            * */
            for (Map.Entry<Integer, String> entry :Auction.getInstance().bitsOwner.entrySet()){
                if (!Objects.equals(entry.getValue(), old_screen.comp.getStockName())){
                    continue;
                }
                if (entry.getKey() == location){
                    continue;
                }

                compValue -= Auction.getInstance().getBit(entry.getKey());
            }

            if (compValue < currentBit){
                player.sendMessage("§bCompany is not valued enough to place the current Bid");
                return;
            }

            Auction.getInstance().setBit(location, currentBit, old_screen.comp.getStockName());
            old_screen.init();
        }

        if (event.getClickedInventory().getHolder() instanceof PatentDesignerUpgrade){
            event.setCancelled(true);
            PatentDesignerUpgrade old_screen = (PatentDesignerUpgrade) event.getClickedInventory().getHolder();
            PatentUpgrade pat_up = old_screen.patentUpgradeList.get(event.getSlot());
            Patent pat = old_screen.patent;
            pat.upgrades.add(pat_up);

            Player player = (Player) event.getWhoClicked();
            PatentDesignerMenu new_screen = new PatentDesignerMenu(pat, old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }

        if (event.getClickedInventory().getHolder() instanceof PatentCrafting){
            event.setCancelled(true);
            PatentCrafting old_screen = (PatentCrafting) event.getClickedInventory().getHolder();
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equalsIgnoreCase("§6Get Patent Sheet")){
                Player player = (Player) event.getWhoClicked();
                old_screen.comp.patentCrafted += 1;

                ItemStack item = new ItemStack(Material.PAPER, 1);

                ItemMeta m = item.getItemMeta();
                m.setDisplayName("§6"+old_screen.comp.getStockName() +":"+old_screen.patent.getName()+":"+old_screen.comp.patent.indexOf(old_screen.patent));
                item.setItemMeta(m);

                player.getInventory().setItem(player.getInventory().firstEmpty(), item);
            }
        }

        if (event.getClickedInventory().getHolder() instanceof Policy){
            event.setCancelled(true);
            Policy old_screen = (Policy) event.getClickedInventory().getHolder();
            Player player = (Player) event.getWhoClicked();

            int slot = event.getSlot();
            int row = slot/9;
            int level = slot - 9*row-2;
            if (level < 0){
                return;
            }

            Country country = CountryManager.getCountry(player);

            country.setPolicies(row-1, level);

            old_screen.init();
            player.sendMessage("§bPolicy change Decision has been passed on");
        }

        if (event.getClickedInventory().getHolder() instanceof ElectionMenu){
            ItemStack item = event.getCurrentItem();
            ItemMeta m = item.getItemMeta();
            String party = m.getDisplayName().substring(2);

            ElectionMenu old_screen = (ElectionMenu) event.getClickedInventory().getHolder();
            old_screen.e.vote(party, event.getWhoClicked().getUniqueId());

            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
        }

        if (event.getClickedInventory().getHolder() instanceof DecisionVote currentScreen){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            int slot = currentScreen.getItemIndex(event.getCurrentItem());
            assert slot != -1;

            DecisionVote old_screen = (DecisionVote) event.getClickedInventory().getHolder();
            String countryName = old_screen.p.country;
            Country country = CountryManager.getCountry(countryName);

            Decision d = country.getDecisions().get(slot);

            DecisionChoice new_screen = new DecisionChoice(old_screen.p, d);
            player.openInventory(new_screen.getInventory());

        }

        if (event.getClickedInventory().getHolder() instanceof DecisionChoice){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();

            DecisionChoice old_screen = (DecisionChoice) event.getClickedInventory().getHolder();
            old_screen.d.vote(old_screen.p, slot == 11);

            player.closeInventory();

        }

        if (event.getClickedInventory().getHolder() instanceof Menu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equalsIgnoreCase("§6Policy")){
                Policy screen = new Policy(player.getUniqueId());
                player.openInventory(screen.getInventory());
            }else if (option.equalsIgnoreCase("§6Decisions")){
                DecisionMenu screen = new DecisionMenu(player.getUniqueId());
                player.openInventory(screen.getInventory());
            }
        }

        if (event.getClickedInventory().getHolder() instanceof DecisionMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equalsIgnoreCase("§6Ban party")){
                Country country = CountryManager.getCountry(player);

                if (country.getCountryElection() != null){
                    player.sendMessage("§b you can`t ban parties during elections");
                    return;
                }
                PartyBan screen = new PartyBan(player.getUniqueId());
                player.openInventory(screen.getInventory());
            }else if (option.equalsIgnoreCase("§6Forbid party") || option.equalsIgnoreCase("§6Allow party")){
                String name;
                if (option.equalsIgnoreCase("§6Allow party")){
                    name = "§6Allow Parties";
                }else{
                    name = "§6Forbid Parties";
                }

                Country country = CountryManager.getCountry(player);


                boolean go_to = !country.isForbidParty();

                if (country.getStability() < 30.0){
                    player.sendMessage("§brequired stability is 30");
                    return;
                }

                Decision d = new DecisionForbidParty(name, 0.5, country.getName(), go_to);
                country.addDecision(d);
            }else if (option.equalsIgnoreCase("§6Abort military payments") || option.equalsIgnoreCase("§6Allow military payments")){
                String name;
                if (option.equalsIgnoreCase("§6Allow military payments")){
                    name = "§6Allow military payments";
                }else{
                    name = "§6Abort military payments";
                }

                Country country = CountryManager.getCountry(player);

                boolean go_to = !country.isAboardMilitary();

                Decision d = new DecisionAboardMilitary(name, 0.5, country.getName(), go_to);
                country.addDecision(d);

            }

        }

        if (event.getClickedInventory().getHolder() instanceof PartyBan){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            int slot = event.getSlot();
            Country country = CountryManager.getCountry(player);

            Party p = country.getParties().get(slot);
            Party ph = country.getRuling();
            if (ph == p){
                player.sendMessage("§bcan`t ban ruling party");
                return;
            }

            if (country.getStability() < 40.0){
                player.sendMessage("§brequired stability is 40");
                return;
            }

            Decision d = new DecisionBanParty("§6Ban Party §9"+p.name, 0.5, country.getName(), p);
            country.addDecision(d);

            player.closeInventory();
        }

        if (event.getClickedInventory().getHolder() instanceof TypeSelect){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            TypeSelect old_screen = (TypeSelect) event.getClickedInventory().getHolder();
            String name = event.getCurrentItem().getItemMeta().getDisplayName();

            old_screen.details = new CompanyHandlerCreate.companyCreationDetails(
                    old_screen.details.companyName(),
                    old_screen.details.stockName(),
                    old_screen.details.player(),
                    old_screen.details.creationCost(),
                    name.substring(2)
            );
            player.closeInventory();

            try{
                CompanyHandlerCreate.createCompany(old_screen.details);
            }catch (CommandException e){
                player.sendMessage(e.getMessage());
            }

        }

        if (!(event.getClickedInventory().getHolder() instanceof SelectionMenu)){
            return;
        }

        Player player = (Player) event.getWhoClicked();
        /*
        * Store the last inventory when inventory menu changes
        * */
        if (player.getOpenInventory().getTopInventory() == null){
            return;
        }

        if (!event.getClickedInventory().getHolder().equals(player.getOpenInventory().getTopInventory().getHolder())){
            storeStack(player, (SelectionMenu) event.getClickedInventory().getHolder());
        }


    }

}
