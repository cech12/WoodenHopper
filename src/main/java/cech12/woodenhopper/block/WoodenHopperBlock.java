package cech12.woodenhopper.block;

import cech12.woodenhopper.api.tileentity.WoodenHopperTileEntities;
import cech12.woodenhopper.config.ServerConfig;
import cech12.woodenhopper.tileentity.WoodenHopperTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WoodenHopperBlock extends HopperBlock {

    public WoodenHopperBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!ServerConfig.WOODEN_HOPPER_PULL_ITEMS_FROM_WORLD_ENABLED.get()) {
            tooltip.add(new TranslatableComponent("block.woodenhopper.wooden_hopper.desc.cannotAbsorbItemsFromWorld").withStyle(ChatFormatting.RED));
        }
        if (!ServerConfig.WOODEN_HOPPER_PULL_ITEMS_FROM_INVENTORIES_ENABLED.get()) {
            tooltip.add(new TranslatableComponent("block.woodenhopper.wooden_hopper.desc.cannotAbsorbItemsFromInventories").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new WoodenHopperTileEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> entityType) {
        return createTickerHelper(entityType, (BlockEntityType<WoodenHopperTileEntity>) WoodenHopperTileEntities.WOODEN_HOPPER, WoodenHopperTileEntity::tick);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof WoodenHopperTileEntity) {
                ((WoodenHopperTileEntity)tileentity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    @Nonnull
    public InteractionResult use(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull Player player,
                                             @Nonnull InteractionHand handIn, @Nonnull BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof WoodenHopperTileEntity) {
                NetworkHooks.openGui((ServerPlayer) player, (WoodenHopperTileEntity) tileentity, pos);
                player.awardStat(Stats.INSPECT_HOPPER);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof WoodenHopperTileEntity) {
                Containers.dropContents(worldIn, pos, (WoodenHopperTileEntity)tileentity);
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void entityInside(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof WoodenHopperTileEntity) {
            ((WoodenHopperTileEntity)tileentity).onEntityCollision(entityIn);
        }
    }

}