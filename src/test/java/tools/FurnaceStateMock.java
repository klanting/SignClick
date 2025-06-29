package tools;

import be.seeseemelk.mockbukkit.block.state.BlockStateMock;
import be.seeseemelk.mockbukkit.block.state.TileStateMock;
import be.seeseemelk.mockbukkit.persistence.PersistentDataContainerMock;
import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.block.TileState;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FurnaceStateMock extends BlockStateMock implements Furnace, TileState {
    private short burnTime = 0;
    private short cookTime = 0;
    private int cookTimeTotal = 200;

    private final PersistentDataContainer container;

    FurnaceInventory inv = (FurnaceInventory) Bukkit.createInventory(this, InventoryType.BLAST_FURNACE);

    public FurnaceStateMock(Block block) {

        super(block);
        this.container = new PersistentDataContainerMock();
    }

    @Override public short getBurnTime() { return burnTime; }
    @Override public void setBurnTime(short burnTime) { this.burnTime = burnTime; }

    @Override public short getCookTime() { return cookTime; }
    @Override public void setCookTime(short cookTime) { this.cookTime = cookTime; }

    @Override public int getCookTimeTotal() { return cookTimeTotal; }

    @Override public FurnaceInventory getInventory() { return inv; }

    @Override
    public @NotNull FurnaceInventory getSnapshotInventory() {
        return null;
    }

    @Override public void setCookTimeTotal(int cookTimeTotal) { this.cookTimeTotal = cookTimeTotal; }

    @Override
    public @NotNull Map<CookingRecipe<?>, Integer> getRecipesUsed() {
        return null;
    }

    @Override
    public double getCookSpeedMultiplier() {
        return 1;
    }

    @Override
    public void setCookSpeedMultiplier(double v) {

    }

    @Override
    public int getRecipeUsedCount(@NotNull NamespacedKey namespacedKey) {
        return 0;
    }

    @Override
    public boolean hasRecipeUsedCount(@NotNull NamespacedKey namespacedKey) {
        return false;
    }

    @Override
    public void setRecipeUsedCount(@NotNull CookingRecipe<?> cookingRecipe, int i) {

    }

    @Override
    public void setRecipesUsed(@NotNull Map<CookingRecipe<?>, Integer> map) {

    }

    @Override
    public @Nullable Component customName() {
        return null;
    }

    @Override
    public void customName(@Nullable Component component) {

    }

    @Override
    public @Nullable String getCustomName() {
        return null;
    }

    @Override
    public void setCustomName(@Nullable String s) {

    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public @NotNull String getLock() {
        return null;
    }

    @Override
    public void setLock(@Nullable String s) {

    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return container;
    }

    @Override
    public boolean isSnapshot() {
        return false;
    }

    @Override
    public @NotNull BlockState getSnapshot() {
        return null;
    }
}