package de.cech12.woodenhopper.platform;

import de.cech12.woodenhopper.Constants;
import de.cech12.woodenhopper.block.WoodenHopperBlock;
import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import de.cech12.woodenhopper.platform.services.IRegistryHelper;
import de.cech12.woodenhopper.blockentity.NeoForgeWoodenHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;

public class NeoForgeRegistryHelper implements IRegistryHelper {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    public static final DeferredBlock<Block> WOODEN_HOPPER_BLOCK = BLOCKS.register("wooden_hopper", () -> new WoodenHopperBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));


    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);

    public static final DeferredItem<Item> WOODEN_HOPPER_ITEM = fromBlock(WOODEN_HOPPER_BLOCK);


    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NeoForgeWoodenHopperBlockEntity>> WOODEN_HOPPER_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("wooden_hopper", () -> BlockEntityType.Builder.of(NeoForgeWoodenHopperBlockEntity::new, WOODEN_HOPPER_BLOCK.get()).build(null));


    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Constants.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<WoodenHopperContainer>> WOODEN_HOPPER_MENU_TYPE = MENU_TYPES.register("woodenhopper", () -> IMenuTypeExtension.create((pWindowID, pInventory, pData) -> new WoodenHopperContainer(pWindowID, pInventory)));


    private static DeferredItem<Item> fromBlock(DeferredBlock<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    @Override
    public BlockEntityTicker<WoodenHopperBlockEntity> getBlockTicker() {
        return NeoForgeWoodenHopperBlockEntity::tick;
    }

    @Override
    public BlockEntityType<WoodenHopperBlockEntity> getBlockEntityType() {
        return (BlockEntityType<WoodenHopperBlockEntity>) (Object) WOODEN_HOPPER_BLOCK_ENTITY_TYPE.get();
    }

    @Override
    public WoodenHopperBlockEntity getNewBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new NeoForgeWoodenHopperBlockEntity(pos, state);
    }

    @Override
    public MenuType<WoodenHopperContainer> getMenuType() {
        return WOODEN_HOPPER_MENU_TYPE.get();
    }

    @Override
    public void onEntityCollision(@Nonnull WoodenHopperBlockEntity blockEntity, @Nonnull Entity entity) {
        if (blockEntity instanceof NeoForgeWoodenHopperBlockEntity forgeBlockEntity) {
            forgeBlockEntity.onEntityCollision(entity);
        }
    }
}
