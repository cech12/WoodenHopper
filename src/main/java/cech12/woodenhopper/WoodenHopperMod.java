package cech12.woodenhopper;

import cech12.woodenhopper.client.WoodenHopperScreen;
import cech12.woodenhopper.config.ServerConfig;
import cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import static cech12.woodenhopper.WoodenHopperMod.MOD_ID;
import static cech12.woodenhopper.api.inventory.WoodenHopperContainerTypes.WOODEN_HOPPER;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid= MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class WoodenHopperMod {

    public static final String MOD_ID = "woodenhopper";

    public WoodenHopperMod() {
        //Config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
        ServerConfig.loadConfig(ServerConfig.SERVER_CONFIG, FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(MOD_ID + "-server.toml"));
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().register(WOODEN_HOPPER);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientRegister(FMLClientSetupEvent event) {
        MenuScreens.register((MenuType<WoodenHopperContainer>) WOODEN_HOPPER, WoodenHopperScreen::new);
    }

}
