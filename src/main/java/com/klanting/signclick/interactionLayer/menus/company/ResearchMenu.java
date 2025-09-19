package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.ResearchOption;
import com.klanting.signclick.interactionLayer.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResearchMenu extends PagingMenu {
    public CompanyI comp;

    public ResearchMenu(UUID uuid, CompanyI company){
        super(54, "Company Research", true);
        comp = company;

        assert comp.getCOM().getBoard().getBoardMembers().contains(uuid);

        init();
        startTitleUpdater(Bukkit.getPlayer(uuid));
    }

    public void init(){
        getInventory().clear();
        clearItems();

        comp.getResearch().checkProgress();

        for(ResearchOption researchOption: comp.getResearch().getResearchOptions()){

            DecimalFormat df3 = new DecimalFormat("###,###,##0.00");
            List<String> l = new ArrayList<>();
            l.add("§7Research "+df3.format(researchOption.getProgress()*100)+"% completed");

            double compUpgradeModifier = (double) comp.getUpgrades().get(5).getBonus()/100.0;
            long remainingTime = (long) (researchOption.getRemainingTime()/compUpgradeModifier);

            if (researchOption.isResearching()){
                l.add("§7"+remainingTime/3600+"h "+(remainingTime%3600)/60 + "m "+remainingTime%60+"s");
            }else{
                l.add("§7IDLE");
            }


            ItemStack researchItem = ItemFactory.create(researchOption.getMaterial(),
                    "§7"+researchOption.getMaterial().name()+" Research", l);

            addItem(researchItem);

            /*
            * Add gray glass
            * */
            addItem(ItemFactory.createGray());

            /*
            * Give sliders
            * */

            DecimalFormat df = new DecimalFormat("###,###,##0");
            DecimalFormat df2 = new DecimalFormat("###,###,###");

            for (int i=0; i<6; i++){

                if (researchOption.isComplete()){

                    addItem(ItemFactory.create(Material.LIME_STAINED_GLASS_PANE, "§aCOMPLETED"));

                    continue;
                }

                Material mat;
                if (i < researchOption.getModifierIndex()){
                    mat = Material.LIGHT_BLUE_STAINED_GLASS_PANE;
                } else if (i == researchOption.getModifierIndex()){
                    if (researchOption.isResearching()){
                        mat = Material.WHITE_STAINED_GLASS_PANE;
                    }else{
                        mat = Material.RED_STAINED_GLASS_PANE;
                    }
                }else{
                    mat = Material.BLACK_STAINED_GLASS_PANE;
                }

                l = new ArrayList<>();
                String modifier = df.format(ResearchOption.modifiers.get(i).getLeft()*100);
                l.add("§7Speed: "+modifier+"%");
                l.add("§7Cost: $"+df2.format(ResearchOption.modifiers.get(i).getRight())+"/h");

                if (researchOption.isResearching() && (comp.getValue() <= 0.5 || comp.getSpendable() <= 0.5)){
                    l.add("§cResearch frozen by lack of funds or spendable");
                }

                addItem(ItemFactory.create(mat,
                        "§7"+modifier+"%", l));
            }

            addItem(ItemFactory.createGray());
        }

        super.init();
        onOpen();
    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }
        event.setCancelled(true);


        int slot = event.getSlot() % 9;
        if (!(slot >= 2 && slot <= 7)){
            return false;
        }
        int index = getPage()*5+(event.getSlot()/9);

        int newIndex = slot-2;
        ResearchOption researchOption = comp.getResearch().getResearchOptions().get(index);

        if (researchOption.getModifierIndex() != newIndex){
            /*
            * Store a log of this research change
            * */
            int old = researchOption.getModifierIndex();
            double oldModifier = ResearchOption.modifiers.get(old).getLeft();
            double newModifier = ResearchOption.modifiers.get(newIndex).getLeft();

            DecimalFormat df = new DecimalFormat("###,###,###");

            comp.update("Research priority Change",
                    "§7From "+ df.format(oldModifier*100)+"% to "+
                            df.format(newModifier*100)+"% for product "+researchOption.getMaterial().toString()
                    , event.getWhoClicked().getUniqueId());

            /*
            * Change the modifier
            * */
            researchOption.setModifierIndex(newIndex);
        }


        init();

        return false;
    }

    public void startTitleUpdater(Player player) {
        BukkitRunnable titleUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                Inventory top = player.getOpenInventory().getTopInventory();

                if (top == null ||!top.equals(getInventory())) {
                    cancel();
                    return;
                }

                init();

            }
        };

        titleUpdater.runTaskTimer(SignClick.getPlugin(), 0L, 20L); // every second
    }
}
