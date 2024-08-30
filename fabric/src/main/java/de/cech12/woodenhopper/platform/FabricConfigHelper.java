package de.cech12.woodenhopper.platform;

import de.cech12.woodenhopper.Constants;
import de.cech12.woodenhopper.platform.services.IConfigHelper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

/**
 * The config service implementation for Fabric.
 */
@Config(name = Constants.MOD_ID)
public class FabricConfigHelper implements ConfigData, IConfigHelper {

    @ConfigEntry.Gui.Tooltip(count = 5)
    public int COOLDOWN = COOLDOWN_DEFAULT;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean PULL_ITEMS_FROM_WORLD_ENABLED = PULL_ITEMS_FROM_WORLD_ENABLED_DEFAULT;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean PULL_ITEMS_FROM_INVENTORIES_ENABLED = PULL_ITEMS_FROM_INVENTORIES_ENABLED_DEFAULT;

    @Override
    public void init() {
        AutoConfig.register(FabricConfigHelper.class, Toml4jConfigSerializer::new);
    }

    private FabricConfigHelper getConfig() {
        return AutoConfig.getConfigHolder(FabricConfigHelper.class).getConfig();
    }

    @Override
    public int getCooldown() {
        return Math.clamp(getConfig().COOLDOWN, COOLDOWN_MIN, COOLDOWN_MAX);
    }

    @Override
    public boolean isPullItemsFromWorldEnabled() {
        return getConfig().PULL_ITEMS_FROM_WORLD_ENABLED;
    }

    @Override
    public boolean isPullItemsFromInventoriesEnabled() {
        return getConfig().PULL_ITEMS_FROM_INVENTORIES_ENABLED;
    }

}
