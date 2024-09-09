package com.klanting.signclick.Calculate;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignCasino {
    public static void Play(Sign sign){
        String coins = sign.getLine(1);
        String cost = coins.substring(0,coins.indexOf("-"));
        String prize = coins.substring(coins.indexOf(">")+1);


    }
    public static void set(Sign sign, Player player){

    }
}
