package tools;

import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WorldDoubleMock extends WorldMock {

    @Override public @NotNull String getName(){
        return "world";
    }

    @Override public boolean isChunkLoaded(int x, int y){
        return true;
    }

    private Map<Location, BlockMock> mapping = new HashMap<>();

    public void setBlock(BlockMock block, Location location){
        mapping.put(location, block);
    }

    @Override
    public @NotNull BlockMock getBlockAt(@NotNull Location location) {
        System.out.println("W");
        return mapping.get(location);
    }
}
