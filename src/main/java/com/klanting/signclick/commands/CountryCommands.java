package com.klanting.signclick.commands;

import com.klanting.signclick.commands.countryHandlers.*;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.parties.Election;
import com.klanting.signclick.Menus.CountryElectionMenu;
import com.klanting.signclick.Menus.CountryMenu;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

import static com.klanting.signclick.economy.parties.ElectionTools.setupElectionDeadline;


public class CountryCommands implements CommandExecutor, TabCompleter {
    private static final Map<String, String> countryInvites = new HashMap<String, String>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return false; }
        Player player = (Player) sender;

        if (!cmd.getName().equalsIgnoreCase("country")){
            return true;
        }

        if (args.length == 0){
            player.sendMessage("§bplease enter /country <category>");
            return true;
        }
        String commando = args[0];

        HashMap<String, CountryHandler> handlerTranslation = new HashMap<>();
        handlerTranslation.put("bal", new CountryHandlerBal());
        handlerTranslation.put("create", new CountryHandlerCreate());
        handlerTranslation.put("pay", new CountryHandlerPay());
        handlerTranslation.put("donate", new CountryHandlerDonate());
        handlerTranslation.put("baltop", new CountryHandlerBaltop());
        handlerTranslation.put("tax", new CountryHandlerTax());

        try{
            if (handlerTranslation.containsKey(commando)){
                CountryHandler ch = handlerTranslation.get(commando);

                ch.handleCommand(player, args);
            }
        }catch (CommandException e){
            player.sendMessage(e.getMessage());
            return true;
        }

        if (commando.equals("invite")){
            Country country = CountryManager.getCountry(player);
            if (country.isOwner(player)){

                String username;
                try{
                    username = args[1];
                }catch (Exception e){
                    player.sendMessage("§bplease enter /country invite <username>");
                    return true;
                }

                countryInvites.put(username, country.getName());
                boolean inviteSend = false;
                for (Player p: Bukkit.getOnlinePlayers()){
                    if (p.getName().equals(username)){
                        inviteSend = true;
                        p.sendMessage("§byou have an invite for §8"+country.getName()+ " §byou have 120s for accepting by \n" +
                                "§c/country accept");


                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
                            public void run() {
                                countryInvites.remove(username);
                            }
                        }, 20*120L);
                    }
                }
                if (inviteSend){
                    player.sendMessage("§bthe invite to join the country has been send to "+username);
                }else{
                    player.sendMessage("§bthe invite was unable to arrive at the player");
                }

            }else{
                player.sendMessage("§byou are not allowed to do this");
            }
        }else if (commando.equals("accept")){
            if (countryInvites.containsKey(player.getName())){
                String countryName = countryInvites.get(player.getName());
                Country country = CountryManager.getCountry(countryName);
                country.addMember(player);
                player.sendMessage("§byou succesfully joint this country");
                player.setPlayerListName(country.getColor()+player.getName());
            }else{
                player.sendMessage("§bNo pending invites");
            }
        }else if (commando.equals("kick")){
            Country country = CountryManager.getCountry(player);
            if (country != null && country.isOwner(player)){
                Player target;
                try{
                    target = Bukkit.getServer().getPlayer(args[1]);
                }catch (Exception e){
                    player.sendMessage("§bplease enter /country kick <> player");
                    return true;
                }

                country.removeMember(target);
                player.sendMessage("§btarget has been kicked from your country");
            }else{
                player.sendMessage("§byou are not allowed to kick members");
            }

        }else if (commando.equals("info")){
            Country country;
            if (args.length == 2){
                String name = args[1];
                country = CountryManager.getCountry(name);
            }else{
                country = CountryManager.getCountry(player);

            }

            if (country == null){
                player.sendMessage("§bprovided country is invalid, or the player did not specify a country name, while also not being inside one");
                return true;
            }

            country.info(player);
        }else if (commando.equals("leave")){
            Country country = CountryManager.getCountry(player);

            if (country.isOwner(player)){
                country.removeOwner(player);
            }else{
                country.removeMember(player);
            }
            player.sendMessage("§bcountry succesfully left");
        }else if (commando.equals("setspawn")) {
            Country country = CountryManager.getCountry(player);

            if (country.isOwner(player)) {
                country.setSpawn(player.getLocation());
                player.sendMessage("§bspawn succesfully relocated");
            }

        }else if (commando.equals("spawn")){
            Country country;
            if ((player.hasPermission("signclick.staff")) && (args.length == 2)){
                String countryName = args[1];
                country = CountryManager.getCountry(countryName);
            }else{
                country = CountryManager.getCountry(player);
            }

            if (country != null){
                Location loc = country.getSpawn();
                if (loc != null){
                    player.teleport(loc);
                    player.sendMessage("§bteleported to country spawn");
                }else{
                    player.sendMessage("§bno country spawn has been set, owners can set it by entering /country setspawn");
                }

            }else{
                player.sendMessage("§byou are not in a country");
            }

        }else if (commando.equals("add_enforcement")){
            if (args.length < 2){
                player.sendMessage("§bplease enter /country add_enforcement <player>");
                return true;
            }
            String player_name = args[1];

            Country country = CountryManager.getCountry(player);
            if (!country.isOwner(player)){
                player.sendMessage("§byou are not country owner");
                return true;
            }

            Player target = Bukkit.getPlayer(player_name);

            if (!country.getMembers().contains(target.getUniqueId())){
                player.sendMessage("§bOnly country members can be law enforcement");
                return true;
            }

            if (target != null){
                country.addLawEnforcement(target);
                player.sendMessage("§byou succesfully assigned an law enforcement agent");
            }else{
                player.sendMessage("§bassigning failed");
                return true;
            }

        }else if (commando.equals("remove_enforcement")){
            if (args.length < 2){
                player.sendMessage("§bplease enter /country remove_enforcement <player>");
                return true;
            }
            String player_name = args[1];

            Country country = CountryManager.getCountry(player);
            if (!country.isOwner(player)){
                player.sendMessage("§byou are not country owner");
                return true;
            }

            Player target = Bukkit.getPlayer(player_name);
            if (target != null){
                country.removeLawEnforcement(target);
                player.sendMessage("§byou succesfully resigned an law enforcement agent");
            }else{
                for (OfflinePlayer op: Bukkit.getOfflinePlayers()){
                    if (op.getName().equals(player_name)){
                        country.removeLawEnforcement(op);
                        break;
                    }
                }
                player.sendMessage("§byou succesfully resigned an law enforcement agent");
            }


        }else if (commando.equals("menu")) {
            Country country = CountryManager.getCountry(player);

            if (!country.isOwner(player)){
                player.sendMessage("§bplayer is not the owner");
                return true;
            }

            CountryMenu screen = new CountryMenu(player.getUniqueId());
            player.openInventory(screen.getInventory());

        }else if (commando.equals("election")) {
            Country country = CountryManager.getCountry(player);

            if (!country.isOwner(player)){
                player.sendMessage("§bplayer is not the owner");
                return true;
            }

            if (country.getCountryElection() != null){
                player.sendMessage("§bcountry is already in an election phase");
                return true;
            }

            long system_end = System.currentTimeMillis()/1000 + 60*60*24*7;
            country.addStability(15.0);
            player.sendMessage("§belections started");

            country.setCountryElection(new Election(country.getName(), system_end));

            long time = 60*20*60*24*7L;
            setupElectionDeadline(country, time);

        }

        else if (commando.equals("vote")) {
            Country country = CountryManager.getCountry(player);
            if (country.getCountryElection() == null){
                player.sendMessage("§bcountry is not in an election phase");
                return true;
            }

            Election e = country.getCountryElection();
            if (e.alreadyVoted.contains(player.getUniqueId())){
                player.sendMessage("§byou can`t vote twice");
                return true;
            }

            CountryElectionMenu screen = new CountryElectionMenu(e);
            player.openInventory(screen.getInventory());

        }

        if (player.hasPermission("signclick.staff")){
            if (commando.equals("setowner")){

                if (args.length < 3){
                    player.sendMessage("§bPlease enter /country setowner <country> <username>");
                    return true;
                }

                Player p = Bukkit.getPlayer(args[2]);
                assert p != null;

                Country country = CountryManager.getCountry(args[1]);
                boolean suc6 = country.addOwner(p);

                if (suc6){
                    p.sendMessage("§bYou are added as owner");
                }else{
                    p.sendMessage("§bYou are already an owner");
                }

                player.sendMessage("§bOwner has been set");

            }else if (commando.equals("removeowner")){

                if (args.length < 3){
                    player.sendMessage("§bPlease enter /country removeowner <country> <username>");
                    return true;
                }

                Player p = Bukkit.getPlayer(args[2]);
                assert p != null;
                Country country = CountryManager.getCountry(args[1]);
                country.removeOwner(p);
                player.sendMessage("§bowner has been set");
            }else if (commando.equals("color")){
                Country country = CountryManager.getCountry(args[1]);
                if (country == null){
                    player.sendMessage("§bThe country "+args[1]+" does not exists");
                    return true;
                }

                try {
                    country.setColor(ChatColor.valueOf(args[2]));
                    player.sendMessage("§bColor has been changed to "+args[2].toUpperCase());
                }catch (IllegalArgumentException e){
                    player.sendMessage("§bColor "+args[2].toUpperCase()+" is not a valid color");
                }

            }else if (commando.equals("promote")){

                if (args.length < 2){
                    player.sendMessage("§bPlease enter /country promote <username>");
                    return true;
                }

                try{
                    Player p = Bukkit.getPlayer(args[1]);

                    Country country = CountryManager.getCountry(p);
                    country.removeMember(p);
                    boolean suc6 = country.addOwner(p);
                    if (suc6){
                        p.sendMessage("you are promoted to owner");
                    }else{
                        p.sendMessage("you are already owner, and so cannot be promoted");
                    }

                }catch (Exception e){
                    for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                        if (target.getName().equalsIgnoreCase(args[1])) {
                            Country country = CountryManager.getCountry(target);
                            country.removeMember(target);
                            country.addOwner(target);
                        }

                    }

                }

            }else if (commando.equals("demote")){

                if (args.length < 2){
                    player.sendMessage("§bPlease enter /country demote <username>");
                    return true;
                }

                try{
                    Player p = Bukkit.getPlayer(args[1]);
                    Country country = CountryManager.getCountry(p);
                    country.removeOwner(p);
                    country.addMember(p);
                }catch (Exception e){
                    for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                        if (target.getName().equalsIgnoreCase(args[1])) {
                            Country country = CountryManager.getCountry(target);
                            country.removeOwner(target);
                            country.addMember(target);
                        }

                    }

                }
            }else if (commando.equals("remove")) {

                if (args.length < 2){
                    player.sendMessage("§bPlease enter /country remove <country>");
                    return true;
                }

                String name = args[1];
                Country country = CountryManager.getCountry(name);
                CountryManager.delete(country.getName(), player);

            }else if (commando.equals("addmember")) {

                if (args.length < 3){
                    player.sendMessage("§bPlease enter /country addmember <country> <username>");
                    return true;
                }

                Country country = CountryManager.getCountry(args[1]);

                Player addedPlayer = Bukkit.getPlayer(args[2]);
                country.addMember(addedPlayer);
                player.sendMessage("§bplayer succesfully joint this country");

                addedPlayer.setPlayerListName(country.getColor()+player.getName());

            }else if (commando.equals("removemember")) {

                if (args.length < 3){
                    player.sendMessage("§bPlease enter /country removemember <country> <username>");
                    return true;
                }

                Country country = CountryManager.getCountry(args[1]);

                Player removedPlayer = Objects.requireNonNull(Bukkit.getPlayer(args[2]));

                country.removeMember(removedPlayer);
                player.sendMessage("§bplayer succesfully left this country");
                removedPlayer.setPlayerListName(ChatColor.WHITE+player.getName());

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

        if (command.getName().equalsIgnoreCase("country")) {
            List<String> autoCompletes = new ArrayList<>();
            if (args.length == 1) {
                if (player.hasPermission("signclick.staff")){
                    autoCompletes.add("create");
                    autoCompletes.add("setowner");
                    autoCompletes.add("removeowner");
                    autoCompletes.add("color");
                    autoCompletes.add("promote");
                    autoCompletes.add("demote");
                    autoCompletes.add("remove");
                    autoCompletes.add("addmember");
                    autoCompletes.add("removemember");
                }
                autoCompletes.add("bal");
                autoCompletes.add("pay");
                autoCompletes.add("donate");
                autoCompletes.add("tax");
                autoCompletes.add("accept");
                autoCompletes.add("invite");
                autoCompletes.add("info");
                autoCompletes.add("leave");
                autoCompletes.add("kick");
                autoCompletes.add("spawn");
                autoCompletes.add("setspawn");
                autoCompletes.add("add_enforcement");
                autoCompletes.add("remove_enforcement");
                autoCompletes.add("menu");
                autoCompletes.add("election");
                autoCompletes.add("vote");

                return autoCompletes;

            }else if (args.length == 2){
                if (args[0].equals("donate") || args[0].equals("bal") || args[0].equals("info")){
                    return CountryManager.getCountriesString();
                }else if (player.hasPermission("signclick.staff")){
                    if (args[0].equals("setowner")){
                        return CountryManager.getCountriesString();
                    }else if (args[0].equals("removeowner")){
                        return CountryManager.getCountriesString();
                    }else if (args[0].equals("color")){
                        return CountryManager.getCountriesString();
                    }else if (args[0].equals("spawn")){
                        return CountryManager.getCountriesString();
                    }else if (args[0].equals("remove")){
                        return CountryManager.getCountriesString();
                    }else if (args[0].equals("addmember")){
                        return CountryManager.getCountriesString();
                    }else if (args[0].equals("removemember")){
                        return CountryManager.getCountriesString();
                    }

                }
            }

        }
        return null;
    }
}

