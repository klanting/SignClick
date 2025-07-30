package com.klanting.signclick.events;

import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.menus.company.MarketMenu;
import com.klanting.signclick.routines.SignIncome;
import com.klanting.signclick.routines.SignStock;
import com.klanting.signclick.routines.SignTP;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

import static org.bukkit.Bukkit.getServer;

public class SignEvents implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public static void OnSignClick(PlayerInteractEvent event){

        if(event.getClickedBlock() != null){
            if(event.getClickedBlock().getState() instanceof org.bukkit.block.Sign){

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Sign sign = (Sign) event.getClickedBlock().getState();
                    Player player = event.getPlayer();

                    String define = sign.getLine(0);
                    //start options
                    if (define.equalsIgnoreCase("§b[signclick_tp]")||define.equalsIgnoreCase("§b[sign_tp]")){
                        SignTP.tp(sign, player);
                    }else if(define.equalsIgnoreCase("§b[sign_in]")){
                        SignIncome.Open(sign, player);

                    }else if (define.equalsIgnoreCase("§b[signclick_comp]")||define.equalsIgnoreCase("§b[sign_comp]")){
                        SignTP.tpBus(sign, player);
                    }else if (define.equalsIgnoreCase("§b[stock]")){
                        String stockName = sign.getLine(1);

                        CompanyI comp = Market.getCompany(stockName);
                        if (comp == null){
                            return;
                        }

                        MarketMenu mm = new MarketMenu(player.getUniqueId(), comp, false);
                        player.openInventory(mm.getInventory());

                    }


                }

            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public static void OnSignSet(SignChangeEvent sign){

        if (sign.getLine(0) != null){
            Player player = sign.getPlayer();
            String define = sign.getLine(0);

                if ((define.equalsIgnoreCase("[signclick_tp]"))||(define.equalsIgnoreCase("[sign_tp]"))) {
                    SignTP.set(sign, player);
                }else if(define.equalsIgnoreCase("[sign_in]")){
                    SignIncome.Set(sign, player);
                }else if (define.equalsIgnoreCase("[sign_comp]")){
                    SignTP.setBus(sign, player);
                }else if (define.equalsIgnoreCase("[stock]")){
                    if (sign.getLine(1) != null){
                        SignStock.set(sign, player);

                    }else{
                        player.sendMessage("§b no company given");
                    }

                }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void OnDoorBreak(BlockBreakEvent event){
        event.setCancelled(SignIncome.Destroy(event));

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void OnDoorOpen(PlayerInteractEvent event){
        if(event.getClickedBlock() != null){
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(SignIncome.cancelOpenDoor(event));
            }
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void OnSignBreak(BlockBreakEvent event){
        if (!(event.getBlock().getState() instanceof Sign)){
            return;
        }
        Sign s = (Sign) event.getBlock().getState();
        String define = s.getLine(0);
        if (define.equalsIgnoreCase("§b[stock]")){
            SignStock.delete(s);
        }

    }


}
