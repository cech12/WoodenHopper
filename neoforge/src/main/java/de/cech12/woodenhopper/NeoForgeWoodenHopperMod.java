package de.cech12.woodenhopper;

import de.cech12.woodenhopper.block.NeoForgeWoodenHopperItemHandler;
import de.cech12.woodenhopper.client.WoodenHopperScreen;
import de.cech12.woodenhopper.platform.NeoForgeRegistryHelper;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
@EventBusSubscriber(modid= Constants.MOD_ID, bus= EventBusSubscriber.Bus.MOD)
public class NeoForgeWoodenHopperMod {

    public NeoForgeWoodenHopperMod(IEventBus modEventBus) {
        NeoForgeRegistryHelper.BLOCKS.register(modEventBus);
        NeoForgeRegistryHelper.ITEMS.register(modEventBus);
        NeoForgeRegistryHelper.BLOCK_ENTITY_TYPES.register(modEventBus);
        NeoForgeRegistryHelper.MENU_TYPES.register(modEventBus);
        CommonLoader.init();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onMenuScreenRegister(RegisterMenuScreensEvent event) {
        event.register(Constants.WOODEN_HOPPER_MENU_TYPE.get(), WoodenHopperScreen::new);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Constants.WOODEN_HOPPER_BLOCK_ENTITY_TYPE.get(), (blockEntity, side) -> new NeoForgeWoodenHopperItemHandler(blockEntity));
    }

    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Constants.WOODEN_HOPPER_ITEM.get());
        }
    }

}
