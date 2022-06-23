package cech12.woodenhopper.tileentity;

import cech12.woodenhopper.api.blockentity.WoodenHopperBlockEntities;
import cech12.woodenhopper.block.WoodenHopperItemHandler;
import cech12.woodenhopper.config.ServerConfig;
import cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WoodenHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {

    private ItemStackHandler inventory = new ItemStackHandler();
    private int transferCooldown = -1;
    private long tickedGameTime;

    public WoodenHopperBlockEntity(BlockPos pos, BlockState state) {
        super(WoodenHopperBlockEntities.WOODEN_HOPPER.get(), pos, state);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        inventory = new ItemStackHandler();
        if (!this.tryLoadLootTable(nbt)) {
            this.inventory.deserializeNBT(nbt);
        }
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        if (!this.trySaveLootTable(compound)) {
            compound.merge(this.inventory.serializeNBT());
        }
        compound.putInt("TransferCooldown", this.transferCooldown);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getContainerSize() {
        return this.inventory.getSlots();
    }

    @Override
    @Nonnull
    protected NonNullList<ItemStack> getItems() {
        return NonNullList.withSize(1, this.inventory.getStackInSlot(0));
    }

    @Override
    protected void setItems(@Nonnull NonNullList<ItemStack> itemsIn) {
        this.inventory.setStackInSlot(0, itemsIn.get(0));
        this.setChanged();
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    @Nonnull
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        ItemStack stack = this.inventory.extractItem(index, count, false);
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
        ItemStack stack = this.inventory.getStackInSlot(index);
        this.inventory.setStackInSlot(index, ItemStack.EMPTY);
        this.setChanged();
        return stack;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        this.unpackLootTable(null);
        this.inventory.setStackInSlot(index, stack);
        this.setChanged();
    }

    @Override
    @Nonnull
    protected Component getDefaultName() {
        return Component.translatable("block.woodenhopper.wooden_hopper");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WoodenHopperBlockEntity entity) {
        if (level != null && !level.isClientSide) {
            entity.transferCooldown--;
            entity.tickedGameTime = level.getGameTime();
            if (!entity.isOnTransferCooldown()) {
                entity.setTransferCooldown(0);
                entity.updateHopper(() -> pullItems(entity));
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
                    this.setTransferCooldown(ServerConfig.WOODEN_HOPPER_COOLDOWN.get());
                    this.setChanged();
                }
            }
        }
    }

    private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack) {
        for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++) {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot) {
        ItemStack itemstack = destInventory.getStackInSlot(slot);
        if (destInventory.insertItem(slot, stack, true).isEmpty()) {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);
            if (itemstack.isEmpty()) {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            } else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack)) {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                insertedItem = originalSize < stack.getCount();
            }
            if (insertedItem) {
                if (inventoryWasEmpty && destination instanceof WoodenHopperBlockEntity) {
                    WoodenHopperBlockEntity destinationHopper = (WoodenHopperBlockEntity)destination;
                    if (!destinationHopper.mayTransfer()) {
                        int k = 0;
                        if (source instanceof WoodenHopperBlockEntity) {
                            if (destinationHopper.getLastUpdateTime() >= ((WoodenHopperBlockEntity) source).getLastUpdateTime()) {
                                k = 1;
                            }
                        }
                        destinationHopper.setTransferCooldown(ServerConfig.WOODEN_HOPPER_COOLDOWN.get() - k);
                    }
                }
            }
        }

        return stack;
    }

    private static Optional<Pair<IItemHandler, Object>> getItemHandler(WoodenHopperBlockEntity hopper, Direction hopperFacing) {
        double x = hopper.getLevelX() + (double) hopperFacing.getStepX();
        double y = hopper.getLevelY() + (double) hopperFacing.getStepY();
        double z = hopper.getLevelZ() + (double) hopperFacing.getStepZ();
        return getItemHandler(hopper.getLevel(), x, y, z, hopperFacing.getOpposite());
    }

    public static Optional<Pair<IItemHandler, Object>> getItemHandler(Level worldIn, double x, double y, double z, final Direction side) {
        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState state = worldIn.getBlockState(blockpos);
        if (state.hasBlockEntity()) {
            BlockEntity tileentity = worldIn.getBlockEntity(blockpos);
            if (tileentity != null) {
                return tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                        .map(capability -> ImmutablePair.of(capability, tileentity));
            }
        }
        //support vanilla inventory blocks without IItemHandler
        Block block = state.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            return Optional.of(ImmutablePair.of(new SidedInvWrapper(((WorldlyContainerHolder)block).getContainer(state, worldIn, blockpos), side), state));
        }
        return Optional.empty();
    }

    private static boolean isNotFull(IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmpty(IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean transferItemsOut() {
        Direction hopperFacing = this.getBlockState().getValue(HopperBlock.FACING);
        return getItemHandler(this, hopperFacing)
                .map(destinationResult -> {
                    IItemHandler itemHandler = destinationResult.getKey();
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

    /**
     * Pull dropped EntityItems from the world above the hopper and items
     * from any inventory attached to this hopper into the hopper's inventory.
     *
     * @param hopper the hopper in question
     * @return whether any items were successfully added to the hopper
     */
    public static boolean pullItems(WoodenHopperBlockEntity hopper) {
        return getItemHandler(hopper, Direction.UP)
                .map(itemHandlerResult -> {
                    //get item from item handler
                    if (ServerConfig.WOODEN_HOPPER_PULL_ITEMS_FROM_INVENTORIES_ENABLED.get()) {
                        IItemHandler handler = itemHandlerResult.getKey();
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack extractItem = handler.extractItem(i, 1, true);
                            if (!extractItem.isEmpty()) {
                                for (int j = 0; j < hopper.getContainerSize(); j++) {
                                    ItemStack destStack = hopper.getItem(j);
                                    if (hopper.canPlaceItem(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize()
                                            && destStack.getCount() < hopper.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack))) {
                                        extractItem = handler.extractItem(i, 1, false);
                                        if (destStack.isEmpty()) {
                                            hopper.setItem(j, extractItem);
                                        } else {
                                            destStack.grow(1);
                                            hopper.setItem(j, destStack);
                                        }
                                        hopper.setChanged();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }).orElseGet(() -> {
                    //capture item
                    if (ServerConfig.WOODEN_HOPPER_PULL_ITEMS_FROM_WORLD_ENABLED.get()) {
                        for (ItemEntity itementity : getCaptureItems(hopper)) {
                            if (captureItem(hopper, itementity)) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
    }

    public static boolean captureItem(WoodenHopperBlockEntity hopper, ItemEntity p_200114_1_) {
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

    public static List<ItemEntity> getCaptureItems(WoodenHopperBlockEntity p_200115_0_) {
        return p_200115_0_.getSuckShape().toAabbs().stream().flatMap((p_200110_1_) -> {
            return p_200115_0_.getLevel().getEntitiesOfClass(ItemEntity.class, p_200110_1_.move(p_200115_0_.getLevelX() - 0.5D, p_200115_0_.getLevelY() - 0.5D, p_200115_0_.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
        }).collect(Collectors.toList());
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

    public void setTransferCooldown(int ticks) {
        this.transferCooldown = ticks;
    }

    private boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public boolean mayTransfer() {
        return this.transferCooldown > ServerConfig.WOODEN_HOPPER_COOLDOWN.get();
    }

    public void onEntityCollision(Entity p_200113_1_) {
        if (ServerConfig.WOODEN_HOPPER_PULL_ITEMS_FROM_WORLD_ENABLED.get()) {
            if (p_200113_1_ instanceof ItemEntity) {
                BlockPos blockpos = this.getBlockPos();
                if (Shapes.joinIsNotEmpty(Shapes.create(p_200113_1_.getBoundingBox().move((-blockpos.getX()), (-blockpos.getY()), (-blockpos.getZ()))), this.getSuckShape(), BooleanOp.AND)) {
                    this.updateHopper(() -> captureItem(this, (ItemEntity)p_200113_1_));
                }
            }
        }
    }

    @Override
    @Nonnull
    protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory player) {
        return new WoodenHopperContainer(id, player, this);
    }

    @Override
    @Nonnull
    protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
        return new WoodenHopperItemHandler(this);
    }

    public long getLastUpdateTime() {
        return this.tickedGameTime;
    }
}
