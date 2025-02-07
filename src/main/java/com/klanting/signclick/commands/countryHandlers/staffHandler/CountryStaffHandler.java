package com.klanting.signclick.commands.countryHandlers.staffHandler;

import com.klanting.signclick.commands.countryHandlers.CountryHandler;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import org.bukkit.entity.Player;

public abstract class CountryStaffHandler extends CountryHandler {
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        handleStaffCommand(player, args);
    }

    public abstract void handleStaffCommand(Player player, String[] args) throws CommandException;
}
