package de.cech12.woodenhopper.client;

import de.cech12.woodenhopper.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class FabricWoodenHopperClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(Constants.WOODEN_HOPPER_MENU_TYPE.get(), WoodenHopperScreen::new);
    }

}
