package com.klanting.signclick.interactionLayer.commands.exceptions;

public class CommandException extends Exception{
    public CommandException(String errorMessage) {
        super(errorMessage);
    }
}
