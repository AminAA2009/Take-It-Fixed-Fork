package com.cutexxgirl.takeit.network;

import com.cutexxgirl.takeit.TakeItConfig;
import com.cutexxgirl.takeit.events.CommonEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * PacketClickPickup - Network packet for right-click item pickup.
 *
 * Client -> Server communication.
 */
public class PacketClickPickup {

    private final int entityId;

    public PacketClickPickup(int entityId) {
        this.entityId = entityId;
    }

    public PacketClickPickup(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public static PacketClickPickup decode(FriendlyByteBuf buf) {
        return new PacketClickPickup(buf);
    }

    /**
     * Handle the packet on the server.
     */
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {

            ServerPlayer player = context.getSender();

            if (player == null || !TakeItConfig.ENABLE_MOD.get()) {
                return;
            }

            LogUtils.getLogger().info(
                    "TakeIt: Server received click pickup packet for entity ID: {}",
                    entityId
            );

            Entity target = player.level().getEntity(entityId);

            LogUtils.getLogger().info(
                    "TakeIt: Found entity: {}",
                    target
            );

            if (!(target instanceof ItemEntity itemEntity)) {
                return;
            }

            double distSq = player.distanceToSqr(itemEntity);

            LogUtils.getLogger().info(
                    "TakeIt: Distance squared: {}",
                    distSq
            );

            /*
             * Server-side reach validation.
             */
            if (distSq >= 36.0D) {
                return;
            }

            CommonEvents.isManualPickup.set(true);

            try {

                /*
                 * Stable pickup logic:
                 * playerTouch() is called exactly once.
                 */
                itemEntity.setPickUpDelay(0);
                itemEntity.playerTouch(player);

                LogUtils.getLogger().info(
                        "TakeIt: Successfully triggered playerTouch"
                );

                /*
                 * Synchronize the current ItemStack with clients.
                 */
                PacketHandler.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(
                                () -> itemEntity
                        ),
                        new SyncItemEntityPacket(
                                itemEntity.getId(),
                                itemEntity.getItem()
                        )
                );

                /*
                 * Show the full-inventory message only if
                 * the feature is enabled in the config.
                 */
                if (TakeItConfig.ENABLE_FULL_MESSAGE.get()
                        && itemEntity.isAlive()
                        && !itemEntity.getItem().isEmpty()) {

                    player.displayClientMessage(
                            Component.literal("Inventory is Full"),
                            true
                    );
                }

            } catch (Exception e) {

                LogUtils.getLogger().error(
                        "TakeIt: Error during pickup",
                        e
                );

            } finally {

                CommonEvents.isManualPickup.set(false);
            }
        });

        context.setPacketHandled(true);
    }
}