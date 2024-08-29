package com.klanting.signclick.Tests;

import io.github.jwdeveloper.spigot.tester.api.PluginTest;
import io.github.jwdeveloper.spigot.tester.api.annotations.Test;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class CountryTests extends PluginTest {


    @Test(name = "create country (suc6)")
    public void createCountrySuc6(){

        /*give player SignClick staff perms*/
        Player player = addPlayer("klanting");
        PermissionAttachment attachment = player.addAttachment(getPlugin());

        attachment.setPermission("signclick.staff", true);

        assertThatPlayer(player)
                .hasName("mike")
                .hasPermission("crating");
    }

    @Test(name = "create country (suc6) Command")
    public void createCountrySuc6Command(){

        /*give player SignClick staff perms*/
        Player player = addPlayer("klanting");

        PermissionAttachment attachment = player.addAttachment(getPlugin());
        attachment.setPermission("signclick.staff", true);

        invokeCommand(player, "country create A");



        assertThatCommand("country").withFail().validate();
    }
}
