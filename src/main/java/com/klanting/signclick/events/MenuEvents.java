package com.klanting.signclick.events;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.*;
import com.klanting.signclick.economy.companyPatent.Auction;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.decisions.DecisionAboardMilitary;
import com.klanting.signclick.economy.decisions.DecisionBanParty;
import com.klanting.signclick.economy.decisions.DecisionForbidParty;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.Menus.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuEvents implements Listener {

    private static final HashMap<Player, Stack<SelectionMenu>> menuStack = new HashMap<>();

    private static void storeStack(Player player, SelectionMenu sm){
        Stack<SelectionMenu> playerStack = menuStack.getOrDefault(player, new Stack<>());
        playerStack.push(sm);
        menuStack.put(player, playerStack);
    }

    private static void loadStack(Player player){
        Stack<SelectionMenu> playerStack = menuStack.getOrDefault(player, new Stack<>());
        SelectionMenu sm = playerStack.pop();
        player.openInventory(sm.getInventory());
    }

    private static void clearStack(Player player){
        menuStack.put(player, new Stack<>());
    }

    @EventHandler
    public static void OnClick(InventoryClickEvent event){

        if (event.getClickedInventory() == null || event.getCurrentItem() == null){
            return;
        }

        if (event.getCurrentItem().getType().equals(Material.BARRIER)){
            Player player = (Player) event.getWhoClicked();
            loadStack(player);
            return;
        }

        if (event.getClickedInventory().getHolder() instanceof CompanyMarketMenu currentScreen){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            clearStack(player);
            Company currentCompany = currentScreen.companies.get(currentScreen.currentIndex);

            Account acc = Market.getAccount(player);

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("BUY")){
                int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);
                acc.buyShare(currentCompany.getName(), amount, player);
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("SELL")){
                int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);
                acc.sellShare(currentCompany.getName(), amount, player);
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Next")){
                currentScreen.changePtr(1);
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Back")){
                currentScreen.changePtr(-1);
            }

            currentScreen.init();


        }


        if (event.getClickedInventory().getHolder() instanceof CompanySelector){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            clearStack(player);

            Company company = Market.getCompany(event.getCurrentItem().getItemMeta().getDisplayName());
            CompanyOwnerMenu screen = new CompanyOwnerMenu(player.getUniqueId(), company);

            player.openInventory(screen.getInventory());
        }

        if (event.getClickedInventory().getHolder() instanceof CompanyOwnerMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            CompanyOwnerMenu old_screen = (CompanyOwnerMenu) event.getClickedInventory().getHolder();
            if (option.equalsIgnoreCase("§6Upgrades")){
                CompanyUpgradeMenu new_screen = new CompanyUpgradeMenu(player.getUniqueId(), old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }else if(option.equalsIgnoreCase("§6Patent")){

                Country country = CountryManager.getCountry(old_screen.comp.getCountry());
                if (country != null && country.getStability() < 30){
                    player.sendMessage("§bcan`t access patent auction with country stability under 30");
                    return;
                }
                CompanyPatentIDMenu new_screen = new CompanyPatentIDMenu(old_screen.comp, true);
                player.openInventory(new_screen.getInventory());

            }else if(option.equalsIgnoreCase("§6Auction")){
                CompanyAuctionMenu new_screen = new CompanyAuctionMenu(old_screen.comp);
                player.openInventory(new_screen.getInventory());

            }else if(option.equalsIgnoreCase("§6Recipes")){
                CompanyPatentIDMenu new_screen = new CompanyPatentIDMenu(old_screen.comp, false);
                player.openInventory(new_screen.getInventory());

            }else if(option.equalsIgnoreCase("§6Type")){
                CompanyTypeSelect new_screen = new CompanyTypeSelect(old_screen.comp);
                player.openInventory(new_screen.getInventory());

            }
        }

        if (event.getClickedInventory().getHolder() instanceof CompanyUpgradeMenu){
            CompanyUpgradeMenu screen = (CompanyUpgradeMenu) event.getClickedInventory().getHolder();
            event.setCancelled(true);
            int id = event.getSlot()-11;
            boolean suc6 = screen.comp.doUpgrade(id);
            screen.init();

            Player player = (Player) event.getWhoClicked();
            if (!suc6){
                player.sendMessage("§bNot enough Money or Points to do the upgrade");
            }
        }
        if (event.getClickedInventory().getHolder() instanceof CompanyPatentIDMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            CompanyPatentIDMenu old_screen = (CompanyPatentIDMenu) event.getClickedInventory().getHolder();

            if (old_screen.designer){
                if (option.equalsIgnoreCase("§6Empty Patent")){
                    CompanyPatentSelectorMenu new_screen = new CompanyPatentSelectorMenu(old_screen.comp);
                    player.openInventory(new_screen.getInventory());
                }else{
                    CompanyPatentDesignerMenu new_screen = new CompanyPatentDesignerMenu(old_screen.comp.patent.get(event.getSlot()), old_screen.comp);
                    player.openInventory(new_screen.getInventory());
                }
            }else{
                if (!option.equalsIgnoreCase("§6Empty Patent")){
                    CompanyPatentCrafting new_screen = new CompanyPatentCrafting(old_screen.comp, old_screen.comp.patent.get(event.getSlot()));
                    player.openInventory(new_screen.getInventory());
                }
            }

        }

        if (event.getClickedInventory().getHolder() instanceof CompanyPatentSelectorMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();

            CompanyPatentSelectorMenu old_screen = (CompanyPatentSelectorMenu) event.getClickedInventory().getHolder();
            Patent pat = new Patent("Nameless", item.getType(), new ArrayList<PatentUpgrade>());
            CompanyPatentDesignerMenu new_screen = new CompanyPatentDesignerMenu(pat, old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }

        if (event.getClickedInventory().getHolder() instanceof CompanyPatentDesignerMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            CompanyPatentDesignerMenu old_screen = (CompanyPatentDesignerMenu) event.getClickedInventory().getHolder();
            Patent pat = old_screen.patent;

            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equalsIgnoreCase("§aSave")){
                if (!old_screen.comp.patent.contains(old_screen.patent)){
                    old_screen.comp.patent.add(old_screen.patent);
                    old_screen.patent.createCraft(old_screen.comp);
                }

                CompanyPatentIDMenu new_screen = new CompanyPatentIDMenu(old_screen.comp, true);
                player.openInventory(new_screen.getInventory());
                return;
            }
            if (event.getCurrentItem().getType().equals(Material.NAME_TAG)){
                pat.setName(event.getCurrentItem().getItemMeta().getDisplayName());
                old_screen.init();
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_DYE)){
                CompanyPatentDesignerUpgrade new_screen = new CompanyPatentDesignerUpgrade(pat, old_screen.comp);
                player.openInventory(new_screen.getInventory());
            }
        }

        if (event.getClickedInventory().getHolder() instanceof CompanyAuctionMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            CompanyAuctionMenu old_screen = (CompanyAuctionMenu) event.getClickedInventory().getHolder();
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
                player.sendMessage("§bCompany is not valued enough to place the current Bet");
                return;
            }

            Auction.getInstance().setBit(location, currentBit, old_screen.comp.getStockName());
            old_screen.init();
        }

        if (event.getClickedInventory().getHolder() instanceof CompanyPatentDesignerUpgrade){
            event.setCancelled(true);
            CompanyPatentDesignerUpgrade old_screen = (CompanyPatentDesignerUpgrade) event.getClickedInventory().getHolder();
            PatentUpgrade pat_up = old_screen.patentUpgradeList.get(event.getSlot());
            Patent pat = old_screen.patent;
            pat.upgrades.add(pat_up);

            Player player = (Player) event.getWhoClicked();
            CompanyPatentDesignerMenu new_screen = new CompanyPatentDesignerMenu(pat, old_screen.comp);
            player.openInventory(new_screen.getInventory());
        }

        if (event.getClickedInventory().getHolder() instanceof CompanyPatentCrafting){
            event.setCancelled(true);
            CompanyPatentCrafting old_screen = (CompanyPatentCrafting) event.getClickedInventory().getHolder();
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equalsIgnoreCase("§6Get Patent Sheet")){
                Player player = (Player) event.getWhoClicked();
                if (old_screen.comp.patentCrafted < old_screen.comp.upgrades.get(3).getBonus()){
                    old_screen.comp.patentCrafted += 1;

                    ItemStack item = new ItemStack(Material.PAPER, 1);

                    ItemMeta m = item.getItemMeta();
                    m.setDisplayName("§6"+old_screen.comp.getStockName() +":"+old_screen.patent.getName()+":"+old_screen.comp.patent.indexOf(old_screen.patent));
                    item.setItemMeta(m);

                    player.getInventory().setItem(player.getInventory().firstEmpty(), item);
                }else{
                    player.sendMessage(ChatColor.RED+"Craft limit Reached");
                }
            }
        }

        if (event.getClickedInventory().getHolder() instanceof CountryPolicy){
            event.setCancelled(true);
            CountryPolicy old_screen = (CountryPolicy) event.getClickedInventory().getHolder();
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

        if (event.getClickedInventory().getHolder() instanceof CountryElectionMenu){
            ItemStack item = event.getCurrentItem();
            ItemMeta m = item.getItemMeta();
            String party = m.getDisplayName().substring(2);

            CountryElectionMenu old_screen = (CountryElectionMenu) event.getClickedInventory().getHolder();
            old_screen.e.vote(party, event.getWhoClicked().getUniqueId());

            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
        }

        if (event.getClickedInventory().getHolder() instanceof PartyDecisionVote){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();

            PartyDecisionVote old_screen = (PartyDecisionVote) event.getClickedInventory().getHolder();
            String countryName = old_screen.p.country;
            Country country = CountryManager.getCountry(countryName);
            Decision d = country.getDecisions().get(slot);

            PartyDecisionChoice new_screen = new PartyDecisionChoice(old_screen.p, d);
            player.openInventory(new_screen.getInventory());

        }

        if (event.getClickedInventory().getHolder() instanceof PartyDecisionChoice){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();

            PartyDecisionChoice old_screen = (PartyDecisionChoice) event.getClickedInventory().getHolder();
            old_screen.d.vote(old_screen.p, slot == 11);

            player.closeInventory();

        }

        if (event.getClickedInventory().getHolder() instanceof CountryMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equalsIgnoreCase("§6Policy")){
                CountryPolicy screen = new CountryPolicy(player.getUniqueId());
                player.openInventory(screen.getInventory());
            }else if (option.equalsIgnoreCase("§6Decisions")){
                CountryDecisionMenu screen = new CountryDecisionMenu(player.getUniqueId());
                player.openInventory(screen.getInventory());
            }
        }

        if (event.getClickedInventory().getHolder() instanceof CountryDecisionMenu){
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            String option = event.getCurrentItem().getItemMeta().getDisplayName();
            if (option.equalsIgnoreCase("§6Ban party")){
                Country country = CountryManager.getCountry(player);

                if (country.getCountryElection() != null){
                    player.sendMessage("§b you can`t ban parties during elections");
                    return;
                }
                CountryPartyBan screen = new CountryPartyBan(player.getUniqueId());
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

        if (event.getClickedInventory().getHolder() instanceof CountryPartyBan){
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

        if (event.getClickedInventory().getHolder() instanceof CompanyTypeSelect){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            CompanyTypeSelect old_screen = (CompanyTypeSelect) event.getClickedInventory().getHolder();
            String name = event.getCurrentItem().getItemMeta().getDisplayName();
            old_screen.comp.type = name.substring(2);
            player.closeInventory();
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
