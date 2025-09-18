package com.klanting.signclick.interactionLayer.commands.exceptions;

import org.bukkit.entity.Player;

public class CommandAssert {
    /*
    * Assertions on command information
    * */
    public static void assertTrue(Boolean passed, String errorMessage) throws CommandException {
        if (!passed){
            throw new CommandException(errorMessage);
        }
    }

    public static void assertPerms(Player player, String needPerm, String errorMessage) throws CommandException {

        if (!player.hasPermission("signclick."+needPerm)){
            throw new CommandException(errorMessage);
        }
    }
}
