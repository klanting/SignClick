package com.klanting.signclick.utils;

import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;

public class Prefix {
    public static void sendMessage(Player player, String text){
        player.sendMessage(SignClick.getPrefix()+text);
    }
}
