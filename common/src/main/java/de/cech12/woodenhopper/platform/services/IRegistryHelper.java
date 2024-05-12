package de.cech12.woodenhopper.platform.services;

import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Common registry helper service interface.
 */
public interface IRegistryHelper {

    BlockEntityTicker<WoodenHopperBlockEntity> getBlockTicker();

    WoodenHopperBlockEntity getNewBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);

}
