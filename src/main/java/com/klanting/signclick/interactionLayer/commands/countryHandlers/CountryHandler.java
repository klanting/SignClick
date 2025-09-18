package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import org.bukkit.entity.Player;

public abstract class CountryHandler {
    /*
     * Situation when the command is entered
     * */
    abstract public void handleCommand(Player player, String[] args) throws CommandException;
}
