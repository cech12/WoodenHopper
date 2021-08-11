package cech12.woodenhopper.init;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.api.block.WoodenHopperBlocks;
import cech12.woodenhopper.block.WoodenHopperBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.BlockBehaviour;

@Mod.EventBusSubscriber(modid= WoodenHopperMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBlocks {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        WoodenHopperBlocks.WOODEN_HOPPER = registerBlock("wooden_hopper", CreativeModeTab.TAB_REDSTONE, new WoodenHopperBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
    }

    public static Block registerBlock(String name, CreativeModeTab itemGroup, Block block) {
        Item.Properties itemProperties = new Item.Properties().tab(itemGroup);
        BlockItem itemBlock = new BlockItem(block, itemProperties);
        block.setRegistryName(name);
        itemBlock.setRegistryName(name);
        ForgeRegistries.BLOCKS.register(block);
        ForgeRegistries.ITEMS.register(itemBlock);
        return block;
    }

}