package com.cutexxgirl.takeit.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * PacketHandler - Network packet registration
 *
 * This class manages all network communication between the mod's
 * client and server.
 */
public class PacketHandler {

    /**
     * Protocol version.
     */
    private static final String PROTOCOL_VERSION = "1";

    /**
     * Main network channel.
     */
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("takeit", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    /**
     * Register all network packets.
     */
    public static void register() {

        int id = 0;

        /*
         * Client -> Server:
         * Radius pickup.
         */
        INSTANCE.registerMessage(
                id++,
                PacketPickup.class,
                PacketPickup::encode,
                PacketPickup::decode,
                PacketPickup::handle
        );

        /*
         * Client -> Server:
         * Single-item pickup.
         */
        INSTANCE.registerMessage(
                id++,
                PacketClickPickup.class,
                PacketClickPickup::encode,
                PacketClickPickup::decode,
                PacketClickPickup::handle
        );

        /*
         * Server -> Client:
         * Synchronize the current ItemStack of an ItemEntity.
         */
        INSTANCE.registerMessage(
                id++,
                SyncItemEntityPacket.class,
                SyncItemEntityPacket::encode,
                SyncItemEntityPacket::decode,
                SyncItemEntityPacket::handle
        );
    }
}