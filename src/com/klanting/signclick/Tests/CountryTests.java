package com.klanting.signclick.Tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.SignClick;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


class CountryTests {


    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SignClick.class);
    }

    @AfterEach
    public void tearDown()
    {

    }

    @Test
    void CountryCreate(){


        assertTrue(true);
    }
}

