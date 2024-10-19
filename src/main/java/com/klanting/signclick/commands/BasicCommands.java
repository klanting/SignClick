package com.klanting.signclick.commands;

import com.klanting.signclick.calculate.WeeklyPay;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class BasicCommands implements CommandExecutor , TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return true; }
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("signclick")){
            player.sendMessage("§bstart your own payment travel system in some simple steps.\n" +
                                  "step 1: go to your endpoint location en do /signclickpos\n" +
                                  "step 2: set your sign on another location with the first line: §6[signclick_tp]");
        }else if (cmd.getName().equalsIgnoreCase("discord")){
            player.sendMessage("https://discord.gg/gTUsNBVQNg");
        }else if (cmd.getName().equalsIgnoreCase("dynmap")){
            player.sendMessage("http://klanting.ga:8880/");
        }else if (cmd.getName().equalsIgnoreCase("weeklypay")){
            if (args.length >= 1){
                if (args[0].equalsIgnoreCase("pay")){
                    if (args.length == 3){
                        WeeklyPay.start(player, args[1], Integer.parseInt(args[2]));
                    }else{
                        player.sendMessage("§bpls enter /weeklypay pay <receiver> <amount>");
                    }
                }else if(args[0].equalsIgnoreCase("list")){
                    if (args.length >= 2){
                        OfflinePlayer informationTarget = Bukkit.getOfflinePlayer(args[1]);
                        WeeklyPay.list(player, informationTarget);
                    }else{
                        WeeklyPay.list(player);
                    }
                }else if (args[0].equalsIgnoreCase("cancel")){
                    if (args.length >= 2){
                        WeeklyPay.stop(player, args[1]);
                    }else{
                       player.sendMessage("§bplease enter /weeklypay cancel <receiver>");
                    }

                }

            }else{
                player.sendMessage("§bplease enter /weeklypay <category>");
            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return null; }
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("weeklypay")) {
            List<String> autoCompletes = new ArrayList<>();
            if (args.length == 1) {
                autoCompletes.add("pay");
                autoCompletes.add("list");
                autoCompletes.add("change");
                autoCompletes.add("cancel");
                return autoCompletes;
            }else if (args.length == 2){
                if (args[0].equals("cancel")){
                    return WeeklyPay.receivers(player);
                }
            }


        }
        return null;
    }

}
