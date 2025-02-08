package com.klanting.signclick.utils;

import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class BookParser {
    public static List<String> getPages(String path, Player player) {
        InputStream in = SignClick.getPlugin().getResource(path);

        assert in != null;

        String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        result = result.replace("{player}", player.getName());

        return List.of(result.split("---\n"));
    }
}
