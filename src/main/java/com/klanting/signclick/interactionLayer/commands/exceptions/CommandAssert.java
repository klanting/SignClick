package com.klanting.signclick.interactionLayer.commands.exceptions;

import com.klanting.signclick.utils.PermissionsSingleton;
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

        if (!PermissionsSingleton.getInstance().hasPermission(player, "signclick."+needPerm)){
            throw new CommandException(errorMessage);
        }
    }
}
