package com.modsfinder.achievementsexpandworld;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class WorldProgressData extends SavedData {
    private static final Codec<WorldProgressData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("initialized", false)
                            .forGetter(data -> data.initialized),
                    Codec.DOUBLE.optionalFieldOf("center_x", 0.5D)
                            .forGetter(data -> data.centerX),
                    Codec.DOUBLE.optionalFieldOf("center_z", 0.5D)
                            .forGetter(data -> data.centerZ),
                    Codec.STRING.listOf()
                            .optionalFieldOf("counted_advancements", List.of())
                            .forGetter(data -> new ArrayList<>(data.countedAdvancements))
            ).apply(instance, WorldProgressData::new)
    );

    private static final SavedDataType<WorldProgressData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(AchievementsExpandYourWorld.MOD_ID, "world_progress"),
            WorldProgressData::new,
            CODEC,
            null
    );

    private boolean initialized;
    private double centerX = 0.5D;
    private double centerZ = 0.5D;
    private final Set<String> countedAdvancements = new HashSet<>();

    public WorldProgressData() {
    }

    private WorldProgressData(
            boolean initialized,
            double centerX,
            double centerZ,
            List<String> countedAdvancements
    ) {
        this.initialized = initialized;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.countedAdvancements.addAll(countedAdvancements);
    }

    public static WorldProgressData get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public void initializeCenterIfNeeded(BlockPos spawnPosition) {
        if (initialized) {
            return;
        }

        centerX = spawnPosition.getX() + 0.5D;
        centerZ = spawnPosition.getZ() + 0.5D;
        initialized = true;
        setDirty();
    }

    public boolean recordAdvancement(String advancementId) {
        if (!WorldExpansionRules.recordUniqueAdvancement(countedAdvancements, advancementId)) {
            return false;
        }

        setDirty();
        return true;
    }

    public int getAdvancementCount() {
        return countedAdvancements.size();
    }

    public double getBorderSize() {
        // Vanilla World Border's maximum side length is 59,999,968 blocks.
        return WorldExpansionRules.borderSizeForCount(getAdvancementCount());
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterZ() {
        return centerZ;
    }
}
