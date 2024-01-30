package de.cech12.woodenhopper.platform;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import de.cech12.woodenhopper.Constants;
import de.cech12.woodenhopper.platform.services.IConfigHelper;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

/**
 * The config service implementation for NeoForge.
 */
public class NeoForgeConfigHelper implements IConfigHelper {

    private static final ModConfigSpec SERVER_CONFIG;

    private static final ModConfigSpec.IntValue COOLDOWN;
    public static final ModConfigSpec.BooleanValue PULL_ITEMS_FROM_WORLD_ENABLED;
    public static final ModConfigSpec.BooleanValue PULL_ITEMS_FROM_INVENTORIES_ENABLED;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Options that affect the added Wooden Hopper.").push("Wooden Hopper Settings");

        COOLDOWN = builder
                .comment(COOLDOWN_DESCRIPTION)
                .defineInRange("woodenHopperCooldown", COOLDOWN_DEFAULT, COOLDOWN_MIN, COOLDOWN_MAX);
        PULL_ITEMS_FROM_WORLD_ENABLED = builder
                .comment(PULL_ITEMS_FROM_WORLD_ENABLED_DESCRIPTION)
                .define("woodenHopperPullItemsFromWorldEnabled", PULL_ITEMS_FROM_WORLD_ENABLED_DEFAULT);
        PULL_ITEMS_FROM_INVENTORIES_ENABLED = builder
                .comment(PULL_ITEMS_FROM_INVENTORIES_ENABLED_DESCRIPTION)
                .define("woodenHopperPullItemsFromInventoriesEnabled", PULL_ITEMS_FROM_INVENTORIES_ENABLED_DEFAULT);

        builder.pop();

        SERVER_CONFIG = builder.build();
    }

    @Override
    public void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
        Path path = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).resolve(Constants.MOD_ID + "-server.toml");
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        SERVER_CONFIG.setConfig(configData);
    }

    @Override
    public int getCooldown() {
        try {
            return COOLDOWN.get();
        } catch (IllegalStateException ex) {
            return COOLDOWN_DEFAULT;
        }
    }

    @Override
    public boolean isPullItemsFromWorldEnabled() {
        try {
            return PULL_ITEMS_FROM_WORLD_ENABLED.get();
        } catch (IllegalStateException ex) {
            return PULL_ITEMS_FROM_WORLD_ENABLED_DEFAULT;
        }
    }

    @Override
    public boolean isPullItemsFromInventoriesEnabled() {
        try {
            return PULL_ITEMS_FROM_INVENTORIES_ENABLED.get();
        } catch (IllegalStateException ex) {
            return PULL_ITEMS_FROM_INVENTORIES_ENABLED_DEFAULT;
        }
    }

}
