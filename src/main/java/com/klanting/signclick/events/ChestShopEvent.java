package com.klanting.signclick.events;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.utils.Prefix;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class ChestShopEvent implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) return;

        if (action != Action.RIGHT_CLICK_BLOCK){
            return;
        }

        if(!(clickedBlock.getState() instanceof Chest chest)){
            return;
        }

        Inventory inv = chest.getInventory();
        InventoryHolder holder = inv.getHolder();

        /*
        * Check double chest, adn store list of chests
        * */
        List<Chest> chests = List.of(chest);
        if(holder instanceof DoubleChest doubleChest){
            chests = List.of((Chest) doubleChest.getLeftSide(), (Chest) doubleChest.getRightSide());
        }

        /*
        * for each chest check if a sign is connected to the chest by any side
        * This sign must be a shop sign, if so restrict access
        * */
        CompanyI company = null;

        for(Chest chestI: chests){
            for(BlockFace face: new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}){
                Block relative = chestI.getBlock().getRelative(face);

                if(relative.getState() instanceof org.bukkit.block.Sign sign){
                    /*
                    * check this is a chestShop
                    * */
                    if(SignEvents.slShop.equals(sign.getLine(0))){
                        company = Market.getCompany(sign.getLine(2));
                    }
                }
            }
        }

        if(company == null){
            return;
        }

        if(!company.getCOM().isEmployee(player.getUniqueId())){
            Prefix.sendMessage(player, "Can't open chest when not being employee");
            event.setCancelled(true);

        }
    }
}
