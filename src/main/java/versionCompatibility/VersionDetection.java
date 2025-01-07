package versionCompatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class VersionDetection {
    /*
    * Singleton design pattern to handle different minecraft versions
    *
    * */

    private static VersionDetection instance = null;

    private String version = null;

    private VersionDetection(){

        String name = Bukkit.getServer().getClass().getPackage().getName();

        if (name.equals("be.seeseemelk.mockbukkit")){
            version = "v1_18_R2";
            return;
        }

        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    }

    public static VersionDetection getInstance(){

        if (instance == null){
            instance = new VersionDetection();
        }

        return instance;
    }

    public String getVersion() {
        return version;
    }
}
