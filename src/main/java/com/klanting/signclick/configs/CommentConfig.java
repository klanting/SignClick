package com.klanting.signclick.configs;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class CommentConfig extends YamlConfiguration {

    private final HashMap<String, String> commentMap = new HashMap<>();

    public void set(@NotNull String path, Object value, String comment){
        commentMap.put(path, comment);
        set(path, value);

    }

    public ConfigurationSection createSection(@NotNull String path, String comment){
        commentMap.put(path, comment);
        return createSection(path);


    }

    public static @NotNull CommentConfig loadConfiguration(@NotNull File file) {
        CommentConfig config = new CommentConfig();

        try {
            config.load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        }

        return config;
    }

    @Override
    public void save(@NotNull File file) throws IOException {
        super.save(file);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> content = reader.lines().toList();

        reader.close();

        List<String> finalContent = new ArrayList<>();

        String lastLine = "";
        for (String line: content){

            boolean dataLine = line.contains(":") && commentMap.containsKey(line.substring(0, line.indexOf(":")));
            if (dataLine && ! lastLine.startsWith("#")){
                finalContent.add("#"+commentMap.get(line.substring(0, line.indexOf(":"))));
            }

            finalContent.add(line);
            lastLine = line;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        for (String line: finalContent){
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }


}
