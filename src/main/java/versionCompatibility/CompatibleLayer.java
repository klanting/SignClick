/*
#------------------------------------------------------------------------------------#
#                                 CompatibleLayer                                    #
#------------------------------------------------------------------------------------#
#                                                                                    #
# WARNING DETECTS VERSION OF MINECRAFT NOT THE PLUG IN                               #
# Description:                                                                       #
# This class provides a compatibility layer for methods that may differ across       #
# various Minecraft server versions. It ensures that the plugin can call             #
# version-specific methods without breaking on older or newer versions by providing  #
# a fallback mechanism.                                                              #
#------------------------------------------------------------------------------------#
*/

// Main package for the compatibility layer.
package versionCompatibility;

// Imports for various functionalities used in the compatibility layer.
import java.util.Objects;
import static org.bukkit.Bukkit.getServer;

//Safely gets the current server tick count, supporting both modern and legacy APIs.
//It first tries the `getServer().getCurrentTick()` method. If that fails (on older
//server versions), it falls back to `world.getFullTime()`.
public class CompatibleLayer {

//The current server tick as a long.
    public static long getCurrentTick(){
        try{
            return getServer().getCurrentTick();
        }catch (NoSuchMethodError e){

//Fallback for older server versions that lack getCurrentTick() 
            return Objects.requireNonNull(getServer().getWorld("world")).getFullTime();
        }
    }
}

//#--------------------------------------END---------------------------------------#//