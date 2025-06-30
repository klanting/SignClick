package tools;

import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.block.state.BlockStateMock;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class DoubleBlockMock extends BlockMock {

    public DoubleBlockMock(Material material, Location location){
        super(material, location);
    }


    private BlockStateMock state2;
    @Override public void setState(@NotNull BlockStateMock state) {
        Preconditions.checkNotNull(state, "The BlockState cannot be null");
        this.state2 = state;
    }

    @Override public @NotNull BlockState getState() {
        return this.state2!=null ? this.state2: null;
    }
}
