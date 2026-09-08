package com.cutexxgirl.takeit.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * SyncItemEntityPacket
 *
 * Server -> Client packet used to synchronize the current ItemStack
 * of an ItemEntity after Take It manually changes that entity.
 *
 * This is intentionally separate from the pickup packets.
 */
public class SyncItemEntityPacket {

    private final int entityId;
    private final ItemStack itemStack;

    /**
     * Creates a sync packet.
     *
     * @param entityId the ItemEntity ID
     * @param itemStack the current server-side ItemStack
     */
    public SyncItemEntityPacket(int entityId, ItemStack itemStack) {
        this.entityId = entityId;
        this.itemStack = itemStack.copy();
    }

    /**
     * Decodes the packet from the network buffer.
     */
    public SyncItemEntityPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.itemStack = buf.readItem();
    }

    /**
     * Encodes the packet.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeItem(itemStack);
    }

    /**
     * Decodes the packet.
     */
    public static SyncItemEntityPacket decode(FriendlyByteBuf buf) {
        return new SyncItemEntityPacket(buf);
    }

    /**
     * Handles the packet on the client.
     */
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {

            Minecraft mc = Minecraft.getInstance();

            if (mc.level == null) {
                return;
            }

            Entity entity = mc.level.getEntity(entityId);

            if (!(entity instanceof ItemEntity itemEntity)) {
                return;
            }

            /*
             * Apply a fresh copy locally.
             *
             * This updates the client-side ItemEntity data without
             * modifying the server-side pickup logic.
             */
            itemEntity.setItem(itemStack.copy());
        });

        context.setPacketHandled(true);
    }
}