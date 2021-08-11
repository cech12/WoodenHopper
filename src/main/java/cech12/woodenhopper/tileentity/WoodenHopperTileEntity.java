package cech12.woodenhopper.tileentity;

import cech12.woodenhopper.api.tileentity.WoodenHopperTileEntities;
import cech12.woodenhopper.block.WoodenHopperItemHandler;
import cech12.woodenhopper.config.ServerConfig;
import cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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

public class WoodenHopperTileEntity extends LockableLootTileEntity implements IHopper, ITickableTileEntity {

    private ItemStackHandler inventory = new ItemStackHandler();
    private int transferCooldown = -1;
    private long tickedGameTime;

    public WoodenHopperTileEntity() {
        super(WoodenHopperTileEntities.WOODEN_HOPPER);
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        inventory = new ItemStackHandler();
        if (!this.tryLoadLootTable(nbt)) {
            this.inventory.deserializeNBT(nbt);
        }
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        super.save(compound);
        if (!this.trySaveLootTable(compound)) {
            compound.merge(this.inventory.serializeNBT());
        }
        compound.putInt("TransferCooldown", this.transferCooldown);
        return compound;
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
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("block.woodenhopper.wooden_hopper");
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            this.transferCooldown--;
            this.tickedGameTime = this.level.getGameTime();
            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateHopper(() -> pullItems(this));
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

    private static ItemStack putStackInInventoryAllSlots(TileEntity source, Object destination, IItemHandler destInventory, ItemStack stack) {
        for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++) {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    private static ItemStack insertStack(TileEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot) {
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
                if (inventoryWasEmpty && destination instanceof WoodenHopperTileEntity) {
                    WoodenHopperTileEntity destinationHopper = (WoodenHopperTileEntity)destination;
                    if (!destinationHopper.mayTransfer()) {
                        int k = 0;
                        if (source instanceof WoodenHopperTileEntity) {
                            if (destinationHopper.getLastUpdateTime() >= ((WoodenHopperTileEntity) source).getLastUpdateTime()) {
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

    private static Optional<Pair<IItemHandler, Object>> getItemHandler(IHopper hopper, Direction hopperFacing) {
        double x = hopper.getLevelX() + (double) hopperFacing.getStepX();
        double y = hopper.getLevelY() + (double) hopperFacing.getStepY();
        double z = hopper.getLevelZ() + (double) hopperFacing.getStepZ();
        return getItemHandler(hopper.getLevel(), x, y, z, hopperFacing.getOpposite());
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

    public static Optional<Pair<IItemHandler, Object>> getItemHandler(World worldIn, double x, double y, double z, final Direction side) {
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState state = worldIn.getBlockState(blockpos);
        if (state.hasTileEntity()) {
            TileEntity tileentity = worldIn.getBlockEntity(blockpos);
            if (tileentity != null) {
                return tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                        .map(capability -> ImmutablePair.of(capability, tileentity));
            }
        }
        //support vanilla inventory blocks without IItemHandler
        Block block = state.getBlock();
        if (block instanceof ISidedInventoryProvider) {
            return Optional.of(ImmutablePair.of(new SidedInvWrapper(((ISidedInventoryProvider)block).getContainer(state, worldIn, blockpos), side), state));
        }
        return Optional.empty();
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
    public boolean pullItems(IHopper hopper) {
        return getItemHandler(this, Direction.UP)
                .map(itemHandlerResult -> {
                    //get item from item handler
                    if (ServerConfig.WOODEN_HOPPER_PULL_ITEMS_FROM_INVENTORIES_ENABLED.get()) {
                        IItemHandler handler = itemHandlerResult.getKey();
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack extractItem = handler.extractItem(i, 1, true);
                            if (!extractItem.isEmpty()) {
                                for (int j = 0; j < this.getContainerSize(); j++) {
                                    ItemStack destStack = this.getItem(j);
                                    if (this.canPlaceItem(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize()
                                            && destStack.getCount() < this.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack))) {
                                        extractItem = handler.extractItem(i, 1, false);
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

    public boolean captureItem(IInventory p_200114_0_, ItemEntity p_200114_1_) {
        boolean flag = false;
        ItemStack itemstack = p_200114_1_.getItem().copy();
        ItemStack itemstack1 = putStackInInventoryAllSlots(null, p_200114_0_, this.inventory, itemstack);
        if (itemstack1.isEmpty()) {
            flag = true;
            p_200114_1_.remove();
        } else {
            p_200114_1_.setItem(itemstack1);
        }
        return flag;
    }

    public static List<ItemEntity> getCaptureItems(IHopper p_200115_0_) {
        return p_200115_0_.getSuckShape().toAabbs().stream().flatMap((p_200110_1_) -> {
            return p_200115_0_.getLevel().getEntitiesOfClass(ItemEntity.class, p_200110_1_.move(p_200115_0_.getLevelX() - 0.5D, p_200115_0_.getLevelY() - 0.5D, p_200115_0_.getLevelZ() - 0.5D), EntityPredicates.ENTITY_STILL_ALIVE).stream();
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
                if (VoxelShapes.joinIsNotEmpty(VoxelShapes.create(p_200113_1_.getBoundingBox().move((-blockpos.getX()), (-blockpos.getY()), (-blockpos.getZ()))), this.getSuckShape(), IBooleanFunction.AND)) {
                    this.updateHopper(() -> captureItem(this, (ItemEntity)p_200113_1_));
                }
            }
        }
    }

    @Override
    @Nonnull
    protected Container createMenu(int id, @Nonnull PlayerInventory player) {
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
