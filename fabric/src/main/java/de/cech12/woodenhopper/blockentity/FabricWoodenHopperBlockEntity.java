package de.cech12.woodenhopper.blockentity;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class FabricWoodenHopperBlockEntity extends WoodenHopperBlockEntity {

    InventoryStorage inventory = InventoryStorage.of(this, null);
    private NonNullList<ItemStack> items;

    public FabricWoodenHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = NonNullList.withSize(1, ItemStack.EMPTY);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compoundTag)) {
            ContainerHelper.loadAllItems(compoundTag, this.items, provider);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.items, provider);
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
    @NotNull
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    @NotNull
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
    @NotNull
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
    public void setItem(int index, @NotNull ItemStack itemStack) {
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
                    blockEntity.updateHopper(blockEntity::pullItems);
                }
            }
        }
    }

    @Override
    protected boolean pullItemsFromItemHandler(Object itemHandler, Object destination) {
        Storage<ItemVariant> storage = (Storage<ItemVariant>) itemHandler;
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<ItemVariant> slot : storage.nonEmptyViews()) {
                if (StorageUtil.simulateExtract(slot, slot.getResource(), 1, transaction) > 0) {
                    ItemStack extractedItem = slot.getResource().toStack(1);
                    for (int j = 0; j < this.getContainerSize(); j++) {
                        ItemStack destStack = this.getItem(j);
                        if (this.canPlaceItem(j, extractedItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize()
                                && destStack.getCount() < this.getMaxStackSize() && ItemStack.isSameItemSameComponents(extractedItem, destStack))) {
                            if (storage.extract(slot.getResource(), 1, transaction) > 0) {
                                if (destStack.isEmpty()) {
                                    this.setItem(j, extractedItem);
                                } else {
                                    destStack.grow(1);
                                    this.setItem(j, destStack);
                                }
                                this.setChanged();
                                transaction.commit();
                                return true;
                            }
                        }
                    }
                }
            }
            transaction.abort();
        }
        return false;
    }

    @Override
    protected Optional<Pair<Object, Object>> getItemHandler(Level level, double x, double y, double z, final Direction side) {
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

    @Override
    protected boolean isNotFull(Object itemHandler) {
        for (StorageView<ItemVariant> slot : (Storage<ItemVariant>) itemHandler) {
            if (slot.isResourceBlank() || slot.getAmount() < slot.getCapacity()) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmpty(Storage<ItemVariant> itemHandler) {
        for (StorageView<ItemVariant> slot : itemHandler.nonEmptyViews()) {
            if (!slot.isResourceBlank()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Object getOwnItemHandler() {
        return this.inventory;
    }

    @Override
    protected ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, Object destInventoryObj, ItemStack stack) {
        Storage<ItemVariant> destInventory = (Storage<ItemVariant>) destInventoryObj;
        boolean inventoryWasEmpty = isEmpty(destInventory);
        try (Transaction transaction = Transaction.openOuter()) {
            long count = destInventory.insert(ItemVariant.of(stack), stack.getCount(), transaction);
            if (count > 0) {
                stack.shrink((int)count);
                updateCooldown(inventoryWasEmpty, source, destination);
                transaction.commit();
            } else {
                transaction.abort();
            }
        }
        return stack;
    }

}
