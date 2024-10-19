package com.storage;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.google.common.reflect.TypeToken;
import com.klanting.signclick.economy.Account;
import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.Utils;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.TestTools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GsonTests {

    private ServerMock server;
    private SignClick plugin;

    private Player testPlayer;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();

        plugin = TestTools.setupPlugin(server);

        testPlayer = server.addPlayer();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void saveLoadUUID(){
        Utils.writeSave("uuid", testPlayer.getUniqueId());
        System.out.print(UUID.class);

        assertEquals(testPlayer.getUniqueId(), Utils.readSave("uuid", UUID.class, new UUID(1, 1)));
    }

    @Test
    void saveLoadAccount(){

        Map<UUID, Account> accountsPreSave = new HashMap<>();
        accountsPreSave.put(testPlayer.getUniqueId(), new Account(testPlayer.getUniqueId()));

        Utils.writeSave("accounts", accountsPreSave);

        Map<UUID, Account> accounts = Utils.readSave("accounts", new TypeToken<HashMap<UUID, Account>>(){}.getType(), new HashMap<>());
        assertEquals(1, accounts.size());

        //assertEquals(testPlayer.getUniqueId(), accounts.keySet().iterator().next());

        assertEquals(testPlayer.getUniqueId(), accounts.values().iterator().next().getUuid());
    }
}
