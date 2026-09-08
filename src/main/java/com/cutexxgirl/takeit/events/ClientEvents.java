package com.cutexxgirl.takeit.events;

import com.cutexxgirl.takeit.TakeIt;
import com.cutexxgirl.takeit.TakeItConfig;
import com.cutexxgirl.takeit.network.PacketClickPickup;
import com.cutexxgirl.takeit.network.PacketHandler;
import com.cutexxgirl.takeit.network.PacketPickup;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * ClientEvents - Client-side only event handlers
 *
 * This class handles events that only occur on the client:
 * - Registers the pickup keybindings
 * - Detects when the pickup key is pressed
 * - Detects when right mouse button is pressed on an item
 * - Sends packets to the server to trigger pickup
 */
@Mod.EventBusSubscriber(
        modid = TakeIt.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ClientEvents {

    /**
     * Keybinding for radius pickup.
     * Default key: G
     */
    public static final KeyMapping PICKUP_KEY = new KeyMapping(
            "key.takeit.pickup",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.takeit"
    );

    /**
     * Keybinding for picking up the specific item being looked at.
     * Default key: Right Click
     */
    public static final KeyMapping PICKUP_ITEM_KEY = new KeyMapping(
            "key.takeit.pickupitem",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_2,
            "key.categories.takeit"
    );

    /**
     * Register keybindings.
     */
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(PICKUP_KEY);
        event.register(PICKUP_ITEM_KEY);
    }

    /**
     * Client-side tick events.
     */
    @Mod.EventBusSubscriber(
            modid = TakeIt.MOD_ID,
            value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.FORGE
    )
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null || mc.screen != null) {
                return;
            }

            if (!TakeItConfig.ENABLE_MOD.get()) {
                return;
            }

            /*
             * Radius pickup:
             *
             * consumeClick() is intentional.
             * One key press = one packet.
             *
             * This prevents holding the key from repeatedly sending
             * pickup packets every few ticks.
             */
            if (PICKUP_KEY.isDown() && mc.player.tickCount % 5 == 0) {
                PacketHandler.INSTANCE.sendToServer(new PacketPickup());
            }

            /*
             * Single-item pickup:
             *
             * Find the ItemEntity the player is looking at and
             * tell the server which entity should be picked up.
             */
            if (PICKUP_ITEM_KEY.consumeClick()) {

                HitResult hit = pick(mc.player, 20.0F);

                if (hit != null && hit.getType() == HitResult.Type.ENTITY) {

                    EntityHitResult entityHit = (EntityHitResult) hit;

                    if (entityHit.getEntity() instanceof ItemEntity) {

                        int entityId = entityHit.getEntity().getId();

                        PacketHandler.INSTANCE.sendToServer(
                                new PacketClickPickup(entityId)
                        );

                        LogUtils.getLogger().info(
                                "TakeIt: Sent click pickup packet for entity ID: {}",
                                entityId
                        );
                    }
                }
            }
        }
    }

    /**
     * Custom raycast used to find ItemEntities the player is looking at.
     *
     * Minecraft's normal interaction raycast does not target ItemEntity,
     * so Take It performs its own entity raycast.
     *
     * @param player player performing the raycast
     * @param partialTicks interpolation value
     * @return hit result, or null if nothing was found
     */
    private static HitResult pick(
            net.minecraft.world.entity.player.Player player,
            float partialTicks
    ) {
        /*
         * Use Forge's block reach attribute.
         *
         * The current client-side raycast can reach up to the player's
         * configured interaction distance.
         */
        double reach = player.getAttributeValue(
                net.minecraftforge.common.ForgeMod.BLOCK_REACH.get()
        );

        if (reach == 0) {
            reach = 4.5D;
        }

        net.minecraft.world.phys.Vec3 eyePos =
                player.getEyePosition(partialTicks);

        net.minecraft.world.phys.Vec3 viewVec =
                player.getViewVector(partialTicks);

        net.minecraft.world.phys.Vec3 endPos = eyePos.add(
                viewVec.x * reach,
                viewVec.y * reach,
                viewVec.z * reach
        );

        /*
         * Slightly enlarge the SEARCH AREA.
         *
         * This does NOT change the real ItemEntity hitbox.
         * It only makes it easier for the custom raycast to find items.
         */
        net.minecraft.world.phys.AABB searchBox =
                player.getBoundingBox()
                        .expandTowards(viewVec.scale(reach))
                        .inflate(1.0D, 1.0D, 1.0D);

        return net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                player,
                eyePos,
                endPos,
                searchBox,
                entity -> entity instanceof ItemEntity,
                reach * reach
        );
    }
}