package de.cech12.woodenhopper;

import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Class that contains all common constants.
 */
public class Constants {

    /** mod id */
    public static final String MOD_ID = "woodenhopper";
    /** mod name*/
    public static final String MOD_NAME = "Wooden Hopper";
    /** Logger instance */
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static Supplier<Block> WOODEN_HOPPER_BLOCK;
    public static Supplier<Item> WOODEN_HOPPER_ITEM;
    public static Supplier<BlockEntityType<? extends WoodenHopperBlockEntity>> WOODEN_HOPPER_BLOCK_ENTITY_TYPE;
    public static Supplier<MenuType<WoodenHopperContainer>> WOODEN_HOPPER_MENU_TYPE;

    private Constants() {}

}