package tools;

import be.seeseemelk.mockbukkit.block.data.BlockDataMock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class DoorMock extends BlockDataMock implements Door {
    private boolean open;
    private boolean powered;
    private Half half = Half.BOTTOM;

    private Hinge hinge = Hinge.LEFT;
    private BlockFace facing = BlockFace.NORTH;

    public DoorMock(@NotNull Material type) {
        super(type); // Adjust this to your required door type
        this.open = false;
        this.powered = false;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public Half getHalf() {
        return half;
    }

    @Override
    public void setHalf(Half half) {
        this.half = Half.TOP;
    }

    @Override
    public Hinge getHinge() {
        return hinge;
    }

    @Override
    public void setHinge(Hinge hinge) {
        this.hinge = hinge;
    }

    @Override
    public BlockFace getFacing() {
        return facing;
    }

    @Override
    public void setFacing(BlockFace facing) {
        this.facing = facing;
    }

    @Override
    public @NotNull Set<BlockFace> getFaces() {
        return EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    }

    @Override
    public BlockData clone() {
        DoorMock clone = new DoorMock(super.getMaterial());
        clone.setOpen(this.open);
        clone.setPowered(this.powered);
        clone.setHalf(this.half);
        clone.setHinge(this.hinge);
        clone.setFacing(this.facing);
        return clone;
    }

    @Override
    public String toString() {
        return "DoorMock{" +
                "open=" + open +
                ", powered=" + powered +
                ", half=" + half +
                ", hinge=" + hinge +
                ", facing=" + facing +
                '}';
    }
}