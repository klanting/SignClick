package com.klanting.signclick.interactionLayer.events;

import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;


public class PatentEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public static void OnDamage(EntityDamageEvent event){
        if (! (event.getEntity() instanceof Player player)){
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            double jump_distance = getGearValue(player.getInventory().getArmorContents(), "JumpBonus");

            if (jump_distance == 0){
                return;
            }

            double damage = Math.min(event.getFinalDamage()*(jump_distance/100.0), 15.0);
            for (Player target: Bukkit.getServer().getOnlinePlayers()){
                if (target == player){
                    return;
                }

                double player_distance = target.getLocation().distance(player.getLocation());
                if (player_distance < jump_distance){
                    double deal_damage = damage*(1-player_distance/jump_distance);


                    if (target.getHealth()-deal_damage <= 0){
                        target.setHealth(1);
                    }else{
                        target.damage(deal_damage);
                    }

                    event.setDamage(event.getFinalDamage()-deal_damage);
                }
            }

        }else if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){
            double evade_chance = getGearValue(player.getInventory().getArmorContents(), "EvadeBonus");

            int evade_val = (int) evade_chance*10;

            Random rand = new Random();
            int compare = rand.nextInt(1000);
            if (compare < evade_val){
                event.setCancelled(true);
            }

            double refill_chance = getGearValue(player.getInventory().getArmorContents(), "RefillBonus");

            int refill_val = (int) refill_chance*10;

            compare = rand.nextInt(1000);
            if (compare < refill_val){
                player.setFoodLevel(player.getFoodLevel()+1);
            }

            if (!(event instanceof EntityDamageByEntityEvent)) {
                return;
            }

            Entity attacker = ((EntityDamageByEntityEvent)event).getDamager();

            if (!(attacker instanceof Player)){
                return;
            }

            Player attacker_player = (Player) attacker;

            double cunning_chance = getGearValue(player.getInventory().getArmorContents(), "CunningBonus");

            int cunning_val = (int) cunning_chance*10;

            compare = rand.nextInt(1000);
            if (compare < cunning_val){
                attacker_player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3, 1));
            }
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void OnPlayerXp(PlayerExpChangeEvent event){
        Player player = event.getPlayer();

        Country country = CountryManager.getCountry(player);

        if(country == null){
            return;
        }

        double m = 1.0 + country.getPolicyBonus("xpGain");
        event.setAmount((int) Math.round(event.getAmount()*m));
    }

    public static double getGearValue(ItemStack[] items, String param){
        double end_value = 0.0;
        for (ItemStack gear: items){
            if (gear != null){
                ItemMeta m = gear.getItemMeta();

                if (m == null || m.getLore() == null){
                    continue;
                }

                for (String lore: m.getLore()){
                    if (lore.contains(param)){
                        String range_str = lore.substring(lore.indexOf(":")+2);
                        double range = Double.parseDouble(range_str);
                        end_value += range;
                    }
                }
            }

        };
        return end_value;
    }

}

