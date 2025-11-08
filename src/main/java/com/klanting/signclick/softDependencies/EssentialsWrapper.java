package com.klanting.signclick.softDependencies;

import com.earth2me.essentials.Worth;
import net.ess3.api.IEssentials;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class EssentialsWrapper {
    /*
    * This class is a wrapper around Essentials, this ensures when essentials is not present, the plugin will not crash
    * */
    private final IEssentials essentials;

    public EssentialsWrapper(Server server){
        this.essentials = (IEssentials) server.getPluginManager().getPlugin("Essentials");
    }

    public BigDecimal getPrice(ItemStack itemStack){
        return this.essentials.getWorth().getPrice(this.essentials, itemStack);
    }
}
