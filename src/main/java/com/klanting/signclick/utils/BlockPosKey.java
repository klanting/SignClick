package com.klanting.signclick.utils;

import org.bukkit.Location;

public record BlockPosKey(String world, int x, int y, int z) {
    public static BlockPosKey from(Location loc) {
        return new BlockPosKey(
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );
    }

    public static BlockPosKey from(String worldName, int X, int Y, int Z) {
        return new BlockPosKey(
                worldName,
                X,
                Y,
                Z
        );
    }
}
