package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.ResearchOption;
import com.klanting.signclick.menus.PagingMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
    }

    public void init(){
        getInventory().clear();
        clearItems();

        comp.getResearch().checkProgress(comp);

        for(ResearchOption researchOption: comp.getResearch().getResearchOptions()){

            DecimalFormat df3 = new DecimalFormat("###,###,##0.00");
            List<String> l = new ArrayList<>();
            l.add("§7Research "+df3.format(researchOption.getProgress()*100)+"% completed");
            long remainingTime = researchOption.getRemainingTime();

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
        comp.getResearch().getResearchOptions().get(index).setModifierIndex(slot-2);
        init();

        return false;
    }
}
