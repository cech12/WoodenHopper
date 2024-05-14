package de.cech12.woodenhopper.block;

import de.cech12.woodenhopper.Constants;
import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WoodenHopperBlock extends HopperBlock {

    public WoodenHopperBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (!Services.CONFIG.isPullItemsFromWorldEnabled()) {
            tooltip.add(Component.translatable("block.woodenhopper.wooden_hopper.desc.cannotAbsorbItemsFromWorld").withStyle(ChatFormatting.RED));
        }
        if (!Services.CONFIG.isPullItemsFromInventoriesEnabled()) {
            tooltip.add(Component.translatable("block.woodenhopper.wooden_hopper.desc.cannotAbsorbItemsFromInventories").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return Services.REGISTRY.getNewBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> entityType) {
        return createTickerHelper(entityType, Constants.WOODEN_HOPPER_BLOCK_ENTITY_TYPE.get(), (lev, pos, sta, entity) -> WoodenHopperBlockEntity.tick(lev, entity));
    }

    @Override
    @NotNull
    public InteractionResult useWithoutItem(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof WoodenHopperBlockEntity) {
                player.openMenu((WoodenHopperBlockEntity)blockEntity);
                player.awardStat(Stats.INSPECT_HOPPER);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof WoodenHopperBlockEntity) {
                Containers.dropContents(worldIn, pos, (WoodenHopperBlockEntity)blockEntity);
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (Services.CONFIG.isPullItemsFromWorldEnabled() && entity instanceof ItemEntity itemEntity) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof WoodenHopperBlockEntity woodenHopperBlockEntity
                    && !itemEntity.getItem().isEmpty() && itemEntity.getBoundingBox().move((-pos.getX()), (-pos.getY()), (-pos.getZ())).intersects(woodenHopperBlockEntity.getSuckAabb())
            ) {
                woodenHopperBlockEntity.onItemEntityIsCaptured(itemEntity);
            }
        }
    }

}