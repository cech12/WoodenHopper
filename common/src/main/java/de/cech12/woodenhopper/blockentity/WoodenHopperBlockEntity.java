package de.cech12.woodenhopper.blockentity;

import de.cech12.woodenhopper.Constants;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import de.cech12.woodenhopper.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class WoodenHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {

    protected int transferCooldown = -1;
    protected long tickedGameTime;

    public WoodenHopperBlockEntity(BlockPos pos, BlockState state) {
        super(Constants.WOODEN_HOPPER_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    @NotNull
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
    public boolean isGridAligned() {
        return true;
    }

    @Override
    @NotNull
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new WoodenHopperContainer(id, player, this);
    }

    public void setTransferCooldown(int ticks) {
        this.transferCooldown = ticks;
    }

    protected boolean isNotOnTransferCooldown() {
        return this.transferCooldown <= 0;
    }

    public boolean mayNotTransfer() {
        return this.transferCooldown <= Services.CONFIG.getCooldown();
    }

    protected long getLastUpdateTime() {
        return this.tickedGameTime;
    }

    public static List<Entity> getAllAliveEntitiesAt(Level level, double x, double y, double z, Predicate<? super Entity> filter) {
        return level.getEntities((Entity)null, new AABB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D),
                entity -> entity.isAlive() && filter.test(entity));
    }

    public static void tick(Level level, WoodenHopperBlockEntity entity) {
        if (level != null && !level.isClientSide) {
            entity.transferCooldown--;
            entity.tickedGameTime = level.getGameTime();
            if (entity.isNotOnTransferCooldown()) {
                entity.setTransferCooldown(0);
                if (entity instanceof WoodenHopperBlockEntity blockEntity) {
                    blockEntity.updateHopper(blockEntity::pullItems);
                }
            }
        }
    }

    public void onItemEntityIsCaptured(ItemEntity itemEntity) {
        this.updateHopper(() -> captureItem(itemEntity));
    }

    protected abstract ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, Object destInventory, ItemStack stack);

    protected abstract Optional<Pair<Object, Object>> getItemHandler(Level level, double x, double y, double z, final Direction side);

    protected abstract boolean isNotFull(Object itemHandler);

    protected abstract boolean pullItemsFromItemHandler(Object itemHandler, Object destination);

    protected abstract Object getOwnItemHandler();

    /**
     * Pull dropped EntityItems from the world above the hopper and items
     * from any inventory attached to this hopper into the hopper's inventory.
     *
     * @return whether any items were successfully added to the hopper
     */
    protected boolean pullItems() {
        return getItemHandler(this, Direction.UP)
                .map(itemHandlerResult -> {
                    //get item from item handler
                    if (Services.CONFIG.isPullItemsFromInventoriesEnabled()) {
                        return pullItemsFromItemHandler(itemHandlerResult.getKey(), itemHandlerResult.getValue());
                    }
                    return false;
                }).orElseGet(() -> {
                    //capture item
                    if (Services.CONFIG.isPullItemsFromWorldEnabled()) {
                        BlockPos pos = BlockPos.containing(this.getLevelX(), this.getLevelY() + 1D, this.getLevelZ());
                        BlockState aboveBlockState = this.level.getBlockState(pos);
                        if (aboveBlockState.is(BlockTags.DOES_NOT_BLOCK_HOPPERS) || !aboveBlockState.isCollisionShapeFullBlock(this.level, pos)) {
                            for (ItemEntity itementity : HopperBlockEntity.getItemsAtAndAbove(this.level, this)) {
                                if (this.captureItem(itementity)) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                });
    }

    protected void updateCooldown(boolean inventoryWasEmpty, BlockEntity source, Object destination) {
        if (inventoryWasEmpty && destination instanceof WoodenHopperBlockEntity destinationHopper && destinationHopper.mayNotTransfer()) {
            int k = 0;
            if (source instanceof WoodenHopperBlockEntity && destinationHopper.getLastUpdateTime() >= ((WoodenHopperBlockEntity) source).getLastUpdateTime()) {
                k = 1;
            }
            destinationHopper.setTransferCooldown(Services.CONFIG.getCooldown() - k);
        }
    }

    protected void updateHopper(Supplier<Boolean> p_200109_1_) {
        if (this.level != null && !this.level.isClientSide) {
            if (this.isNotOnTransferCooldown() && this.getBlockState().getValue(HopperBlock.ENABLED)) {
                boolean flag = false;
                if (!this.isEmpty()) {
                    flag = this.transferItemsOut();
                }
                if (isNotFull(this.getOwnItemHandler())) {
                    flag |= p_200109_1_.get();
                }
                if (flag) {
                    this.setTransferCooldown(Services.CONFIG.getCooldown());
                    this.setChanged();
                }
            }
        }
    }

    private Optional<Pair<Object, Object>> getItemHandler(WoodenHopperBlockEntity hopper, Direction hopperFacing) {
        double x = hopper.getLevelX() + (double) hopperFacing.getStepX();
        double y = hopper.getLevelY() + (double) hopperFacing.getStepY();
        double z = hopper.getLevelZ() + (double) hopperFacing.getStepZ();
        return getItemHandler(hopper.getLevel(), x, y, z, hopperFacing.getOpposite());
    }

    private boolean transferItemsOut() {
        Direction hopperFacing = this.getBlockState().getValue(HopperBlock.FACING);
        return getItemHandler(this, hopperFacing)
                .map(destinationResult -> {
                    Object itemHandler = destinationResult.getKey();
                    Object destination = destinationResult.getValue();
                    if (isNotFull(itemHandler)) {
                        for (int i = 0; i < this.getContainerSize(); ++i) {
                            if (!this.getItem(i).isEmpty()) {
                                ItemStack originalSlotContents = this.getItem(i).copy();
                                ItemStack insertStack = this.removeItem(i, 1);
                                ItemStack remainder = putStackInInventoryAllSlots(this, destination, itemHandler, insertStack);
                                if (remainder.isEmpty()) {
                                    return true;
                                }
                                this.setItem(i, originalSlotContents);
                            }
                        }

                    }
                    return false;
                })
                .orElse(false);
    }

    private boolean captureItem(ItemEntity itemEntity) {
        boolean flag = false;
        ItemStack itemstack = itemEntity.getItem().copy();
        ItemStack itemstack1 = putStackInInventoryAllSlots(null, this, getOwnItemHandler(), itemstack);
        if (itemstack1.isEmpty()) {
            flag = true;
            itemEntity.remove(Entity.RemovalReason.DISCARDED);
        } else {
            itemEntity.setItem(itemstack1);
        }
        return flag;
    }

}
