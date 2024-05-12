package de.cech12.woodenhopper.platform;

import de.cech12.woodenhopper.blockentity.FabricWoodenHopperBlockEntity;
import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.platform.services.IRegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public BlockEntityTicker<WoodenHopperBlockEntity> getBlockTicker() {
        return FabricWoodenHopperBlockEntity::tick;
    }

    @Override
    public WoodenHopperBlockEntity getNewBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FabricWoodenHopperBlockEntity(pos, state);
    }

}
