package tools;

import be.seeseemelk.mockbukkit.WorldMock;
import org.jetbrains.annotations.NotNull;

public class WorldDoubleMock extends WorldMock {

    @Override public @NotNull String getName(){
        return "world";
    }

    @Override public boolean isChunkLoaded(int x, int y){
        return true;
    }
}
