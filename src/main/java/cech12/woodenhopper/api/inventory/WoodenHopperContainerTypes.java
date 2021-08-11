package cech12.woodenhopper.api.inventory;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class WoodenHopperContainerTypes {

    public final static ResourceLocation WOODEN_HOPPER_ID = new ResourceLocation(WoodenHopperMod.MOD_ID, "woodenhopper");

    public static MenuType<? extends AbstractContainerMenu> WOODEN_HOPPER = IForgeContainerType.create((pWindowID, pInventory, pData) -> {
        BlockPos pos = pData.readBlockPos();
        return new WoodenHopperContainer(pWindowID, pInventory, pos);
    }).setRegistryName(WOODEN_HOPPER_ID);

}
