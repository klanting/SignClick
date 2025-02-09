package com.compatibility;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ExpandedServerMock;
import tools.TestTools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class compatibilityTest100 {
    /*
    * Test compatibility with v1.0.0
    * */
    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock(new ExpandedServerMock());

        plugin = TestTools.setupPlugin(server);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        CountryManager.clear();
        Market.clear();
    }

    @Test
    void testLoadFiles(){
        TestTools.disable(server);

        Path targetDir = Paths.get(plugin.getDataFolder().getAbsolutePath());


        Path mockDataFolder = Paths.get("src","test", "resources", "v100");

        try {
            List<Path> files = Files.list(Path.of(mockDataFolder.toFile().getAbsolutePath())).toList();

            for (Path file: files){
                Files.copy(file, targetDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }


        }catch (IOException e){
            assertFalse(true);
        }

        TestTools.setupPlugin(server);
    }

}
