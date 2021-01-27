package cech12.woodenhopper.init;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.api.block.WoodenHopperBlocks;
import cech12.woodenhopper.block.WoodenHopperBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid= WoodenHopperMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBlocks {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        WoodenHopperBlocks.WOODEN_HOPPER = registerBlock("wooden_hopper", ItemGroup.REDSTONE, new WoodenHopperBlock(Block.Properties.create(Material.WOOD).setRequiresTool().hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    }

    public static Block registerBlock(String name, ItemGroup itemGroup, Block block) {
        Item.Properties itemProperties = new Item.Properties().group(itemGroup);
        BlockItem itemBlock = new BlockItem(block, itemProperties);
        block.setRegistryName(name);
        itemBlock.setRegistryName(name);
        ForgeRegistries.BLOCKS.register(block);
        ForgeRegistries.ITEMS.register(itemBlock);
        return block;
    }

}