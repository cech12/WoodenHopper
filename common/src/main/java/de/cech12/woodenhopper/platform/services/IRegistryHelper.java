package de.cech12.woodenhopper.platform.services;

import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * Common registry helper service interface.
 */
public interface IRegistryHelper {

    BlockEntityTicker<WoodenHopperBlockEntity> getBlockTicker();

    BlockEntityType<WoodenHopperBlockEntity> getBlockEntityType();

    WoodenHopperBlockEntity getNewBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state);

    MenuType<WoodenHopperContainer> getMenuType();

    void onEntityCollision(@Nonnull WoodenHopperBlockEntity blockEntity, @Nonnull Entity entity);

}
