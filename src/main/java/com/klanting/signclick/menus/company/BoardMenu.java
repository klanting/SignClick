package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BoardMenu extends SelectionMenu {
    public CompanyI comp;

    public BoardMenu(CompanyI company){
        super(54, "Company Board Menu: "+ company.getStockName(), true);
        comp = company;
        init();
    }

    public void init(){

        /*
         * Sign Block giving the UI a title
         * */
        ArrayList<String> l = new ArrayList<>();
        l.add("§6Board Seats: §9"+ comp.getCOM().getBoard().getBoardSeats());
        ItemStack value = ItemFactory.create(Material.OAK_SIGN, "§6Board Members", l);
        getInventory().setItem(4, value);

        List<Pair<UUID, Double>> boardMembers = comp.getCOM().getBoard().getBoardMembersWeight();
        for (int i=0; i<boardMembers.size(); i++){
            UUID boardMember = boardMembers.get(i).getLeft();
            Double weight = boardMembers.get(i).getRight();

            value = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) value.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(boardMember);
            meta.setOwningPlayer(player);

            meta.setDisplayName("§a"+(i+1)+". "+player.getName());

            DecimalFormat df = new DecimalFormat("##0.00");
            l = new ArrayList<>();
            l.add("§7"+df.format(weight*100)+"% support");
            meta.setLore(l);

            value.setItemMeta(meta);
            getInventory().setItem(9+i, value);
        }

        for (int i=0; i<comp.getCOM().getBoard().getBoardSeats()-boardMembers.size(); i++){
            ItemStack emptyGlass = ItemFactory.create(Material.WHITE_STAINED_GLASS, "§7Empty Board Slot");
            getInventory().setItem(9+boardMembers.size()+i, emptyGlass);
        }

        for (int i=0; i<9; i++){
            ItemStack blackGlass = ItemFactory.create(Material.BLACK_STAINED_GLASS_PANE, "");
            getInventory().setItem(27+i, blackGlass);
        }

        ItemStack assignButton = ItemFactory.create(Material.ITEM_FRAME, "§7Assign Board");
        getInventory().setItem(45, assignButton);

        super.init();
    }
}
