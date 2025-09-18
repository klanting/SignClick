package com.klanting.signclick.interactionLayer.commands;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.interactionLayer.commands.partyHandlers.*;
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

        HashMap<String, PartyHandler> handlerTranslation = new HashMap<>();

        handlerTranslation.put("create", new PartyHandlerCreate());
        handlerTranslation.put("add", new PartyHandlerAdd());
        handlerTranslation.put("kick", new PartyHandlerKick());
        handlerTranslation.put("promote", new PartyHandlerPromote());
        handlerTranslation.put("demote", new PartyHandlerDemote());
        handlerTranslation.put("leave", new PartyHandlerLeave());
        handlerTranslation.put("info", new PartyHandlerInfo());
        handlerTranslation.put("vote", new PartyHandlerVote());
        handlerTranslation.put("coup", new PartyHandlerCoup());

        try{
            if (handlerTranslation.containsKey(commando)){
                PartyHandler ch = handlerTranslation.get(commando);

                ch.handleCommand(player, args);
            }
        }catch (CommandException e){
            player.sendMessage(e.getMessage());
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
