package com.klanting.signclick.menus.company;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.Board;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.events.AddChiefSupportEvent;
import com.klanting.signclick.menus.SelectionMenu;
import com.klanting.signclick.utils.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChiefMenu extends SelectionMenu {
    public CompanyI comp;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public final String position;
    private final UUID uuid;

    public ChiefMenu(UUID uuid, CompanyI company, String position){
        super(27, "Company Chief Menu: "+position, true);
        comp = company;
        this.position = position;
        this.uuid = uuid;

        assert comp.getCOM().getBoard().getBoardMembers().contains(uuid);

        init();
    }

    public void init(){

        getInventory().clear();
        Board board = comp.getCOM().getBoard();
        UUID chief = board.getChief(position);

        /*
        * Show current CEO
        * */
        ItemStack chiefItem;
        if (chief == null){
            chiefItem = ItemFactory.create(Material.IRON_HELMET, "§7"+position+": Unassigned");
        }else{
            chiefItem = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) chiefItem.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(chief);
            meta.setOwningPlayer(player);

            Pair<UUID, Double> match = board.chiefRanking(position).stream()
                    .filter(pair -> pair.getKey().equals(chief))
                    .findFirst().get();

            meta.setDisplayName("§7"+position+": "+player.getName());
            meta.setLore(List.of("§7"+position+" Votes: "+match.getRight().intValue()));

            chiefItem.setItemMeta(meta);
        }

        getInventory().setItem(13, chiefItem);

        /*
        * Orange Glass around head
        * */
        List<Integer> orangePos = List.of(3, 4, 5, 12, 14, 21, 22, 23);
        for (Integer pos: orangePos){
            ItemStack orangeGlass = ItemFactory.create(Material.ORANGE_STAINED_GLASS_PANE, "");

            getInventory().setItem(pos, orangeGlass);
        }

        Double change = SignClick.getPlugin().getConfig().getDouble("chiefSalaryChange");

        ItemStack incSalary = ItemFactory.create(Material.LIME_STAINED_GLASS_PANE, "§aIncrease Salary by "+change);
        ItemStack decrSalary = ItemFactory.create(Material.RED_STAINED_GLASS_PANE, "§cDecrease Salary by "+change);

        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        List<String> l = new ArrayList<>();
        l.add("§7Your Given Salary: "+df.format(board.getSalaryMap(uuid, position)));
        ItemStack avgSalary = ItemFactory.create(Material.WHITE_STAINED_GLASS_PANE,
                "§7(Avg) Salary: "+df.format(board.getSalary(position)), l);

        getInventory().setItem(0, incSalary);
        getInventory().setItem(9, avgSalary);
        getInventory().setItem(18, decrSalary);

        /*
        * Board Vote Button
        * */
        UUID chiefVote = comp.getCOM().getBoard().getChiefSupport(position, uuid);

        l = new ArrayList<>();
        if (chiefVote == null){
            chiefItem = ItemFactory.create(Material.SKELETON_SKULL, "§7Supporting No one yet");
        }else{
            chiefItem = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) chiefItem.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(chiefVote);
            meta.setOwningPlayer(player);

            meta.setDisplayName("§7Supporting: "+player.getName());
            Pair<UUID, Double> match = board.chiefRanking(position).stream()
                    .filter(pair -> pair.getKey().equals(chiefVote))
                    .findFirst().get();
            l.add("§7"+position+" Votes: "+match.getRight().intValue());

            chiefItem.setItemMeta(meta);
        }

        l.add("§7Click to Assign or Reassign");
        l.add("§7your support for the "+position + " position");
        ItemMeta meta = chiefItem.getItemMeta();
        meta.setLore(l);
        chiefItem.setItemMeta(meta);

        getInventory().setItem(24, chiefItem);

        super.init();
    }

    public boolean onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        if(event.getSlot() == 24){
            AddChiefSupportEvent.waitForMessage.put(player, this);
            player.closeInventory();
            player.sendMessage("§bEnter the supported player its username");
        }

        String option = event.getCurrentItem().getItemMeta().getDisplayName();

        Double change = SignClick.getPlugin().getConfig().getDouble("chiefSalaryChange");

        if (option.contains("§aIncrease Salary")){
            comp.getCOM().getBoard().boardChangeSalary(player.getUniqueId(), position, change);
            init();
        }

        if (option.contains("§cDecrease Salary")){
            comp.getCOM().getBoard().boardChangeSalary(player.getUniqueId(), position, -change);
            init();
        }


        return false;
    }


}
