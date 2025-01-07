package com.versionManagement;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.klanting.signclick.SignClick;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.TestTools;
import versionCompatibility.VersionDetection;


public class versionTests {
    private ServerMock server;
    private SignClick plugin;
    private PlayerMock testPlayer;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        /*Create country*/
        testPlayer = server.addPlayer();
        testPlayer.addAttachment(plugin, "signclick.staff", true);
    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void testVersionDetection(){
        VersionDetection vd = VersionDetection.getInstance();

        System.out.print(vd.getVersion());
    }

}
