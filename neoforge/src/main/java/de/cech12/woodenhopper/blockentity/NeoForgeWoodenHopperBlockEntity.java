package de.cech12.woodenhopper.blockentity;

import de.cech12.woodenhopper.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class NeoForgeWoodenHopperBlockEntity extends WoodenHopperBlockEntity {

    private ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(1);

    public NeoForgeWoodenHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        inventory = new ItemStacksResourceHandler(1);
        if (!this.tryLoadLootTable(valueInput)) {
            this.inventory.deserialize(valueInput);
        }
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        if (!this.trySaveLootTable(valueOutput)) {
            this.inventory.serialize(valueOutput);
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    @NotNull
    protected NonNullList<ItemStack> getItems() {
        return NonNullList.withSize(1, this.inventory.getResource(0).toStack(this.inventory.getAmountAsInt(0)));
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> itemsIn) {
        if (itemsIn.size() == 1) {
            this.inventory.set(0, ItemResource.of(itemsIn.getFirst()), itemsIn.getFirst().getCount());
        }
        //this.setChanged(); //don't set it as changed to be compatible with Canary
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    @NotNull
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        try (Transaction transaction = Transaction.open(null)) {
            ItemResource resource = this.inventory.getResource(index);
            if (!resource.isEmpty()) {
                int extractedAmount = this.inventory.extract(index, resource, count, transaction);
                transaction.commit();
                this.setChanged();
                return resource.toStack(extractedAmount);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    @NotNull
    public ItemStack removeItemNoUpdate(int index) {
        try (Transaction transaction = Transaction.open(null)) {
            this.unpackLootTable(null);
            ItemResource resource = this.inventory.getResource(index);
            int amount = this.inventory.getAmountAsInt(index);
            if (!resource.isEmpty() && amount > 0) {
                int extractedAmount = this.inventory.extract(index, resource, amount, transaction);
                ItemStack stack = resource.toStack(extractedAmount);
                transaction.commit();
                this.setChanged();
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setItem(int index, @NotNull ItemStack stack, boolean insideTransaction) {
        super.setItem(index, stack, insideTransaction);
        this.unpackLootTable(null);
        this.inventory.set(index, ItemResource.of(stack), stack.getCount());
        if (!insideTransaction) {
            this.setChanged();
        }
    }

    @Override
    protected ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, Object destInventoryObj, ItemStack stack) {
        ResourceHandler<ItemResource> destInventory = (ResourceHandler<ItemResource>) destInventoryObj;
        for (int slot = 0; slot < destInventory.size() && !stack.isEmpty(); slot++) {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    private ItemStack insertStack(BlockEntity source, Object destination, ResourceHandler<ItemResource> destInventory, ItemStack stack, int slot) {
        ItemStack result = stack;
        boolean inventoryWasEmpty = isEmpty(destInventory);
        try (Transaction transaction = Transaction.open(null)) {
            ItemResource resource = ItemResource.of(stack);
            int insertedAmount = destInventory.insert(slot, resource, stack.getCount(), transaction);
            if (insertedAmount <= stack.getCount()) {
                result = resource.toStack(stack.getCount() - insertedAmount);
                transaction.commit();
                updateCooldown(inventoryWasEmpty, source, destination);
            }
        }
        return result;
    }

    @Override
    protected Optional<Pair<Object, Object>> getItemHandler(Level level, double x, double y, double z, final Direction side) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        BlockState state = level.getBlockState(blockpos);
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(blockpos);
            if (blockEntity != null) {
                ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, blockpos, state, blockEntity, side);
                if (handler != null) {
                    return Optional.of(ImmutablePair.of(handler, blockEntity));
                }
            }
            //support vanilla inventory block entities without IItemHandler
            if (blockEntity instanceof WorldlyContainer container) {
                return Optional.of(ImmutablePair.of(new WorldlyContainerWrapper(container, side), state));
            }
            if (blockEntity instanceof Container container) {
                return Optional.of(ImmutablePair.of(VanillaContainerWrapper.of(container), state));
            }
        }
        //support vanilla inventory blocks without IItemHandler
        Block block = state.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            return Optional.of(ImmutablePair.of(new WorldlyContainerWrapper(((WorldlyContainerHolder)block).getContainer(state, level, blockpos), side), state));
        }
        //get entities with item handlers
        List<Entity> list = getAllAliveEntitiesAt(level, x, y, z,
                entity -> entity instanceof Container || !(entity instanceof LivingEntity) && entity.getCapability(Capabilities.Item.ENTITY_AUTOMATION, side) != null);
        if (!list.isEmpty()) {
            Entity entity = list.get(level.random.nextInt(list.size()));
            ResourceHandler<ItemResource> cap = entity.getCapability(Capabilities.Item.ENTITY_AUTOMATION, side);
            if (cap != null) {
                return Optional.of(ImmutablePair.of(cap, entity));
            }
            if (entity instanceof WorldlyContainer container) {
                return Optional.of(ImmutablePair.of(new WorldlyContainerWrapper(container, side), entity));
            }
            if (entity instanceof Container containerEntity) {
                return Optional.of(ImmutablePair.of(VanillaContainerWrapper.of((containerEntity)), entity));
            }
        }
        return Optional.empty();
    }

    @Override
    protected boolean isNotFull(Object itemHandlerObj) {
        ResourceHandler<ItemResource> itemHandler = (ResourceHandler<ItemResource>) itemHandlerObj;
        for (int slot = 0; slot < itemHandler.size(); slot++) {
            ItemResource resource = itemHandler.getResource(slot);
            int amount = itemHandler.getAmountAsInt(slot);
            if (resource.isEmpty() || amount <= 0 || amount < itemHandler.getCapacityAsInt(slot, resource)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmpty(ResourceHandler<ItemResource> itemHandler) {
        for (int slot = 0; slot < itemHandler.size(); slot++) {
            if (itemHandler.getAmountAsInt(slot) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean pullItemsFromItemHandler(Object itemHandler) {
        ResourceHandler<ItemResource> handler = (ResourceHandler<ItemResource>) itemHandler;
        for (int i = 0; i < handler.size(); i++) {
            try (Transaction transaction = Transaction.open(null)) {
                ItemResource resource = handler.getResource(i);
                if (!resource.isEmpty()) {
                    int extractedAmount = handler.extract(i, resource, 1, transaction);
                    if (extractedAmount > 0) {
                        ItemStack extractItem = resource.toStack(extractedAmount);
                        for (int j = 0; j < this.getContainerSize(); j++) {
                            ItemStack destStack = this.getItem(j);
                            if (this.canPlaceItem(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize()
                                    && destStack.getCount() < this.getMaxStackSize() && ItemStack.isSameItemSameComponents(extractItem, destStack))) {
                                transaction.commit();
                                if (destStack.isEmpty()) {
                                    this.setItem(j, extractItem);
                                } else {
                                    destStack.grow(1);
                                    this.setItem(j, destStack);
                                }
                                this.setChanged();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected Object getOwnItemHandler() {
        return this.inventory;
    }

    @Override
    public void onTransfer(int slot, int amountChange, @NotNull TransactionContext transaction) {
        if (amountChange > 0 && wasEmpty(slot, amountChange)) {
            cooldownTimeJournal.updateSnapshots(transaction);
            transferCooldown = (Services.CONFIG.getCooldown());
        }
    }

    private boolean wasEmpty(int slot, int amountChange) {
        if (inventory.getAmountAsInt(slot) != amountChange) return false;
        for (int i = 0; i < inventory.size(); ++i) {
            if (i != slot && inventory.getAmountAsInt(i) > 0) {
                return false;
            }
        }
        return true;
    }

    private final SnapshotJournal<Integer> cooldownTimeJournal = new SnapshotJournal<>() {
        @Override
        protected Integer createSnapshot() {
            return transferCooldown;
        }
        @Override
        protected void revertToSnapshot(Integer snapshot) {
            transferCooldown = snapshot;
        }
    };

}
