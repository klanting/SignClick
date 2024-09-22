package com.klanting.signclick.Calculate;

import com.klanting.signclick.Economy.*;
import com.klanting.signclick.SignClick;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SignTP {
    public static void tp(Sign sign, Player player){
        String[] cords = sign.getLine(1).split(" ");
        int x = Integer.parseInt(cords[0]);
        int y = Integer.parseInt(cords[1]);
        int z = Integer.parseInt(cords[2]);


        int amount = Integer.parseInt(sign.getLine(3));



        if (SignClick.getEconomy().getBalance(player) >= amount) {
            try{
                Player target = Bukkit.getServer().getPlayer(sign.getLine(2));
                SignClick.getEconomy().depositPlayer(target, amount);

            }catch(Exception e){
                for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                    if (target.getName().equals(sign.getLine(2))){
                        SignClick.getEconomy().depositPlayer(target, amount);
                    }
                }


            }

            SignClick.getEconomy().withdrawPlayer(player, amount);
            player.teleport(new Location(player.getLocation().getWorld(), x, y, z));


            player.sendMessage("§bYou are now Teleported");
        }
        else{
            player.sendMessage("§bYou have not enough money to travel");
        }

    }

    public static void set(SignChangeEvent sign, Player player){
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key_x = new NamespacedKey(SignClick.getPlugin(), "x");
        NamespacedKey key_y = new NamespacedKey(SignClick.getPlugin(), "y");
        NamespacedKey key_z = new NamespacedKey(SignClick.getPlugin(), "z");
        if (data.has(key_x, PersistentDataType.INTEGER)) {
            int x = data.get(key_x, PersistentDataType.INTEGER);

            int y = data.get(key_y, PersistentDataType.INTEGER);

            int z = data.get(key_z, PersistentDataType.INTEGER);

            int sign_x = sign.getBlock().getX();
            int sign_z = sign.getBlock().getZ();

            double results = Math.sqrt(((sign_x-x)*(sign_x-x))+((sign_z-z)*(sign_z-z)));
            int amount = (int)results;
            String amount_string = Integer.toString(amount);

            String cords = String.valueOf(x)+" "+String.valueOf(y)+" "+String.valueOf(z);

            Sign s = (Sign) sign.getBlock().getState();

            sign.setLine(0, "§b[sign_tp]");
            sign.setLine(1, cords);
            sign.setLine(2,player.getName());
            sign.setLine(3,amount_string);

            /*
            * For testing and wide support
            * */
            s.setLine(0, "§b[sign_tp]");
            s.setLine(1, cords);
            s.setLine(2,player.getName());
            s.setLine(3,amount_string);
            s.update();
        }else{

            Sign s = (Sign) sign.getBlock().getState();

            sign.setLine(1, "please connect");
            sign.setLine(2,"cords by");
            sign.setLine(3,"/signclickpos");

            s.setLine(1, "please connect");
            s.setLine(2,"cords by");
            s.setLine(3,"/signclickpos");
            s.update();

        }
    }

    public static void setBus(SignChangeEvent sign, Player player){
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key_x = new NamespacedKey(SignClick.getPlugin(), "x");
        NamespacedKey key_y = new NamespacedKey(SignClick.getPlugin(), "y");
        NamespacedKey key_z = new NamespacedKey(SignClick.getPlugin(), "z");
        if (data.has(key_x, PersistentDataType.INTEGER)) {
            int x = data.get(key_x, PersistentDataType.INTEGER);

            int y = data.get(key_y, PersistentDataType.INTEGER);

            int z = data.get(key_z, PersistentDataType.INTEGER);

            int sign_x = sign.getBlock().getX();
            int sign_z = sign.getBlock().getZ();

            double results = Math.sqrt(((sign_x-x)*(sign_x-x))+((sign_z-z)*(sign_z-z)))*1.10;
            int amount = (int)results;
            String amount_string = Integer.toString(amount);

            String cords = String.valueOf(x)+" "+String.valueOf(y)+" "+String.valueOf(z);
            String bus = sign.getLine(1);
            if (bus == null){
                player.sendMessage("§bplease enter company name on 2nd line");
                return;
            }

            bus = bus.toUpperCase();

            if (!Market.hasBusiness(bus)){
                player.sendMessage("§bcompany name invalid");
                sign.getBlock().setType(Material.AIR);
                return;
            }
            sign.setLine(0, "§b[sign_comp]");
            sign.setLine(1, cords);
            sign.setLine(2,bus);
            sign.setLine(3,amount_string);
        }else{
            sign.setLine(1, "please connect");
            sign.setLine(2,"cords by");
            sign.setLine(3,"/signclickpos");

        }
    }

    public static void tpBus(Sign sign, Player player){
        String[] cords = sign.getLine(1).split(" ");
        int x = Integer.parseInt(cords[0]);
        int y = Integer.parseInt(cords[1]);
        int z = Integer.parseInt(cords[2]);


        int amount = Integer.parseInt(sign.getLine(3));

        if (SignClick.getEconomy().getBalance(player) >= amount) {
            Company comp = Market.get_business(sign.getLine(2));

            Country country = CountryManager.getCountry(player);
            double d = country.getPolicyBonus(3, 0);
            int amount_first = amount;
            amount = (int) (amount*(1.0+d));

            if (comp == null){
                player.sendMessage("§bCompany invalid");
                return;
            }

            if (amount_first > amount){
                comp.add_bal((double) amount_first);
                country.withdraw(amount_first-amount);
            }else{
                comp.add_bal((double) amount);
            }

            SignClick.getEconomy().withdrawPlayer(player, amount);
            player.teleport(new Location(player.getLocation().getWorld(), x, y, z));


            player.sendMessage("§bYou are now Teleported");
        }
        else{
            player.sendMessage("§bYou have not enough money to travel");
        }

    }
}
