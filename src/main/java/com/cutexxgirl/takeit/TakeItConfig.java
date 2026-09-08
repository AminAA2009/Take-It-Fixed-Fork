package com.cutexxgirl.takeit;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * TakeItConfig - Configuration file handler
 *
 * This class defines all configurable options for the mod.
 * Config file location: config/takeit-common.toml
 */
public class TakeItConfig {

    // Builder used to create the configuration specification
    public static final ForgeConfigSpec.Builder BUILDER =
            new ForgeConfigSpec.Builder();

    // The final configuration specification
    public static final ForgeConfigSpec SPEC;

    // Configuration values
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FULL_MESSAGE;

    public static final ForgeConfigSpec.IntValue RADIUS_X;
    public static final ForgeConfigSpec.IntValue RADIUS_Y;
    public static final ForgeConfigSpec.IntValue RADIUS_Z;

    static {

        BUILDER.push("TakeIt Config");

        // Enable/disable the entire mod functionality
        ENABLE_MOD = BUILDER
                .comment("Enable or disable the mod")
                .define("enableMod", true);

        // Enable/disable the "Inventory is Full" message
        ENABLE_FULL_MESSAGE = BUILDER
                .comment("Enable or disable the Inventory is Full message")
                .define("enableFullMessage", true);

        // X-axis pickup radius
        RADIUS_X = BUILDER
                .comment("Pickup radius in X direction (blocks)")
                .defineInRange("radiusX", 2, 0, 16);

        // Y-axis pickup radius
        RADIUS_Y = BUILDER
                .comment("Pickup radius in Y direction (blocks)")
                .defineInRange("radiusY", 2, 0, 16);

        // Z-axis pickup radius
        RADIUS_Z = BUILDER
                .comment("Pickup radius in Z direction (blocks)")
                .defineInRange("radiusZ", 2, 0, 16);

        BUILDER.pop();

        // Build the final configuration specification
        SPEC = BUILDER.build();
    }
}