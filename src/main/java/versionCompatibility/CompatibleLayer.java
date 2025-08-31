package versionCompatibility;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class CompatibleLayer {
    public static long getCurrentTick(){
        try{
            return getServer().getCurrentTick();
        }catch (NoSuchMethodError e){
            return Objects.requireNonNull(getServer().getWorld("world")).getFullTime();
        }
    }
}
