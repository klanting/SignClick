package com.klanting.signclick.commands;

import com.klanting.signclick.commands.exceptions.CommandException;

public class CommandTools {
    public static double parseDouble(String input, String errorMessage) throws CommandException {

        double amount;

        try {
            amount = Double.parseDouble(input);

            if (amount < 0){
                throw new CommandException(errorMessage);
            }

        }catch (NumberFormatException nfe){
            throw new CommandException(errorMessage);
        }
        return amount;
    }

    public static int parseInteger(String input, String errorMessage) throws CommandException {

        int amount;

        try {
            amount = Integer.parseInt(input);

            if (amount < 0){
                throw new CommandException(errorMessage);
            }

        }catch (NumberFormatException nfe){
            throw new CommandException(errorMessage);
        }
        return amount;
    }

    public static String parseString(String input, String errorMessage) throws CommandException {
        if (input.matches("^[a-zA-Z0-9_-]+$")){
            return input;
        }

        throw new CommandException(errorMessage);
    }
}
