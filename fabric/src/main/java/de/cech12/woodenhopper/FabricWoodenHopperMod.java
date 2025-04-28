package de.cech12.woodenhopper;

import de.cech12.woodenhopper.block.FabricWoodenHopperBlock;
import de.cech12.woodenhopper.blockentity.FabricWoodenHopperBlockEntity;
import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import de.cech12.woodenhopper.item.WoodenHopperItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class FabricWoodenHopperMod implements ModInitializer {

    private static final Block WOODEN_HOPPER_BLOCK = Registry.register(BuiltInRegistries.BLOCK, Constants.id(Constants.BLOCK_ITEM_NAME), new FabricWoodenHopperBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion().setId(ResourceKey.create(BuiltInRegistries.BLOCK.key(), Constants.id(Constants.BLOCK_ITEM_NAME)))));
    private static final Item WOODEN_HOPPER_ITEM = Registry.register(BuiltInRegistries.ITEM, Constants.id(Constants.BLOCK_ITEM_NAME), new WoodenHopperItem(WOODEN_HOPPER_BLOCK, new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Constants.id(Constants.BLOCK_ITEM_NAME)))));
    private static final BlockEntityType<? extends WoodenHopperBlockEntity> WOODEN_HOPPER_BLOCK_ENTITY_TYPE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.id(Constants.BLOCK_ITEM_NAME), FabricBlockEntityTypeBuilder.create(FabricWoodenHopperBlockEntity::new, WOODEN_HOPPER_BLOCK).build(null));
    private static final MenuType<WoodenHopperContainer> WOODEN_HOPPER_MENU_TYPE = Registry.register(BuiltInRegistries.MENU, Constants.id("woodenhopper"), new ExtendedScreenHandlerType<>((pWindowID, pInventory, pData) -> new WoodenHopperContainer(pWindowID, pInventory), HopperData.CODEC));

    public record HopperData(boolean empty) {
        public static final StreamCodec<RegistryFriendlyByteBuf, HopperData> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                HopperData::empty,
                HopperData::new
        );
    }

    static {
        Constants.WOODEN_HOPPER_BLOCK = () -> WOODEN_HOPPER_BLOCK;
        Constants.WOODEN_HOPPER_ITEM = () -> WOODEN_HOPPER_ITEM;
        Constants.WOODEN_HOPPER_BLOCK_ENTITY_TYPE = () -> WOODEN_HOPPER_BLOCK_ENTITY_TYPE;
        Constants.WOODEN_HOPPER_MENU_TYPE = () -> WOODEN_HOPPER_MENU_TYPE;
    }

    @Override
    public void onInitialize() {
        CommonLoader.init();
        //Register item in the creative tab.
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> content.accept(Constants.WOODEN_HOPPER_ITEM.get()));
    }

}
