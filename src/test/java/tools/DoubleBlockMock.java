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

    public DoubleBlockMock(Material material){
        super(material);
    }

    private BlockStateMock state;
    @Override public void setState(@NotNull BlockStateMock state) {
        Preconditions.checkNotNull(state, "The BlockState cannot be null");
        this.state = state;
    }

    @Override public @NotNull BlockState getState() {
        return this.state!=null ? this.state: null;
    }
}
