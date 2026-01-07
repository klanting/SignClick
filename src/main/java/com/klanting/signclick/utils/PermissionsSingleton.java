package com.klanting.signclick.utils;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.patent.Auction;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionsSingleton {
    private static PermissionsSingleton instance = null;
    private final Map<String, String> stringChain = new HashMap<>();

    public static PermissionsSingleton getInstance(){
        if (instance == null){
            instance = new PermissionsSingleton();
        }

        return instance;
    }

    private PermissionsSingleton(){
        Plugin plugin = SignClick.getPlugin();
        PluginDescriptionFile pdf = plugin.getDescription();
        List<Permission> permissions = pdf.getPermissions();
        for (Permission permission: permissions){
            Map<String, Boolean> children = permission.getChildren();
            for(Map.Entry<String, Boolean> entry: children.entrySet()){
                if (!entry.getValue()){
                    continue;
                }

                stringChain.put(entry.getKey(), permission.getName());
            }
        }
    }

    public boolean hasPermission(Player player, String permissionName){
        String subPermissionName = permissionName;

        while (subPermissionName != null){
            if (player.hasPermission(subPermissionName)){
                return true;
            }

            subPermissionName = stringChain.getOrDefault(subPermissionName, null);
        }


        return false;
    }
}
