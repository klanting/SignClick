package com.klanting.signclick.commands;

import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Decisions.Decision;
import com.klanting.signclick.Economy.Parties.Election;
import com.klanting.signclick.Economy.Parties.Party;
import com.klanting.signclick.Menus.CountryElectionMenu;
import com.klanting.signclick.Menus.CountryMenu;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
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


public class BankCommands implements CommandExecutor, TabCompleter {
    private static Map<String, String> CountryInvites = new HashMap<String, String>();
    public static Map<String, Election> CountryElections = new HashMap<String, Election>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return false; }
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("country")){
            if (args.length == 0){
                player.sendMessage("§bplease enter /country <category>");
                return true;
            }
            String type = args[0];
            if (type.equals("bal")){
                DecimalFormat df = new DecimalFormat("###,###,###");
                if (args.length == 2){
                    player.sendMessage("§bsaldo: "+String.valueOf(df.format(Banking.bal(args[1]))));
                }else{
                    player.sendMessage("§bsaldo: "+String.valueOf(df.format(Banking.bal(Banking.Element(player)))));
                }


            }else if (type.equals("create")){

                String name = args[1];
                Player user = Bukkit.getServer().getPlayer(args[2]);
                Banking.create(name, user);

            }else if (type.equals("pay")){
                int amount;
                String p;
                try{
                    amount = Integer.parseInt(args[2]);
                    p = args[1];

                }catch (Exception e){
                    player.sendMessage("§bplease enter /country pay <player> <amount>");
                    return true;
                }

                String name = Banking.Element(player);

                if (Banking.isOwner(name, player)){
                    if (Banking.has(name, amount)){
                        if (!player.getName().equals(p)) {
                            try {
                                Player target = Bukkit.getServer().getPlayer(p);
                                SignClick.getEconomy().depositPlayer(target, amount);
                                target.sendMessage("§byou got " + amount + " from " + name);

                            } catch (Exception e) {
                                for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                                    if (target.getName().equals(p)) {
                                        SignClick.getEconomy().depositPlayer(target, amount);
                                    }
                                }
                            }
                            Banking.withdraw(name, amount);
                            player.sendMessage("§byou paid " + amount + " to " + p);
                        }
                    }else{
                        player.sendMessage("§byou have not enough money");
                    }

                }

            }else if (type.equals("donate")){
                String name;
                if (args.length == 3){
                    if (Banking.GetBanks().contains(args[1])){
                        name = args[1];
                    }else{
                        name = "none";
                    }
                }else{
                    name = Banking.Element(player);
                }
                if (name != "none"){

                    int amount;
                    try{
                        amount = Integer.parseInt(args[args.length-1]);


                        
                    }catch (Exception e){
                        player.sendMessage("§bplease enter /country donate [country] <amount>");
                        return true;
                    }

                    if (amount < 0){
                        player.sendMessage("§bYou cannot donate negative amounts");
                        return true;
                    }

                    if (SignClick.getEconomy().has(player, amount)){
                        Banking.deposit(name, amount);
                        SignClick.getEconomy().withdrawPlayer(player, amount);
                        player.sendMessage("§bYou paid " + amount + " to " + name);
                    }else{
                        player.sendMessage("§bYou have not enough money");
                        return true;
                    }

                    for (Player pl : Bukkit.getServer().getOnlinePlayers()){
                        if (Banking.GetOwners(name).contains(pl.getUniqueId())){
                            pl.sendMessage("§b"+player.getName()+" donated "+amount + " to your country");
                        }
                    }



                }else{
                    player.sendMessage("§bYou are not in a country or your designated country does not exist");
                }
            }else if (type.equals("baltop")){
                StringBuilder line = new StringBuilder("§bBaltop: ");
                int index = 1;
                for (String bank : Banking.getTop()){
                    if (index <= 10){
                        int amount = Banking.bal(bank);
                        DecimalFormat df = new DecimalFormat("###,###,###");
                        line.append("\n").append("§b"+index+".§3 ").append(bank).append(": §7").append(df.format(amount));
                        index += 1;
                    }

                }
                player.sendMessage(String.valueOf(line));

            }else if (type.equals("tax")){
                int amount;
                try{
                    amount = Integer.parseInt(args[1]);
                }catch (Exception e){
                    player.sendMessage("§bplease enter /country tax <amount>");
                    return true;
                }

                String name = Banking.Element(player);
                if (Banking.isOwner(name, player)){
                    if (0 <= amount && amount <= 20){
                        Banking.setPCT(name, amount);
                        player.sendMessage("§bthe tax has been changed");
                    }else{
                        player.sendMessage("§bpls enter an integer from 0 to 20");
                    }
                }else{
                    player.sendMessage("§byou are not allowed to do this");
                }
            }else if (type.equals("invite")){
                String name = Banking.Element(player);
                if (Banking.isOwner(name, player)){

                    String username;
                    try{
                        username = args[1];
                    }catch (Exception e){
                        player.sendMessage("§bplease enter /country invite <username>");
                        return true;
                    }

                    CountryInvites.put(username, name);
                    boolean inviteSend = false;
                    for (Player p: Bukkit.getOnlinePlayers()){
                        if (p.getName().equals(username)){
                            inviteSend = true;
                            p.sendMessage("§byou have an invite for §8"+name+ " §byou have 120s for accepting by \n" +
                                    "§c/country accept");


                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
                                public void run() {
                                    CountryInvites.remove(username);


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
            }else if (type.equals("accept")){
                if (CountryInvites.containsKey(player.getName())){
                    Banking.addMember(CountryInvites.get(player.getName()), player);
                    player.sendMessage("§byou succesfully joint this country");
                    player.setPlayerListName(Banking.GetColor(CountryInvites.get(player.getName()))+player.getName());
                }
            }else if (type.equals("kick")){
                String name = Banking.Element(player);
                if (Banking.isOwner(name, player)){
                    Player target;
                    try{
                        target = Bukkit.getServer().getPlayer(args[1]);
                    }catch (Exception e){
                        player.sendMessage("§bplease enter /country kick <> player");
                        return true;
                    }

                    Banking.removeMember(name, target);
                    player.sendMessage("§btarget has been kicked from your country");
                }else{
                    player.sendMessage("§byou are not allowed to kick members");
                }

            }else if (type.equals("info")){
                if (args.length == 2){
                    String name = args[1];
                    Banking.info(name, player);
                }else{
                    if (!Banking.Element(player).equals("none")){
                        Banking.info(Banking.Element(player), player);
                    }
                }
            }else if (type.equals("leave")){
                String name = Banking.Element(player);

                if (Banking.isOwner(name, player)){
                    Banking.removeOwner(name, player);
                }else{
                    Banking.removeMember(name, player);
                }
                player.sendMessage("§bcountry succesfully left");
            }else if (type.equals("setspawn")) {
                String name = Banking.Element(player);
                if (Banking.isOwner(name, player)) {
                    Banking.SetSpawn(name, player.getLocation());
                    player.sendMessage("§bspawn succesfully relocated");
                }

            }else if (type.equals("spawn")){
                String name;
                if ((player.hasPermission("signclick.staff")) && (args.length == 2)){
                    name = args[1];
                }else{
                    name = Banking.Element(player);
                }

                if (name != null){
                    Location loc = Banking.GetSpawn(name);
                    if (loc != null){
                        player.teleport(loc);
                        player.sendMessage("§bteleported to country spawn");
                    }else{
                        player.sendMessage("§bno country spawn has been set, owners can set it by entering /country setspawn");
                    }

                }else{
                    player.sendMessage("§byou are not in a country");
                }

            }else if (type.equals("add_enforcement")){
                if (args.length < 2){
                    player.sendMessage("§bplease enter /country add_enforcement <playername>");
                    return true;
                }
                String player_name = args[1];

                String country = Banking.Element(player);
                if (!Banking.isOwner(country, player)){
                    player.sendMessage("§byou are not country owner");
                    return true;
                }

                Player target = Bukkit.getPlayer(player_name);
                if (target != null){
                    Banking.addLawEnforcement(Banking.Element(player), target);
                    player.sendMessage("§byou succesfully assigned an law enforcement agent");
                }else{
                    player.sendMessage("§bassigning failed");
                    return true;
                }



            }else if (type.equals("remove_enforcement")){
                if (args.length < 2){
                    player.sendMessage("§bplease enter /country remove_enforcement <playername>");
                    return true;
                }
                String player_name = args[1];

                String country = Banking.Element(player);
                if (!Banking.isOwner(country, player)){
                    player.sendMessage("§byou are not country owner");
                    return true;
                }

                Player target = Bukkit.getPlayer(player_name);
                if (target != null){
                    Banking.removeLawEnforcement(country, target);
                    player.sendMessage("§byou succesfully resigned an law enforcement agent");
                }else{
                    for (OfflinePlayer op: Bukkit.getOfflinePlayers()){
                        if (op.getName().equals(player_name)){
                            Banking.removeLawEnforcement(country, op);
                            break;
                        }
                    }
                    player.sendMessage("§byou succesfully resigned an law enforcement agent");
                }


            }else if (type.equals("menu")) {
                String country = Banking.Element(player);

                if (!Banking.isOwner(country, player)){
                    player.sendMessage("§bplayer is not the owner");
                    return true;
                }

                CountryMenu screen = new CountryMenu(player.getUniqueId());
                player.openInventory(screen.getInventory());

            }else if (type.equals("election")) {
                String country = Banking.Element(player);

                if (!Banking.isOwner(country, player)){
                    player.sendMessage("§bplayer is not the owner");
                    return true;
                }

                if (CountryElections.containsKey(country)){
                    player.sendMessage("§bcountry is already in an election phase");
                    return true;
                }

                long system_end = System.currentTimeMillis()/1000 + 60*60*24*7;
                CountryElections.put(country, new Election(country, system_end));
                Banking.add_stability(country, 15.0);
                player.sendMessage("§belections started");


                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
                    public void run() {
                        Election e = CountryElections.get(country);
                        CountryElections.remove(country);

                        double total = 0.0;
                        for (float f : e.vote_dict.values()) {
                            total += f;
                        }

                        if (total == 0.0){
                            return;
                        }

                        double highest_pct = -0.1;
                        Party highest_party = null;

                        for (Party p: Banking.parties.getOrDefault(country, new ArrayList<>())){
                            double pct = (double) e.vote_dict.getOrDefault(p.name, 0)/total;
                            p.PCT = pct;

                            if (pct > highest_pct){
                                highest_pct = pct;
                                highest_party = p;
                            }
                        }

                        if (highest_party != Banking.getRuling(country)){
                            double base = 2.0*(1.0-Banking.getPolicyBonus(country, 2, 8));
                            Banking.add_stability(country, -base);
                        }

                        List<UUID> old_owners = Banking.owners.getOrDefault(country, new ArrayList<>());
                        List<UUID> members = Banking.members.getOrDefault(country, new ArrayList<>());
                        for (UUID uuid: old_owners){
                            members.add(uuid);
                        }
                        Banking.members.put(country, members);

                        Banking.owners.put(country, highest_party.owners);

                        for (UUID uuid: highest_party.owners){
                            members.remove(uuid);
                        }

                        for (Decision d: Banking.decisions.get(country)){
                            d.checkApprove();
                        }


                    }
                }, 60*20*60*24*7L);

                //Bukkit.getServer().getScheduler().getPendingTasks().get(event_id);
                //Bukkit.getServer().getScheduler().


            }

            else if (type.equals("vote")) {
                String country = Banking.Element(player);
                if (!CountryElections.containsKey(country)){
                    player.sendMessage("§bcountry is not in an election phase");
                    return true;
                }

                Election e = CountryElections.get(country);
                if (e.already_voted.contains(player.getUniqueId())){
                    player.sendMessage("§byou can`t vote twice");
                    return true;
                }

                CountryElectionMenu screen = new CountryElectionMenu(e);
                player.openInventory(screen.getInventory());

            }

            if (player.hasPermission("signclick.staff")){
                if (type.equals("setowner")){
                    Player p = Bukkit.getPlayer(args[2]);
                    assert p != null;
                    boolean suc6 = Banking.addOwner(args[1], p);

                    if (suc6){
                        p.sendMessage("you are added as owner");
                    }else{
                        p.sendMessage("you are already an owner");
                    }

                    player.sendMessage("§bowner has been set");
                }else if (type.equals("removeowner")){
                    Player p = Bukkit.getPlayer(args[2]);
                    assert p != null;
                    Banking.removeOwner(args[1], p);
                    player.sendMessage("§bowner has been set");
                }else if (type.equals("color")){
                    Banking.SetColor(args[1], args[2]);
                    player.sendMessage("§bcolor changed");
                }else if (type.equals("promote")){
                    try{
                        Player p = Bukkit.getPlayer(args[1]);
                        String name = Banking.Element(p);
                        Banking.removeMember(name, p);
                        boolean suc6 = Banking.addOwner(name, p);
                        if (suc6){
                            p.sendMessage("you are promoted to owner");
                        }else{
                            p.sendMessage("you are already owner, and so cannot be promoted");
                        }

                    }catch (Exception e){
                        for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                            if (target.getName().equalsIgnoreCase(args[1])) {
                                String name = Banking.Element(target);
                                Banking.removeMember(name, target);
                                Banking.addOwner(name, target);
                            }

                        }

                    }

                }else if (type.equals("demote")){
                    try{
                        Player p = Bukkit.getPlayer(args[1]);
                        String name = Banking.Element(p);
                        Banking.removeOwner(name, p);
                        Banking.addMember(name, p);
                    }catch (Exception e){
                        for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                            if (target.getName().equalsIgnoreCase(args[1])) {
                                String name = Banking.Element(target);
                                Banking.removeOwner(name, target);
                                Banking.addMember(name, target);
                            }

                        }

                    }
                }else if (type.equals("remove")) {
                    String name = args[1];
                    Banking.delete(name, player);

                }else if (type.equals("addmember")) {
                    Banking.addMember(args[1], Bukkit.getPlayer(args[2]));
                    player.sendMessage("§bplayer succesfully joint this country");
                    player.setPlayerListName(Banking.GetColor(CountryInvites.get(player.getName()))+player.getName());

                }else if (type.equals("removemember")) {
                    Banking.removeMember(args[2], Objects.requireNonNull(Bukkit.getPlayer(args[1])));
                    player.sendMessage("§bplayer succesfully left this country");
                    player.setPlayerListName(Banking.GetColor(CountryInvites.get(player.getName()))+player.getName());

                }

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
                    return Banking.GetBanks();
                }else if (player.hasPermission("signclick.staff")){
                    if (args[0].equals("setowner")){
                        return Banking.GetBanks();
                    }else if (args[0].equals("removeowner")){
                        return Banking.GetBanks();
                    }else if (args[0].equals("color")){
                        return Banking.GetBanks();
                    }else if (args[0].equals("spawn")){
                        return Banking.GetBanks();
                    }else if (args[0].equals("remove")){
                        return Banking.GetBanks();
                    }else if (args[0].equals("addmember")){
                        return Banking.GetBanks();
                    }else if (args[0].equals("removemember")){
                        return Banking.GetBanks();
                    }

                }
            }

        }
        return null;
    }
}

