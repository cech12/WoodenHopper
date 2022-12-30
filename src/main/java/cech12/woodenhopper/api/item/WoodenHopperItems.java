package cech12.woodenhopper.api.item;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.api.block.WoodenHopperBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WoodenHopperItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WoodenHopperMod.MOD_ID);

    public static final RegistryObject<Item> WOODEN_HOPPER = fromBlock(WoodenHopperBlocks.WOODEN_HOPPER);

    private static RegistryObject<Item> fromBlock(RegistryObject<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

}
