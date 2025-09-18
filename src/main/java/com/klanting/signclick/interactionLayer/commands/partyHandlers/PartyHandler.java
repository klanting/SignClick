package com.klanting.signclick.interactionLayer.commands.partyHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import org.bukkit.entity.Player;

public abstract class PartyHandler {
    /*
     * Situation when the command is entered
     * */
    abstract public void handleCommand(Player player, String[] args) throws CommandException;
}
