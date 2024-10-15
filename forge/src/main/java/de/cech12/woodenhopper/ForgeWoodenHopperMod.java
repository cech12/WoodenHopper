package de.cech12.woodenhopper;

import de.cech12.woodenhopper.client.WoodenHopperScreen;
import de.cech12.woodenhopper.platform.ForgeRegistryHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid= Constants.MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ForgeWoodenHopperMod {

    public ForgeWoodenHopperMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ForgeRegistryHelper.BLOCKS.register(modEventBus);
        ForgeRegistryHelper.ITEMS.register(modEventBus);
        ForgeRegistryHelper.BLOCK_ENTITY_TYPES.register(modEventBus);
        ForgeRegistryHelper.MENU_TYPES.register(modEventBus);
        CommonLoader.init();
    }

    @SubscribeEvent
    public static void onClientRegister(FMLClientSetupEvent event) {
        MenuScreens.register(Constants.WOODEN_HOPPER_MENU_TYPE.get(), WoodenHopperScreen::new);
    }

    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Constants.WOODEN_HOPPER_ITEM);
        }
    }

}
