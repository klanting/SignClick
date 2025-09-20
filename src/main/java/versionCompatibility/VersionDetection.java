package versionCompatibility;
import com.klanting.signclick.SignClick;

public class VersionDetection {
    /*
    #------------------------------------------------------------------------------------#
    #                                 VersionDetection                                   #
    #------------------------------------------------------------------------------------#
    #                                                                                    #
    # WARNING DETECTS VERSION OF THE PLUG IN NOT MINECRAFT                               #
    # This class provides a centralized way to access the plugin's version string.       #
    # It follows the singleton design pattern to ensure that only one instance of this   #
    # class exists, which retrieves and stores the version from the plugin.yml file.     #
    #                                                                                    #
    # Singleton class to detect and provide the plugin's version.                        #
    #------------------------------------------------------------------------------------#
    */
    private static VersionDetection instance = null;
    private final String version;

    //Private constructor to prevent direct instantiation.
    //It initializes the version string by fetching it from the main plugin class.

    private VersionDetection(){
        version = SignClick.getPlugin().getDescription().getVersion();
    }

    //Returns the singleton instance of the VersionDetection class.
    public static VersionDetection getInstance(){

        if (instance == null){
            instance = new VersionDetection();
        }
        return instance;
    }

    //Returns the plugin's version string.
    public String getVersion() {
        return version;
    }
}
