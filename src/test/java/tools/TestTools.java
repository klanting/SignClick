package tools;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.plugin.PluginManagerMock;
import com.klanting.signclick.SignClick;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestTools {
    public static SignClick setupPlugin(ServerMock server){
        PluginManagerMock pluginManager = server.getPluginManager();

        //dynmap mock
        Plugin dynmap = MockBukkit.createMockPlugin("dynmap");
        pluginManager.enablePlugin(dynmap);

        // Mock the Vault plugin
        Plugin vault = MockBukkit.createMockPlugin("Vault");
        pluginManager.enablePlugin(vault);
        MockEconomy mockEconomy = new MockEconomy();
        MockDynmap mockDynmap = new MockDynmap();

        // add Mock Vault to server
        server.getServicesManager().register(Economy.class, mockEconomy, vault, org.bukkit.plugin.ServicePriority.Highest);
        server.getServicesManager().register(DynmapAPI.class, mockDynmap, vault, org.bukkit.plugin.ServicePriority.Highest);

        SignClick plugin = MockBukkit.load(SignClick.class);

        assertNotNull(SignClick.getEconomy());
        assertNotNull(plugin.getServer().getPluginManager().getPlugin("Vault"));

        return plugin;
    }

    public static PlayerMock addPermsPlayer(ServerMock server, SignClick plugin){
        PlayerMock testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);
        return testPlayer;
    }
}
