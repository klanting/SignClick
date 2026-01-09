package com.klanting.signclick.interactionLayer.commands.countryHandlers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.interactionLayer.commands.CommandTools;
import com.klanting.signclick.interactionLayer.commands.exceptions.CommandException;
import com.klanting.signclick.interactionLayer.events.MachineEvent;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class CountryHandlerBaltop extends CountryHandler{
    @Override
    public void handleCommand(Player player, String[] args) throws CommandException {

        int page = 1;
        if (args.length >= 2){
            page = CommandTools.parseInteger(args[1], "Please enter a valid integer as amount");
        }

        String line = SignClick.getPrefix()+"Baltop: page "+page+"/"+(int) Math.ceil(CountryManager.getTop().size()/10.0) +" §0";

        int index = page-1;

        int i = 0;
        for (Country country : CountryManager.getTop()){
            if (i >= index*10 && i < (index*10)+10){
                int amount = country.getBalance();
                DecimalFormat df = new DecimalFormat("###,###,###");
                line += "\n";
                line += SignClick.getPrefix()+(i+1)+".§3 ";
                line += country.getName();
                line += ": §7";
                line += df.format(amount);

                i += 1;
            }

        }
        player.sendMessage(line);
    }
}
