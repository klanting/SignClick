package com.klanting.signclick.commands;

import com.klanting.signclick.SignClick;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SignCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return true; }
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("signclickpos")){
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();
            String worldName = player.getLocation().getWorld().getName();

            PersistentDataContainer data = player.getPersistentDataContainer();
            NamespacedKey key_x = new NamespacedKey(SignClick.getPlugin(), "x");
            data.set(key_x, PersistentDataType.INTEGER, x);
            NamespacedKey key_y = new NamespacedKey(SignClick.getPlugin(), "y");
            data.set(key_y, PersistentDataType.INTEGER, y);
            NamespacedKey key_z = new NamespacedKey(SignClick.getPlugin(), "z");
            data.set(key_z, PersistentDataType.INTEGER, z);
            NamespacedKey world = new NamespacedKey(SignClick.getPlugin(), "world");
            data.set(world, PersistentDataType.STRING, worldName);

            player.sendMessage("§bposition saved");

        }
        return true;
    }

}
