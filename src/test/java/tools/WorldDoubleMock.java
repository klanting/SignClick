package tools;

import be.seeseemelk.mockbukkit.WorldMock;

public class WorldDoubleMock extends WorldMock {
    @Override public boolean isChunkLoaded(int x, int y){
        return true;
    }
}
