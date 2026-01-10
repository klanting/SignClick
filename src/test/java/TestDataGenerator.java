import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.countryLogic.CountryManager;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import tools.ExpandedServerMock;
import tools.TestTools;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestDataGenerator {
    public static void main(String[] args) throws IOException {
        /*
        * Script to generate config files for the current version
        * */
        ServerMock server = MockBukkit.mock(new ExpandedServerMock());;
        SignClick plugin = TestTools.setupPlugin(server);

        /*
        * Create a company
        * */
        PlayerMock testPlayer = TestTools.addPermsPlayer(server, plugin);

        SignClick.getEconomy().depositPlayer(testPlayer, 40000000);
        Market.addCompany("TestCaseInc", "TCI", Market.getAccount(testPlayer));
        SignClick.getEconomy().withdrawPlayer(testPlayer, 40000000);

        /*
         * Create a country
         * */
        CountryManager.create("empire1", testPlayer);

        /*
        * Server to Company Contract
        * */

        CompanyI comp = Market.getCompany("TCI");
        comp.addBal(1000000000.0);
        comp.doUpgrade(0);

        /*
        * Reboot server
        * */
        TestTools.reboot(server);

        /*
        * Save configuration
        * */
        File virtualDataFolder = plugin.getDataFolder();

        File realOutputFolder = new File("src/test/resources", "v206");

        Files.walk(virtualDataFolder.toPath())
                .forEach(source -> {

                    Path relativePath = virtualDataFolder.toPath().relativize(source);
                    Path destination = realOutputFolder.toPath().resolve(relativePath);


                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        /*
        * Unmock bukkit
        * */
        MockBukkit.unmock();
        Market.clear();
    }
}