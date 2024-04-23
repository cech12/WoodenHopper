package de.cech12.woodenhopper.platform;

import de.cech12.woodenhopper.Constants;
import de.cech12.woodenhopper.block.WoodenHopperBlock;
import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import de.cech12.woodenhopper.platform.services.IRegistryHelper;
import de.cech12.woodenhopper.blockentity.ForgeWoodenHopperBlockEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class ForgeRegistryHelper implements IRegistryHelper {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.Keys.MENU_TYPES, Constants.MOD_ID);

    static {
        RegistryObject<Block> block = BLOCKS.register("wooden_hopper", () -> new WoodenHopperBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
        Constants.WOODEN_HOPPER_BLOCK = block;
        Constants.WOODEN_HOPPER_ITEM = fromBlock(block);
        Constants.WOODEN_HOPPER_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("wooden_hopper", () -> BlockEntityType.Builder.of(ForgeWoodenHopperBlockEntity::new, Constants.WOODEN_HOPPER_BLOCK.get()).build(null));
        Constants.WOODEN_HOPPER_MENU_TYPE = MENU_TYPES.register("woodenhopper", () -> IForgeMenuType.create((pWindowID, pInventory, pData) -> new WoodenHopperContainer(pWindowID, pInventory)));
    }

    private static RegistryObject<Item> fromBlock(RegistryObject<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    @Override
    public BlockEntityTicker<WoodenHopperBlockEntity> getBlockTicker() {
        return ForgeWoodenHopperBlockEntity::tick;
    }

    @Override
    public WoodenHopperBlockEntity getNewBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new ForgeWoodenHopperBlockEntity(pos, state);
    }

}
