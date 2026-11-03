package com.modsfinder.achievementsexpandworld;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.border.WorldBorder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AchievementsExpandYourWorld.MOD_ID)
public final class AchievementsExpandYourWorld {
    public static final String MOD_ID = "achievementsexpandworld";
    private static final long BORDER_EXPANSION_TICKS = 40L;

    public AchievementsExpandYourWorld() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        WorldProgressData progress = WorldProgressData.get(server);

        progress.initializeCenterIfNeeded(server.overworld().getSharedSpawnPos());
        applyBorder(server, progress, false);
    }

    @SubscribeEvent
    public void onAdvancementEarned(AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        String advancementId = event.getAdvancement().id().toString();
        int namespaceSeparator = advancementId.indexOf(':');
        String path = namespaceSeparator >= 0
                ? advancementId.substring(namespaceSeparator + 1)
                : advancementId;

        // Recipe-book unlocks are technical advancements, not challenge achievements.
        if (path.startsWith("recipes/")) {
            return;
        }

        MinecraftServer server = player.serverLevel().getServer();
        WorldProgressData progress = WorldProgressData.get(server);

        // The same advancement ID can only expand the shared world once.
        if (!progress.recordAdvancement(advancementId)) {
            return;
        }

        applyBorder(server, progress, true);

        int size = (int) progress.getBorderSize();
        Component message = Component.literal(
                "World expanded to " + size + " x " + size
                        + " blocks! (" + progress.getAdvancementCount() + " advancements)"
        );

        for (ServerPlayer onlinePlayer : server.getPlayerList().getPlayers()) {
            onlinePlayer.sendSystemMessage(message);
        }
    }

    private static void applyBorder(
            MinecraftServer server,
            WorldProgressData progress,
            boolean animate
    ) {
        double targetSize = progress.getBorderSize();

        for (ServerLevel level : server.getAllLevels()) {
            WorldBorder border = level.getWorldBorder();
            border.setCenter(progress.getCenterX(), progress.getCenterZ());
            border.setWarningBlocks(0);

            if (animate) {
                border.lerpSizeBetween(
                        border.getSize(),
                        targetSize,
                        BORDER_EXPANSION_TICKS,
                        level.getGameTime()
                );
            } else {
                border.setSize(targetSize);
            }
        }
    }
}
