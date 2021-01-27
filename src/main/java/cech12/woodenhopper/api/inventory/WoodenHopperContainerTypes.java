package cech12.woodenhopper.api.inventory;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class WoodenHopperContainerTypes {

    public final static ResourceLocation WOODEN_HOPPER_ID = new ResourceLocation(WoodenHopperMod.MOD_ID, "woodenhopper");

    public static ContainerType<? extends Container> WOODEN_HOPPER = IForgeContainerType.create((pWindowID, pInventory, pData) -> {
        BlockPos pos = pData.readBlockPos();
        return new WoodenHopperContainer(pWindowID, pInventory, pos);
    }).setRegistryName(WOODEN_HOPPER_ID);

}
