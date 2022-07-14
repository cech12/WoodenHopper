package cech12.woodenhopper.api.blockentity;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.api.block.WoodenHopperBlocks;
import cech12.woodenhopper.tileentity.WoodenHopperBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WoodenHopperBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WoodenHopperMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<WoodenHopperBlockEntity>> WOODEN_HOPPER = BLOCK_ENTITY_TYPES.register("wooden_hopper", () -> BlockEntityType.Builder.of(WoodenHopperBlockEntity::new, WoodenHopperBlocks.WOODEN_HOPPER.get()).build(null));

}
