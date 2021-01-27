package cech12.woodenhopper.init;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.api.block.WoodenHopperBlocks;
import cech12.woodenhopper.api.tileentity.WoodenHopperTileEntities;
import cech12.woodenhopper.tileentity.WoodenHopperTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid= WoodenHopperMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModTileEntities {

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        WoodenHopperTileEntities.WOODEN_HOPPER = register(WoodenHopperTileEntity::new, "wooden_hopper", WoodenHopperBlocks.WOODEN_HOPPER, event);
    }

    private static <T extends TileEntity> TileEntityType<T> register(Supplier<T> supplier, String registryName, Block block, RegistryEvent.Register<TileEntityType<?>> registryEvent) {
        TileEntityType<T> tileEntityType = TileEntityType.Builder.create(supplier, block).build(null);
        tileEntityType.setRegistryName(registryName);
        registryEvent.getRegistry().register(tileEntityType);
        return tileEntityType;
    }

}
