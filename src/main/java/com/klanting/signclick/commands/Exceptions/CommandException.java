package com.klanting.signclick.commands.Exceptions;

public class CommandException extends Exception{
    public CommandException(String errorMessage) {
        super(errorMessage);
    }
}
