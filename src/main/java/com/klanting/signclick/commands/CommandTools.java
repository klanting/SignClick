package com.klanting.signclick.commands;

import com.klanting.signclick.commands.exceptions.CommandException;

public class CommandTools {
    public static double parseDouble(String input, String errorMessage) throws CommandException {

        double amount;

        try {
            amount = Double.parseDouble(input);
        }catch (NumberFormatException nfe){
            throw new CommandException(errorMessage);
        }
        return amount;
    }

    public static int parseInteger(String input, String errorMessage) throws CommandException {

        int amount;

        try {
            amount = Integer.parseInt(input);
        }catch (NumberFormatException nfe){
            throw new CommandException(errorMessage);
        }
        return amount;
    }
}
