package de.cech12.woodenhopper.platform.services;

/**
 * Common configuration helper service interface.
 */
public interface IConfigHelper {

    /** Default value of the hoppers cooldown value */
    int COOLDOWN_DEFAULT = 16;
    /** Config description of the hoppers cooldown value */
    String COOLDOWN_DESCRIPTION = "Time (ticks) that passes between two wooden hopper operations. (default: " + COOLDOWN_DEFAULT + " ticks) (vanilla hopper: 8 ticks)";
    /** Minimal value of the hoppers cooldown value */
    int COOLDOWN_MIN = 1;
    /** Maximal value of the hoppers cooldown value */
    int COOLDOWN_MAX = 1000;

    /** Default value of the hoppers "pull items from world" value */
    boolean PULL_ITEMS_FROM_WORLD_ENABLED_DEFAULT = true;
    /** Config description of the hoppers "pull items from world" value */
    String PULL_ITEMS_FROM_WORLD_ENABLED_DESCRIPTION = "Whether the wooden hopper can pull item entities lying above it. (default: " + PULL_ITEMS_FROM_WORLD_ENABLED_DEFAULT + ")";

    /** Default value of the hoppers "pull items from inventories" value */
    boolean PULL_ITEMS_FROM_INVENTORIES_ENABLED_DEFAULT = true;
    /** Config description of the hoppers "pull items from inventories" value */
    String PULL_ITEMS_FROM_INVENTORIES_ENABLED_DESCRIPTION = "Whether the wooden hopper can pull items from inventories above it. (default: " + PULL_ITEMS_FROM_INVENTORIES_ENABLED_DEFAULT + ")";

    /**
     * Initialization method for the Service implementations.
     */
    void init();

    /**
     * Gets the configured cooldown value.
     *
     * @return configured cooldown value
     */
    int getCooldown();

    /**
     * Gets the configured "pull items from world" value.
     *
     * @return configured "pull items from world" value
     */
    boolean isPullItemsFromWorldEnabled();

    /**
     * Gets the configured "pull items from inventories" value.
     *
     * @return configured "pull items from inventories" value
     */
    boolean isPullItemsFromInventoriesEnabled();

}