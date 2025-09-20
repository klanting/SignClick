
package versionCompatibility;

import java.util.Objects;
import static org.bukkit.Bukkit.getServer;


public class CompatibleLayer {
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


    public static long getCurrentTick(){
        /*Safely gets the current server tick count, supporting both modern and legacy APIs.
        * It first tries the `getServer().getCurrentTick()` method. If that fails (on older
        * server versions), it falls back to `world.getFullTime()`.
        * */
        try{
            return getServer().getCurrentTick();
        }catch (NoSuchMethodError e){

        //Fallback for older server versions that lack getCurrentTick()
            return Objects.requireNonNull(getServer().getWorld("world")).getFullTime();
        }
    }
}