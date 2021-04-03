package cech12.woodenhopper.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ServerConfig {
    public static ForgeConfigSpec SERVER_CONFIG;

    public static final ForgeConfigSpec.IntValue WOODEN_HOPPER_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue WOODEN_HOPPER_PULL_ITEMS_FROM_WORLD_ENABLED;
    public static final ForgeConfigSpec.BooleanValue WOODEN_HOPPER_PULL_ITEMS_FROM_INVENTORIES_ENABLED;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Options that affect the added Wooden Hopper.").push("Wooden Hopper Settings");
        WOODEN_HOPPER_COOLDOWN = builder
                .comment("Time (ticks) that passes between two wooden hopper operations. (default: 16 ticks) (vanilla hopper: 8 ticks)")
                .defineInRange("woodenHopperCooldown", 16, 1, 1000);
        WOODEN_HOPPER_PULL_ITEMS_FROM_WORLD_ENABLED = builder
                .comment("Whether or not the wooden hopper can pull item entities lying above it. (default: enabled)")
                .define("woodenHopperPullItemsFromWorldEnabled", true);
        WOODEN_HOPPER_PULL_ITEMS_FROM_INVENTORIES_ENABLED = builder
                .comment("Whether or not the wooden hopper can pull items from inventories above it. (default: enabled)")
                .define("woodenHopperPullItemsFromInventoriesEnabled", true);
        builder.pop();

        SERVER_CONFIG = builder.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }

}
