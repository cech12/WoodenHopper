package de.cech12.woodenhopper.blockentity;

import de.cech12.woodenhopper.platform.Services;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FabricWoodenHopperBlockEntity extends WoodenHopperBlockEntity {

    InventoryStorage inventory = InventoryStorage.of(this, null);
    private NonNullList<ItemStack> items;

    public FabricWoodenHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = NonNullList.withSize(1, ItemStack.EMPTY);
    }

    @Override
    public void load(@Nonnull CompoundTag compoundTag) {
        super.load(compoundTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compoundTag)) {
            ContainerHelper.loadAllItems(compoundTag, this.items);
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.items);
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    @Nonnull
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@Nonnull NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    @Nonnull
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        ItemStack stack = ContainerHelper.removeItem(this.getItems(), index, count);
        this.setChanged();
        return stack;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int index) {
        this.unpackLootTable(null);
        ItemStack stack = ContainerHelper.takeItem(this.getItems(), index);
        this.setChanged();
        return stack;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setItem(int index, @Nonnull ItemStack itemStack) {
        this.unpackLootTable(null);
        this.getItems().set(index, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WoodenHopperBlockEntity entity) {
        if (level != null && !level.isClientSide) {
            entity.transferCooldown--;
            entity.tickedGameTime = level.getGameTime();
            if (!entity.isOnTransferCooldown()) {
                entity.setTransferCooldown(0);
                if (entity instanceof FabricWoodenHopperBlockEntity blockEntity) {
                    blockEntity.updateHopper(() -> pullItems(blockEntity));
                }
            }
        }
    }

    private void updateHopper(Supplier<Boolean> p_200109_1_) {
        if (this.level != null && !this.level.isClientSide) {
            if (!this.isOnTransferCooldown() && this.getBlockState().getValue(HopperBlock.ENABLED)) {
                boolean flag = false;
                if (!this.isEmpty()) {
                    flag = this.transferItemsOut();
                }
                if (isNotFull(this.inventory)) {
                    flag |= p_200109_1_.get();
                }
                if (flag) {
                    this.setTransferCooldown(Services.CONFIG.getCooldown());
                    this.setChanged();
                }
            }
        }
    }

    private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, Storage<ItemVariant> destInventory, ItemStack stack) {
        boolean inventoryWasEmpty = isEmpty(destInventory);
        try (Transaction transaction = Transaction.openOuter()) {
            long count = destInventory.insert(ItemVariant.of(stack), stack.getCount(), transaction);
            if (count > 0) {
                stack.shrink((int)count);
                if (inventoryWasEmpty && destination instanceof FabricWoodenHopperBlockEntity destinationHopper && !destinationHopper.mayTransfer()) {
                    int k = 0;
                    if (source instanceof FabricWoodenHopperBlockEntity && destinationHopper.getLastUpdateTime() >= ((FabricWoodenHopperBlockEntity) source).getLastUpdateTime()) {
                        k = 1;
                    }
                    destinationHopper.setTransferCooldown(Services.CONFIG.getCooldown() - k);
                }
                transaction.commit();
            } else {
                transaction.abort();
            }
        }
        return stack;
    }

    private static Optional<Pair<Storage<ItemVariant>, Object>> getItemHandler(FabricWoodenHopperBlockEntity hopper, Direction hopperFacing) {
        double x = hopper.getLevelX() + (double) hopperFacing.getStepX();
        double y = hopper.getLevelY() + (double) hopperFacing.getStepY();
        double z = hopper.getLevelZ() + (double) hopperFacing.getStepZ();
        return getItemHandler(hopper.getLevel(), x, y, z, hopperFacing.getOpposite());
    }

    private static Optional<Pair<Storage<ItemVariant>, Object>> getItemHandler(Level level, double x, double y, double z, final Direction side) {
        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState state = level.getBlockState(blockpos);
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(blockpos);
            if (blockEntity != null) {
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, blockEntity.getBlockPos(), side);
                if (storage != null) {
                    return Optional.of(ImmutablePair.of(storage, blockEntity));
                }
            }
        }
        //support vanilla inventory blocks without ItemStorage
        Block block = state.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            return Optional.of(ImmutablePair.of(InventoryStorage.of(((WorldlyContainerHolder)block).getContainer(state, level, blockpos), side), state));
        }
        //get entities with item handlers
        List<Entity> list = level.getEntities((Entity)null,
                new AABB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D),
                (entity) -> (entity instanceof ContainerEntity) && entity.isAlive()); //TODO modded entities with Item Storage?!
        if (!list.isEmpty()) {
            Entity entity = list.get(level.random.nextInt(list.size()));
            return Optional.of(ImmutablePair.of(InventoryStorage.of((ContainerEntity) entity, side), entity));
        }
        return Optional.empty();
    }

    private static boolean isNotFull(Storage<ItemVariant> itemHandler) {
        for (StorageView<ItemVariant> slot : itemHandler) {
            if (slot.isResourceBlank() || slot.getAmount() < slot.getCapacity()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmpty(Storage<ItemVariant> itemHandler) {
        for (StorageView<ItemVariant> slot : itemHandler.nonEmptyViews()) {
            if (!slot.isResourceBlank()) {
                return false;
            }
        }
        return true;
    }

    private boolean transferItemsOut() {
        Direction hopperFacing = this.getBlockState().getValue(HopperBlock.FACING);
        return getItemHandler(this, hopperFacing)
                .map(destinationResult -> {
                    Storage<ItemVariant> storage = destinationResult.getKey();
                    Object destination = destinationResult.getValue();
                    if (isNotFull(storage)) {
                        for (int i = 0; i < this.getContainerSize(); ++i) {
                            if (!this.getItem(i).isEmpty()) {
                                ItemStack originalSlotContents = this.getItem(i).copy();
                                ItemStack insertStack = this.removeItem(i, 1);
                                ItemStack remainder = putStackInInventoryAllSlots(this, destination, storage, insertStack);
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

    /**
     * Pull dropped EntityItems from the world above the hopper and items
     * from any inventory attached to this hopper into the hopper's inventory.
     *
     * @param hopper the hopper in question
     * @return whether any items were successfully added to the hopper
     */
    private static boolean pullItems(FabricWoodenHopperBlockEntity hopper) {
        return getItemHandler(hopper, Direction.UP)
                .map(itemHandlerResult -> {
                    //get item from item handler
                    if (Services.CONFIG.isPullItemsFromInventoriesEnabled()) {
                        Storage<ItemVariant> storage = itemHandlerResult.getKey();
                        try (Transaction transaction = Transaction.openOuter()) {
                            for (StorageView<ItemVariant> slot : storage.nonEmptyViews()) {
                                if (StorageUtil.simulateExtract(slot, slot.getResource(), 1, transaction) > 0) {
                                    ItemStack extractedItem = slot.getResource().toStack(1);
                                    for (int j = 0; j < hopper.getContainerSize(); j++) {
                                        ItemStack destStack = hopper.getItem(j);
                                        if (hopper.canPlaceItem(j, extractedItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize()
                                                && destStack.getCount() < hopper.getMaxStackSize() && canItemStacksStack(extractedItem, destStack))) {
                                            if (storage.extract(slot.getResource(), 1, transaction) > 0) {
                                                if (destStack.isEmpty()) {
                                                    hopper.setItem(j, extractedItem);
                                                } else {
                                                    destStack.grow(1);
                                                    hopper.setItem(j, destStack);
                                                }
                                                hopper.setChanged();
                                                transaction.commit();
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                            transaction.abort();
                        }
                    }
                    return false;
                }).orElseGet(() -> {
                    //capture item
                    if (Services.CONFIG.isPullItemsFromWorldEnabled()) {
                        for (ItemEntity itementity : getCaptureItems(hopper)) {
                            if (captureItem(hopper, itementity)) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
    }

    private static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b) {
        if (!a.isEmpty() && ItemStack.isSameItem(a, b) && a.hasTag() == b.hasTag()) {
            return (!a.hasTag() || Objects.equals(a.getTag(), b.getTag()));
        } else {
            return false;
        }
    }

    private static boolean captureItem(FabricWoodenHopperBlockEntity hopper, ItemEntity p_200114_1_) {
        boolean flag = false;
        ItemStack itemstack = p_200114_1_.getItem().copy();
        ItemStack itemstack1 = putStackInInventoryAllSlots(null, hopper, hopper.inventory, itemstack);
        if (itemstack1.isEmpty()) {
            flag = true;
            p_200114_1_.remove(Entity.RemovalReason.DISCARDED);
        } else {
            p_200114_1_.setItem(itemstack1);
        }
        return flag;
    }

    private static List<ItemEntity> getCaptureItems(FabricWoodenHopperBlockEntity p_200115_0_) {
        return p_200115_0_.getSuckShape().toAabbs().stream().flatMap((p_200110_1_) -> {
            return p_200115_0_.getLevel().getEntitiesOfClass(ItemEntity.class, p_200110_1_.move(p_200115_0_.getLevelX() - 0.5D, p_200115_0_.getLevelY() - 0.5D, p_200115_0_.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
        }).collect(Collectors.toList());
    }

    public void onEntityCollision(Entity p_200113_1_) {
        if (Services.CONFIG.isPullItemsFromWorldEnabled()) {
            if (p_200113_1_ instanceof ItemEntity) {
                BlockPos blockpos = this.getBlockPos();
                if (Shapes.joinIsNotEmpty(Shapes.create(p_200113_1_.getBoundingBox().move((-blockpos.getX()), (-blockpos.getY()), (-blockpos.getZ()))), this.getSuckShape(), BooleanOp.AND)) {
                    this.updateHopper(() -> captureItem(this, (ItemEntity)p_200113_1_));
                }
            }
        }
    }

}
