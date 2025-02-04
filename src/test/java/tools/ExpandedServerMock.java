package tools;

import be.seeseemelk.mockbukkit.ServerMock;

public class ExpandedServerMock extends ServerMock {
    // Override methods to add custom behavior

    @Override
    public int getCurrentTick() {
        return (int) this.getScheduler().getCurrentTick();
    }


}
