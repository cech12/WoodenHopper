package cech12.woodenhopper.block;

import cech12.woodenhopper.config.ServerConfig;
import cech12.woodenhopper.tileentity.WoodenHopperTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WoodenHopperBlock extends HopperBlock {

    public WoodenHopperBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!ServerConfig.WOODEN_HOPPER_PULL_ITEMS_FROM_WORLD_ENABLED.get()) {
            tooltip.add(new TranslationTextComponent("block.woodenhopper.wooden_hopper.desc.cannotAbsorbItemsFromWorld").withStyle(TextFormatting.RED));
        }
        if (!ServerConfig.WOODEN_HOPPER_PULL_ITEMS_FROM_INVENTORIES_ENABLED.get()) {
            tooltip.add(new TranslationTextComponent("block.woodenhopper.wooden_hopper.desc.cannotAbsorbItemsFromInventories").withStyle(TextFormatting.RED));
        }
    }

    @Override
    public boolean isToolEffective(BlockState state, ToolType tool) {
        return tool == ToolType.AXE;
    }

    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader worldIn) {
        return new WoodenHopperTileEntity();
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void setPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof WoodenHopperTileEntity) {
                ((WoodenHopperTileEntity)tileentity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    @Nonnull
    public ActionResultType use(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player,
                                             @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        if (worldIn.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof WoodenHopperTileEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (WoodenHopperTileEntity) tileentity, pos);
                player.awardStat(Stats.INSPECT_HOPPER);
            }
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof WoodenHopperTileEntity) {
                InventoryHelper.dropContents(worldIn, pos, (WoodenHopperTileEntity)tileentity);
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void entityInside(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof WoodenHopperTileEntity) {
            ((WoodenHopperTileEntity)tileentity).onEntityCollision(entityIn);
        }
    }

}