package cech12.woodenhopper;

import cech12.woodenhopper.api.block.WoodenHopperBlocks;
import cech12.woodenhopper.api.blockentity.WoodenHopperBlockEntities;
import cech12.woodenhopper.api.inventory.WoodenHopperMenuTypes;
import cech12.woodenhopper.api.item.WoodenHopperItems;
import cech12.woodenhopper.client.WoodenHopperScreen;
import cech12.woodenhopper.config.ServerConfig;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import static cech12.woodenhopper.WoodenHopperMod.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid= MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class WoodenHopperMod {

    public static final String MOD_ID = "woodenhopper";

    public WoodenHopperMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        WoodenHopperBlocks.BLOCKS.register(modEventBus);
        WoodenHopperItems.ITEMS.register(modEventBus);
        WoodenHopperBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        WoodenHopperMenuTypes.MENU_TYPES.register(modEventBus);
        //Config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
        ServerConfig.loadConfig(ServerConfig.SERVER_CONFIG, FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(MOD_ID + "-server.toml"));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientRegister(FMLClientSetupEvent event) {
        MenuScreens.register(WoodenHopperMenuTypes.WOODEN_HOPPER.get(), WoodenHopperScreen::new);
    }

    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(WoodenHopperItems.WOODEN_HOPPER);
        }
    }

}
