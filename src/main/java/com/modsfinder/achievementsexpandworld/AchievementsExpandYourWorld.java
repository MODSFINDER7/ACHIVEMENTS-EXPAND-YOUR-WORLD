package com.modsfinder.achievementsexpandworld;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.border.WorldBorder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

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

        progress.initializeCenterIfNeeded(server.overworld().getRespawnData().pos());
        applyBorder(server, progress, false);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        WorldProgressData progress = WorldProgressData.get(player.serverLevel().getServer());
        player.sendSystemMessage(createStatusMessage(progress));
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

        // Recipe-book unlocks are background mechanics, not challenge advancements.
        if (path.startsWith("recipes/")) {
            return;
        }

        MinecraftServer server = player.serverLevel().getServer();
        WorldProgressData progress = WorldProgressData.get(server);

        // Shared world progression: each advancement ID counts only once for the whole server.
        if (!progress.recordAdvancement(advancementId)) {
            return;
        }

        applyBorder(server, progress, true);

        Component message = Component.literal(
                "World expanded to " + formatSize(progress.getBorderSize())
                        + "! (" + progress.getAdvancementCount() + " unique advancements)"
        );

        for (ServerPlayer onlinePlayer : server.getPlayerList().getPlayers()) {
            onlinePlayer.sendSystemMessage(message);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("aew")
                        .then(Commands.literal("status")
                                .executes(context -> {
                                    WorldProgressData progress =
                                            WorldProgressData.get(context.getSource().getServer());
                                    context.getSource().sendSuccess(
                                            () -> createStatusMessage(progress),
                                            false
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );
    }

    private static Component createStatusMessage(WorldProgressData progress) {
        return Component.literal(
                "Achievements Expand Your World | Border: "
                        + formatSize(progress.getBorderSize())
                        + " | Unique advancements: " + progress.getAdvancementCount()
        );
    }

    private static String formatSize(double size) {
        long rounded = Math.round(size);
        return rounded + " x " + rounded + " blocks";
    }

    private static void applyBorder(
            MinecraftServer server,
            WorldProgressData progress,
            boolean animate
    ) {
        double targetSize = progress.getBorderSize();

        // Each dimension receives the same saved center and size. Vanilla border packets
        // synchronize each dimension's border with the players in that dimension.
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
