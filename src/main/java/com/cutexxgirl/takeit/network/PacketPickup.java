package com.cutexxgirl.takeit.network;

import com.cutexxgirl.takeit.TakeItConfig;
import com.cutexxgirl.takeit.events.CommonEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

/**
 * PacketPickup - Network packet for radius item pickup.
 *
 * Client -> Server communication.
 */
public class PacketPickup {

    public PacketPickup() {
    }

    public PacketPickup(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public static PacketPickup decode(FriendlyByteBuf buf) {
        return new PacketPickup(buf);
    }

    /**
     * Handle radius pickup on the server.
     */
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {

            ServerPlayer player = context.getSender();

            if (player == null || !TakeItConfig.ENABLE_MOD.get()) {
                return;
            }

            int radiusX = TakeItConfig.RADIUS_X.get();
            int radiusY = TakeItConfig.RADIUS_Y.get();
            int radiusZ = TakeItConfig.RADIUS_Z.get();

            AABB searchBox = new AABB(
                    player.getX() - radiusX,
                    player.getY() - radiusY,
                    player.getZ() - radiusZ,

                    player.getX() + radiusX,
                    player.getY() + radiusY,
                    player.getZ() + radiusZ
            );

            List<ItemEntity> items =
                    player.level().getEntitiesOfClass(
                            ItemEntity.class,
                            searchBox
                    );

            for (ItemEntity item : items) {

                if (!item.isAlive()) {
                    continue;
                }

                CommonEvents.isManualPickup.set(true);

                try {

                    /*
                     * Stable pickup logic:
                     * playerTouch() is called exactly once.
                     */
                    item.setPickUpDelay(0);
                    item.playerTouch(player);

                    /*
                     * Synchronize the current ItemStack with clients.
                     */
                    PacketHandler.INSTANCE.send(
                            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(
                                    () -> item
                            ),
                            new SyncItemEntityPacket(
                                    item.getId(),
                                    item.getItem()
                            )
                    );

                    /*
                     * Show the full-inventory message only if
                     * the feature is enabled in the config.
                     */
                    if (TakeItConfig.ENABLE_FULL_MESSAGE.get()
                            && item.isAlive()
                            && !item.getItem().isEmpty()) {

                        player.displayClientMessage(
                                Component.literal("Inventory is Full"),
                                true
                        );
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    CommonEvents.isManualPickup.set(false);
                }
            }
        });

        context.setPacketHandled(true);
    }
}