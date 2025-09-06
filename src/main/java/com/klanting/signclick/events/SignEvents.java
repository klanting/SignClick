package com.klanting.signclick.events;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CompanyI;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.menus.company.MarketMenu;
import com.klanting.signclick.routines.SignIncome;
import com.klanting.signclick.routines.SignStock;
import com.klanting.signclick.signs.SignShop;
import com.klanting.signclick.signs.SignTP;
import com.klanting.signclick.signs.SignLookup;
import com.klanting.signclick.utils.Prefix;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignEvents implements Listener {

    private static final SignLookup slTp = new SignLookup("tp");
    private static final SignLookup slIn = new SignLookup("in");
    private static final SignLookup slComp = new SignLookup("comp");
    private static final SignLookup slStock = new SignLookup("stock");

    public static final SignLookup slShop = new SignLookup("shop");


    @EventHandler(priority = EventPriority.NORMAL)
    public static void OnSignClick(PlayerInteractEvent event){

        if(event.getClickedBlock() != null){
            if(event.getClickedBlock().getState() instanceof org.bukkit.block.Sign){

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Sign sign = (Sign) event.getClickedBlock().getState();
                    Player player = event.getPlayer();

                    String define = sign.getLine(0);

                    //start options
                    if (slTp.equals(define)){
                        SignTP.tp(sign, player);
                        event.setCancelled(true);
                    }else if(slIn.equals(define)){
                        SignIncome.Open(sign, player);
                        event.setCancelled(true);

                    }else if (slComp.equals(define)){
                        SignTP.tpBus(sign, player);
                        event.setCancelled(true);
                    }else if (slStock.equals(define)){
                        event.setCancelled(true);

                        String stockName = sign.getLine(1);

                        CompanyI comp = Market.getCompany(stockName);
                        if (comp == null){
                            Prefix.sendMessage(player, "Company not found");
                            return;
                        }

                        MarketMenu mm = new MarketMenu(player.getUniqueId(), comp, false);
                        player.openInventory(mm.getInventory());

                    }else if (slShop.equals(define)){
                        SignShop.onSign(sign, player);
                        event.setCancelled(true);
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
                if (slTp.preEquals(define)) {
                    SignTP.set(sign, player);
                }else if(slIn.preEquals(define)){
                    SignIncome.Set(sign, player);
                }else if (slComp.preEquals(define)){
                    SignTP.setBus(sign, player);
                }else if (slStock.preEquals(define)){
                    if (sign.getLine(1) != null){
                        SignStock.set(sign, player);

                    }else{
                        player.sendMessage(SignClick.getPrefix() +"no company given");
                    }

                }else if (slShop.preEquals(define)){
                    SignShop.setSign(sign, player);
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
