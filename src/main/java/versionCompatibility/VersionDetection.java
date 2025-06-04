package versionCompatibility;

import com.klanting.signclick.SignClick;

public class VersionDetection {
    /*
    * Singleton design pattern to handle different minecraft versions
    *
    * */

    private static VersionDetection instance = null;

    private final String version;

    private VersionDetection(){
        version = SignClick.getPlugin().getDescription().getVersion();
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
