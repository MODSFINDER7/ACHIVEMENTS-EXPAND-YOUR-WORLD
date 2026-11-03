package com.modsfinder.achievementsexpandworld.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Rebuilds chunk meshes only when the set of fully unlocked block columns
 * changes, rather than once per animation tick.
 */
@EventBusSubscriber(modid = "achievementsexpandworld", value = Dist.CLIENT)
public final class BorderMeshRefreshHandler {
    private static ClientLevel lastLevel;
    private static long lastMinX;
    private static long lastMaxX;
    private static long lastMinZ;
    private static long lastMaxZ;
    private static boolean initialized;

    private BorderMeshRefreshHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            initialized = false;
            lastLevel = null;
            return;
        }

        WorldBorder border = level.getWorldBorder();
        long minX = (long) Math.ceil(border.getMinX());
        long maxX = (long) Math.floor(border.getMaxX());
        long minZ = (long) Math.ceil(border.getMinZ());
        long maxZ = (long) Math.floor(border.getMaxZ());

        if (initialized && lastLevel == level
                && lastMinX == minX && lastMaxX == maxX
                && lastMinZ == minZ && lastMaxZ == maxZ) {
            return;
        }

        initialized = true;
        lastLevel = level;
        lastMinX = minX;
        lastMaxX = maxX;
        lastMinZ = minZ;
        lastMaxZ = maxZ;

        minecraft.levelRenderer.invalidateCompiledGeometry(
                level,
                minecraft.options,
                minecraft.gameRenderer.mainCamera(),
                minecraft.getBlockColors()
        );
    }
}
