package com.klanting.signclick.commands;

import com.klanting.signclick.commands.countryHandlers.*;
import com.klanting.signclick.commands.countryHandlers.staffHandler.*;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.CountryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class CountryCommands implements CommandExecutor, TabCompleter {
    public static final Map<String, String> countryInvites = new HashMap<String, String>();

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

        handlerTranslation.put("create", new CountryHandlerCreate());
        handlerTranslation.put("bal", new CountryHandlerBal());
        handlerTranslation.put("pay", new CountryHandlerPay());
        handlerTranslation.put("donate", new CountryHandlerDonate());
        handlerTranslation.put("baltop", new CountryHandlerBaltop());
        handlerTranslation.put("tax", new CountryHandlerTax());
        handlerTranslation.put("invite", new CountryHandlerInvite());
        handlerTranslation.put("accept", new CountryHandlerAccept());
        handlerTranslation.put("kick", new CountryHandlerKick());
        handlerTranslation.put("info", new CountryHandlerInfo());
        handlerTranslation.put("leave", new CountryHandlerLeave());
        handlerTranslation.put("setspawn", new CountryHandlerSetSpawn());
        handlerTranslation.put("spawn", new CountryHandlerSpawn());
        handlerTranslation.put("add_enforcement", new CountryHandlerAddEnforcement());
        handlerTranslation.put("remove_enforcement", new CountryHandlerRemoveEnforcement());
        handlerTranslation.put("menu", new CountryHandlerMenu());
        handlerTranslation.put("election", new CountryHandlerElection());
        handlerTranslation.put("vote", new CountryHandlerVote());
        handlerTranslation.put("guide", new CountryHandlerGuide());

        /*
        * Staff only commands
        * */
        handlerTranslation.put("setowner", new CountryHandlerSetOwner());
        handlerTranslation.put("removeowner", new CountryHandlerRemoveOwner());
        handlerTranslation.put("color", new CountryHandlerColor());
        handlerTranslation.put("promote", new CountryHandlerPromote());
        handlerTranslation.put("demote", new CountryHandlerDemote());
        handlerTranslation.put("remove", new CountryHandlerRemove());
        handlerTranslation.put("addmember", new CountryHandlerAddMember());
        handlerTranslation.put("removemember", new CountryHandlerRemoveMember());


        try{
            if (handlerTranslation.containsKey(commando)){
                CountryHandler ch = handlerTranslation.get(commando);

                ch.handleCommand(player, args);
            }
        }catch (CommandException e){
            player.sendMessage(e.getMessage());
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
                autoCompletes.add("guide");

                return autoCompletes;

            }else if (args.length == 2){
                if (args[0].equals("donate") || args[0].equals("bal") || args[0].equals("info")){
                    return CountryManager.getCountriesString();
                }else if (player.hasPermission("signclick.staff")){

                    Set<String> completeCountries = Set.of(
                            "setowner", "removeowner", "color",
                            "spawn", "remove", "addmember", "removemember");

                    if (completeCountries.contains(args[0])){
                        return CountryManager.getCountriesString();
                    }

                }
            }

        }
        return null;
    }
}

