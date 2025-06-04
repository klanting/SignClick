package com.klanting.signclick.commands;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.decisions.DecisionCoup;
import com.klanting.signclick.economy.parties.Party;
import com.klanting.signclick.menus.party.DecisionVote;
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

public class PartyCommands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return true; }
        Player player = (Player) sender;

        if (!cmd.getName().equalsIgnoreCase("party")){
            return true;
        }
        if (args.length == 0){
            player.sendMessage("§bplease enter /party <category>");
            return true;
        }

        String commando = args[0];

        if (commando.equals("create")){
            if (args.length < 2){
                player.sendMessage("§bplease enter /party create <name>");
                return true;
            }

            Country country = CountryManager.getCountry(player);

            if (country == null){
                player.sendMessage("§bYou need to be in a country");
                return true;
            }

            if (country.isForbidParty()){
                player.sendMessage("§bcountry forbids create party");
                return true;
            }

            if (country.inParty(player.getUniqueId())){
                player.sendMessage("§byou are already in a party");
                return true;
            }

            if (country.hasPartyName(args[1])){
                player.sendMessage("§bparty name already exists");
                return true;
            }

            country.createParty(args[1], player.getUniqueId());
            country.addStability(3.0);
            player.sendMessage("§bparty created");
        }

        if (commando.equals("add")){
            Country country = CountryManager.getCountry(player);

            if (country == null){
                player.sendMessage("§bYou need to be in a country");
                return true;
            }

            if (args.length < 2){
                player.sendMessage("§bplease enter /party add <username>");
                return true;
            }

            String player_name = args[1];

            Player target_player = Bukkit.getPlayer(player_name);

            if (country.getName() != CountryManager.getCountry(player).getName()){
                player.sendMessage("§bplayer is in a different country");
                return true;
            }
            Party p = country.getParty(player.getUniqueId());
            Party p2 = country.getParty(target_player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }

            if (p2 != null){
                player.sendMessage("§btarget already in a party");
                return true;
            }

            if (!p.isOwner(player.getUniqueId())){
                player.sendMessage("§byou must be party owner");
                return true;
            }

            p.addMember(target_player.getUniqueId());
            player.sendMessage("§bplayer added to the party");
        }

        if (commando.equals("kick")){
            Country country = CountryManager.getCountry(player);

            if (country == null){
                player.sendMessage("§bYou need to be in a country");
                return true;
            }

            if (args.length < 2){
                player.sendMessage("§bplease enter /party kick <username>");
                return true;
            }

            String player_name = args[1];

            UUID uuid = null;
            for (OfflinePlayer of: Bukkit.getServer().getOfflinePlayers()){
                if (of.getName().equals(player_name)){
                    uuid = of.getUniqueId();
                    break;
                }
            }

            if (country.getName() != CountryManager.getCountry(uuid).getName()){
                player.sendMessage("§bplayer is in a different country");
                return true;
            }

            Party p = country.getParty(player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            if (!p.isOwner(player.getUniqueId())){
                player.sendMessage("§byou must be party owner");
                return true;
            }

            p.removeMember(uuid);
            player.sendMessage("§bplayer kicked to the party");

        }

        if (commando.equals("promote")){
            Country country = CountryManager.getCountry(player);

            if (country == null){
                player.sendMessage("§bYou need to be in a country");
                return true;
            }

            if (args.length < 2){
                player.sendMessage("§bplease enter /party promote <username>");
                return true;
            }

            String player_name = args[1];
            Player target_player = Bukkit.getPlayer(player_name);

            Party p = country.getParty(player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            if (!p.isOwner(player.getUniqueId())){
                player.sendMessage("§byou must be party owner");
                return true;
            }

            p.promote(target_player.getUniqueId());
            player.sendMessage("§bplayer is promoted");
        }

        if (commando.equals("demote")){
            Country country = CountryManager.getCountry(player);

            if (country == null){
                player.sendMessage("§bYou need to be in a country");
                return true;
            }

            if (args.length < 2){
                player.sendMessage("§bplease enter /party demote <username>");
                return true;
            }

            String player_name = args[1];
            Player target_player = Bukkit.getPlayer(player_name);

            Party p = country.getParty(player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            if (!p.isOwner(player.getUniqueId())){
                player.sendMessage("§byou must be party owner");
                return true;
            }

            p.demote(target_player.getUniqueId());
            player.sendMessage("§bplayer is demoted");
        }

        if (commando.equals("leave")){
            Country country = CountryManager.getCountry(player);

            if (country == null){
                player.sendMessage("§bYou need to be in a country");
                return true;
            }

            Party p = country.getParty(player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            p.removeMember(player.getUniqueId());
            player.sendMessage("§byou left the party");
        }

        if (commando.equals("info")){
            Party p;
            Country country = CountryManager.getCountry(player);
            if (args.length == 2){
                if (country == null){
                    player.sendMessage("§bYou need to be in a country");
                    return true;
                }
                p = country.getParty(args[1]);

            }else if (args.length >= 3){
                country = CountryManager.getCountry(args[1]);
                if (country == null){
                    player.sendMessage("§bYou need to be in a country");
                    return true;
                }
                p = country.getParty(args[2]);
            }else{
                if (country == null){
                    player.sendMessage("§bYou need to be in a country");
                    return true;
                }
                p = country.getParty(player.getUniqueId());
            }

            if (p != null){
                p.info(player);
            }else{
                player.sendMessage("§bparty does not exist");
            }
        }

        if (commando.equals("vote")){
            Country country = CountryManager.getCountry(player);

            if (country == null){
                player.sendMessage("§bYou need to be in a country");
                return true;
            }

            Party p = country.getParty(player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            if (!p.isOwner(player.getUniqueId())){
                player.sendMessage("§byou must be party owner");
                return true;
            }

            DecisionVote screen = new DecisionVote(p);
            player.openInventory(screen.getInventory());
        }

        if (commando.equals("coup")){
            Country country = CountryManager.getCountry(player);

            if (country == null){
                player.sendMessage("§bYou need to be in a country");
                return true;
            }

            Party p = country.getParty(player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            if (!p.isOwner(player.getUniqueId())){
                player.sendMessage("§byou must be party owner");
                return true;
            }

            if (p == country.getRuling()){
                player.sendMessage("§byou can`t start a coup against yourself");
                return true;
            }

            Decision d = new DecisionCoup("§6Stage a coup for party §9"+p.name, Math.max(0.9- country.getRuling().PCT, 0.05),
                    CountryManager.getCountry(player).getName(), p.name);

            country.addDecision(d);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return null; }

        if (command.getName().equalsIgnoreCase("party")) {
            List<String> autoCompletes = new ArrayList<>();

            if (args.length == 1) {
                autoCompletes.add("create");
                autoCompletes.add("add");
                autoCompletes.add("kick");
                autoCompletes.add("promote");
                autoCompletes.add("demote");
                autoCompletes.add("leave");
                autoCompletes.add("info");
                autoCompletes.add("vote");
                autoCompletes.add("coup");
            }else{
                return null;
            }

            return autoCompletes;
        }

        return null;
    }


}
