package com.klanting.signclick.utils.statefulSQLSerializers;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.utils.BlockPosKey;
import com.klanting.signclick.utils.statefulSQL.SQLSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer extends SQLSerializer<Location> {
    public LocationSerializer(Class<Location> type) {
        super(type);
    }

    @Override
    public String serialize(Location value) {
        return value.x()+";"+ value.y()+";"+value.z()+";"+value.getWorld().getName();
    }

    @Override
    public Location deserialize(String value) {
        String[] s = value.split(";");
        return new Location(
                SignClick.getPlugin().getServer().getWorld(s[3]),
                Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])
        );
    }
}
