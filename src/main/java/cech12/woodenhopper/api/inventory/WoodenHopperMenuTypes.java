package cech12.woodenhopper.api.inventory;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WoodenHopperMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.Keys.MENU_TYPES, WoodenHopperMod.MOD_ID);

    public static final RegistryObject<MenuType<WoodenHopperContainer>> WOODEN_HOPPER = MENU_TYPES.register("woodenhopper", () -> IForgeMenuType.create((pWindowID, pInventory, pData) -> new WoodenHopperContainer(pWindowID, pInventory)));

}
