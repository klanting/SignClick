package com.klanting.signclick.commands;

import com.klanting.signclick.Economy.Country;
import com.klanting.signclick.Economy.Decisions.Decision;
import com.klanting.signclick.Economy.Decisions.DecisionCoup;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.Menus.PartyDecisionVote;
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

            String country = Country.Element(player);

            if (Country.forbid_party.getOrDefault(country, false)){
                player.sendMessage("§bcountry forbids create party");
                return true;
            }

            if (Country.inParty(country, player.getUniqueId())){
                player.sendMessage("§byou are already in a party");
                return true;
            }

            if (Country.hasPartyName(country, args[1])){
                player.sendMessage("§bparty name already exists");
                return true;
            }

            Country.createParty(country, args[1], player.getUniqueId());
            Country.add_stability(country, 3.0);
            player.sendMessage("§bparty created");
        }

        if (commando.equals("add")){
            String country = Country.Element(player);
            String player_name = args[1];

            Player target_player = Bukkit.getPlayer(player_name);

            if (country != Country.Element(target_player)){
                player.sendMessage("§bplayer is in a different country");
                return true;
            }
            Party p = Country.getParty(country, player.getUniqueId());
            Party p2 = Country.getParty(country, target_player.getUniqueId());
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
            String country = Country.Element(player);
            String player_name = args[1];

            UUID uuid = null;
            for (OfflinePlayer of: Bukkit.getServer().getOfflinePlayers()){
                if (of.getName() == player_name){
                    uuid = of.getUniqueId();
                    break;
                }
            }

            if (country != Country.ElementUUID(uuid)){
                player.sendMessage("§bplayer is in a different country");
                return true;
            }

            Party p = Country.getParty(country, player.getUniqueId());
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
            String country = Country.Element(player);
            String player_name = args[1];
            Player target_player = Bukkit.getPlayer(player_name);

            Party p = Country.getParty(country, player.getUniqueId());
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
            String country = Country.Element(player);
            String player_name = args[1];
            Player target_player = Bukkit.getPlayer(player_name);

            Party p = Country.getParty(country, player.getUniqueId());
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
            String country = Country.Element(player);
            Party p = Country.getParty(country, player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            p.removeMember(player.getUniqueId());
            player.sendMessage("§byou left the party");
        }

        if (commando.equals("info")){
            Party p;
            String country = Country.Element(player);
            if (args.length == 2){
                p = Country.getParty(country, args[1]);

            }else if (args.length >= 3){
                p = Country.getParty(args[1], args[2]);
            }else{
                p = Country.getParty(country, player.getUniqueId());
            }

            if (p != null){
                p.info(player);
            }else{
                player.sendMessage("§bparty does not exist");
            }
        }

        if (commando.equals("vote")){
            Party p = Country.getParty(Country.Element(player), player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            if (!p.isOwner(player.getUniqueId())){
                player.sendMessage("§byou must be party owner");
                return true;
            }

            PartyDecisionVote screen = new PartyDecisionVote(Country.getParty(Country.Element(player), player.getUniqueId()));
            player.openInventory(screen.getInventory());
        }

        if (commando.equals("coup")){
            Party p = Country.getParty(Country.Element(player), player.getUniqueId());
            if (p == null){
                player.sendMessage("§byou must be in a party");
                return true;
            }
            if (!p.isOwner(player.getUniqueId())){
                player.sendMessage("§byou must be party owner");
                return true;
            }

            if (p == Country.getRuling(Country.Element(player))){
                player.sendMessage("§byou can`t start a coup against yourself");
                return true;
            }

            Decision d = new DecisionCoup("§6Stage a coup for party §9"+p.name, Math.max(0.9- Country.getRuling(Country.Element(player)).PCT, 0.05), Country.Element(player), p.name);

            List<Decision> d_list = Country.decisions.getOrDefault(Country.Element(player), new ArrayList<>());
            d_list.add(d);
            Country.decisions.put(Country.Element(player), d_list);
        }


        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return null; }
        Player player = (Player) sender;

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
            }

            return autoCompletes;
        }

        return null;
    }


}
