package com.klanting.signclick.commands.Exceptions;

public class CommandAssert {
    /*
    * Assertions on command information
    * */
    public static void assertTrue(Boolean passed, String errorMessage) throws CommandException {
        if (!passed){
            throw new CommandException(errorMessage);
        }
    }
}
