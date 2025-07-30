package com.compatibility;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.economy.CountryManager;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.companyUpgrades.UpgradeBoardSize;
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

public class compatibilityTestBasic {
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

    void performTest(String version, String compName){
        TestTools.disable(server);

        Path targetDir = Paths.get(plugin.getDataFolder().getAbsolutePath());


        Path mockDataFolder = Paths.get("src","test", "resources", version);

        try {
            List<Path> files = Files.list(Path.of(mockDataFolder.toFile().getAbsolutePath())).toList();

            for (Path file: Files.list(targetDir.resolve("configs")).toList()){
                Files.delete(file);
            }

            for (Path file: files){
                Files.copy(file, targetDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }


        }catch (IOException e){
            assertFalse(true);
        }

        TestTools.setupPlugin(server);

        assertNotNull(Market.getCompany(compName));

        /*
         * Check that the observers that were added in v1.0.1 are now added
         * */
        assertEquals(4, Market.getCompany(compName).getLogObservers().size());
        assertNotNull(Market.getCompany(compName).getResearch());
    }

    @Test
    void testLoadFilesV100(){
        performTest("v100", "AA");
    }

    @Test
    void testLoadFilesV101(){
        performTest("v101", "TCI");
    }

    @Test
    void testLoadFilesV200Beta(){
        performTest("v200-beta", "TCI");
        assertEquals(Market.getCompany("TCI"),
                ((UpgradeBoardSize) Market.getCompany("TCI").getUpgrades().get(3)).comp);
    }

}
