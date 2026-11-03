package com.modsfinder.achievementsexpandworld;

import com.mojang.brigadier.Command;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
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

        WorldProgressData progress = WorldProgressData.get(((ServerLevel) player.level()).getServer());
        player.sendSystemMessage(createStatusMessage(progress));
    }

    @SubscribeEvent
    public void onAdvancementEarned(AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        String advancementId = event.getAdvancement().id().toString();
        if (!WorldExpansionRules.isCountableAdvancement(advancementId)) {
            return;
        }

        Advancement advancement = event.getAdvancement().value();
        DisplayInfo display = advancement.display().orElse(null);
        boolean hidden = display != null && display.isHidden();
        String frame = display == null ? "TASK" : display.getType().name();
        int rewardPoints = WorldExpansionRules.rewardFor(hidden, frame);

        MinecraftServer server = ((ServerLevel) player.level()).getServer();
        WorldProgressData progress = WorldProgressData.get(server);

        // A given advancement ID awards points only once across the entire server.
        if (!progress.recordAdvancement(advancementId, rewardPoints)) {
            return;
        }

        applyBorder(server, progress, true);

        Component message = Component.literal(
                "World expanded to " + formatSize(progress.getBorderSize())
                        + "! +" + rewardPoints + " points ("
                        + progress.getTotalPoints() + " total points, "
                        + progress.getAdvancementCount() + " unique advancements)"
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
                        + " | Points: " + progress.getTotalPoints()
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
