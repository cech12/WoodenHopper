package de.cech12.woodenhopper.blockentity;

import de.cech12.woodenhopper.Constants;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import de.cech12.woodenhopper.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public abstract class WoodenHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {

    protected int transferCooldown = -1;
    protected long tickedGameTime;

    public WoodenHopperBlockEntity(BlockPos pos, BlockState state) {
        super(Constants.WOODEN_HOPPER_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    @Nonnull
    protected Component getDefaultName() {
        return Component.translatable("block.woodenhopper.wooden_hopper");
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    @Override
    public double getLevelX() {
        return (double)this.worldPosition.getX() + 0.5D;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    @Override
    public double getLevelY() {
        return (double)this.worldPosition.getY() + 0.5D;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    @Override
    public double getLevelZ() {
        return (double)this.worldPosition.getZ() + 0.5D;
    }

    @Override
    @Nonnull
    protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory player) {
        return new WoodenHopperContainer(id, player, this);
    }

    public void setTransferCooldown(int ticks) {
        this.transferCooldown = ticks;
    }

    protected boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public boolean mayTransfer() {
        return this.transferCooldown > Services.CONFIG.getCooldown();
    }

    protected long getLastUpdateTime() {
        return this.tickedGameTime;
    }

    public abstract void onEntityCollision(Entity entity);

}
