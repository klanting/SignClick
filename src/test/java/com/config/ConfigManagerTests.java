package com.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.configs.CommentConfig;
import com.klanting.signclick.configs.ConfigManager;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigManagerTests {
    private ServerMock server;
    private SignClick plugin;

    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);

        testPlayer = TestTools.addPermsPlayer(server, plugin);

        boolean suc6 = Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        assertTrue(suc6);

    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
    }

    @Test
    void simpleSetComment() throws FileNotFoundException {
        /*
        * perform a simple set and see if the comments are saved
        * */

        ConfigManager cm = new ConfigManager(plugin);
        cm.createConfigFile("a.yml");
        CommentConfig cs =  cm.getConfig("a.yml");

        /*
        * should have e as comment
        * */
        cs.set("c", "d", "e");
        cm.save();

        File configFile = new File(plugin.getDataFolder()+"/configs", "a.yml");

        BufferedReader reader = new BufferedReader(new FileReader(configFile));

        String data = reader.lines().collect(Collectors.joining("\n"));
        assertEquals("#e\nc: d", data);

        /*
        * Check only 1 comment can exist
        * */
        cs.set("c", "d", "f");
        assertEquals("#e\nc: d", data);


    }
}
