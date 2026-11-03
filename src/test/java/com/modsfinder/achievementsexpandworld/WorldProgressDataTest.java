package com.modsfinder.achievementsexpandworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

class WorldProgressDataTest {
    @Test
    void newWorldStartsWithOneByOneBorder() {
        WorldProgressData progress = new WorldProgressData();

        assertEquals(1.0D, progress.getBorderSize());
        assertEquals(0, progress.getAdvancementCount());
    }

    @Test
    void everyUniqueAdvancementAddsOneBlockPerSide() {
        WorldProgressData progress = new WorldProgressData();

        assertTrue(progress.recordAdvancement("minecraft:story/root"));
        assertEquals(3.0D, progress.getBorderSize());

        assertTrue(progress.recordAdvancement("minecraft:story/mine_stone"));
        assertEquals(5.0D, progress.getBorderSize());

        assertTrue(progress.recordAdvancement("minecraft:story/upgrade_tools"));
        assertEquals(7.0D, progress.getBorderSize());
    }

    @Test
    void duplicateAdvancementDoesNotExpandWorldAgain() {
        WorldProgressData progress = new WorldProgressData();

        assertTrue(progress.recordAdvancement("minecraft:story/root"));
        assertFalse(progress.recordAdvancement("minecraft:story/root"));

        assertEquals(1, progress.getAdvancementCount());
        assertEquals(3.0D, progress.getBorderSize());
    }

    @Test
    void borderCenterIsInitializedOnlyOnce() {
        WorldProgressData progress = new WorldProgressData();

        progress.initializeCenterIfNeeded(new BlockPos(12, 64, -8));
        assertEquals(12.5D, progress.getCenterX());
        assertEquals(-7.5D, progress.getCenterZ());

        progress.initializeCenterIfNeeded(new BlockPos(100, 70, 100));
        assertEquals(12.5D, progress.getCenterX());
        assertEquals(-7.5D, progress.getCenterZ());
    }
}
