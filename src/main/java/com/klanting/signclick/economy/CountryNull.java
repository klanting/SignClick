package com.klanting.signclick.economy;

import org.bukkit.OfflinePlayer;

public class CountryNull extends Country{

    public CountryNull() {
        super();
    }

    @Override
    public double getPolicyBonus(int id, int index){

        return 0.0;
    }
}
