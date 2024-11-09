package de.cech12.woodenhopper.platform;

import de.cech12.woodenhopper.Constants;
import de.cech12.woodenhopper.block.WoodenHopperBlock;
import de.cech12.woodenhopper.blockentity.NeoForgeWoodenHopperBlockEntity;
import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import de.cech12.woodenhopper.platform.services.IRegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class NeoForgeRegistryHelper implements IRegistryHelper {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Constants.MOD_ID);

    static {
        DeferredBlock<Block> block = BLOCKS.register(Constants.BLOCK_ITEM_NAME, () -> new WoodenHopperBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion().setId(ResourceKey.create(BuiltInRegistries.BLOCK.key(), Constants.id(Constants.BLOCK_ITEM_NAME)))));
        Constants.WOODEN_HOPPER_BLOCK = block;
        Constants.WOODEN_HOPPER_ITEM = fromBlock(Constants.BLOCK_ITEM_NAME, block);
        Constants.WOODEN_HOPPER_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register(Constants.BLOCK_ITEM_NAME, () -> new BlockEntityType<>(NeoForgeWoodenHopperBlockEntity::new, Constants.WOODEN_HOPPER_BLOCK.get()));
        Constants.WOODEN_HOPPER_MENU_TYPE = MENU_TYPES.register("woodenhopper", () -> IMenuTypeExtension.create((pWindowID, pInventory, pData) -> new WoodenHopperContainer(pWindowID, pInventory)));
    }

    private static DeferredItem<Item> fromBlock(String name, DeferredBlock<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Constants.id(name)))));
    }

    @Override
    public WoodenHopperBlockEntity getNewBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new NeoForgeWoodenHopperBlockEntity(pos, state);
    }

}
