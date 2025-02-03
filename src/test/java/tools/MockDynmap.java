package tools;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;

import java.util.HashMap;

public class MockDynmap implements DynmapAPI {

    private HashMap<OfflinePlayer, Boolean> visible = new HashMap<>();

    @Override
    public int triggerRenderOfVolume(Location location, Location location1) {
        return 0;
    }

    @Override
    public void setPlayerVisiblity(Player player, boolean b) {
        visible.put(player, b);
    }

    @Override
    public boolean getPlayerVisbility(Player player) {
        return visible.getOrDefault(player, true);
    }

    @Override
    public void postPlayerMessageToWeb(Player player, String s) {

    }

    @Override
    public void postPlayerJoinQuitToWeb(Player player, boolean b) {

    }

    @Override
    public String getDynmapVersion() {
        return null;
    }

    @Override
    public void assertPlayerInvisibility(Player player, boolean b, Plugin plugin) {

    }

    @Override
    public void assertPlayerVisibility(Player player, boolean b, Plugin plugin) {

    }

    @Override
    public MarkerAPI getMarkerAPI() {
        return null;
    }

    @Override
    public boolean markerAPIInitialized() {
        return false;
    }

    @Override
    public boolean sendBroadcastToWeb(String s, String s1) {
        return false;
    }

    @Override
    public int triggerRenderOfVolume(String s, int i, int i1, int i2, int i3, int i4, int i5) {
        return 0;
    }

    @Override
    public int triggerRenderOfBlock(String s, int i, int i1, int i2) {
        return 0;
    }

    @Override
    public void setPauseFullRadiusRenders(boolean b) {

    }

    @Override
    public boolean getPauseFullRadiusRenders() {
        return false;
    }

    @Override
    public void setPauseUpdateRenders(boolean b) {

    }

    @Override
    public boolean getPauseUpdateRenders() {
        return false;
    }

    @Override
    public void setPlayerVisiblity(String s, boolean b) {

    }

    @Override
    public boolean getPlayerVisbility(String s) {
        return false;
    }

    @Override
    public void assertPlayerInvisibility(String s, boolean b, String s1) {

    }

    @Override
    public void assertPlayerVisibility(String s, boolean b, String s1) {

    }

    @Override
    public void postPlayerMessageToWeb(String s, String s1, String s2) {

    }

    @Override
    public void postPlayerJoinQuitToWeb(String s, String s1, boolean b) {

    }

    @Override
    public String getDynmapCoreVersion() {
        return null;
    }

    @Override
    public boolean setDisableChatToWebProcessing(boolean b) {
        return false;
    }

    @Override
    public boolean testIfPlayerVisibleToPlayer(String s, String s1) {
        return false;
    }

    @Override
    public boolean testIfPlayerInfoProtected() {
        return false;
    }

    @Override
    public void processSignChange(String s, String s1, int i, int i1, int i2, String[] strings, String s2) {

    }
}
