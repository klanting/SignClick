package com.klanting.signclick.utils;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {
    public static List<UUID> toUUIDList(List<String> stringList){
        List<UUID> UUIDList = new ArrayList<>();
        for (String s: stringList){
            UUIDList.add(UUID.fromString(s));
        }
        return UUIDList;
    }
}
