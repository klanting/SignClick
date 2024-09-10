package com.country;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.milkbowl.vault.economy.Economy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CompanyTests {


    private ServerMock server;
    private SignClick plugin;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock();

        plugin = MockBukkit.load(SignClick.class);
    }

    @AfterEach
    public void tearDown() {

        MockBukkit.unmock();
        Banking.clear();
    }

    @Test
    void companyCreate(){
        Player testPlayer = server.addPlayer();
    }
}