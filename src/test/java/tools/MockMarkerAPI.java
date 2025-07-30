package tools;

import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PlayerSet;

import java.io.InputStream;
import java.util.Set;

public class MockMarkerAPI implements MarkerAPI {
    @Override
    public Set<MarkerSet> getMarkerSets() {
        return null;
    }

    @Override
    public MarkerSet getMarkerSet(String s) {
        return null;
    }

    @Override
    public MarkerSet createMarkerSet(String s, String s1, Set<MarkerIcon> set, boolean b) {
        return null;
    }

    @Override
    public Set<MarkerIcon> getMarkerIcons() {
        return null;
    }

    @Override
    public MarkerIcon getMarkerIcon(String s) {
        return null;
    }

    @Override
    public MarkerIcon createMarkerIcon(String s, String s1, InputStream inputStream) {
        return null;
    }

    @Override
    public Set<PlayerSet> getPlayerSets() {
        return null;
    }

    @Override
    public PlayerSet getPlayerSet(String s) {
        return null;
    }

    @Override
    public PlayerSet createPlayerSet(String s, boolean b, Set<String> set, boolean b1) {
        return null;
    }
}
