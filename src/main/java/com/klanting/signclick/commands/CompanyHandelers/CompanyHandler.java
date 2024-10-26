package com.klanting.signclick.commands.CompanyHandelers;

import com.klanting.signclick.commands.Exceptions.CommandException;
import org.bukkit.entity.Player;

public abstract class CompanyHandler {
    /**
    * Handle actions from the company commands
    * */

    /*
    * Situation when the command is entered
    * */
    abstract public Boolean handleCommand(Player player, String[] args, Boolean firstEnter) throws CommandException;



}
