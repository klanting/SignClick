package com.klanting.signclick.commands.partyHandlers;

import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.economy.decisions.DecisionCoup;
import com.klanting.signclick.economy.parties.Party;
import org.bukkit.entity.Player;

public class PartyHandlerCoup extends PartyHandler {
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {
        Country country = CountryManager.getCountry(player);

        CommandAssert.assertTrue(country != null, "§bYou need to be in a country");

        Party p = country.getParty(player.getUniqueId());
        CommandAssert.assertTrue(p != null, "§bYou must be in a party");
        CommandAssert.assertTrue(p.isOwner(player.getUniqueId()), "§bYou must be party owner");

        CommandAssert.assertTrue(p != country.getRuling(), "§bYou can`t start a coup against yourself");

        Decision d = new DecisionCoup("§6Stage a coup for party §9"+p.name, Math.max(0.9- country.getRuling().PCT, 0.05),
                CountryManager.getCountry(player).getName(), p.name);

        country.addDecision(d);
    }
}