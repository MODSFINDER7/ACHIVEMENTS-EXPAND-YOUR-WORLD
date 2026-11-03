package com.modsfinder.achievementsexpandworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class WorldExpansionRulesTest {
    @Test
    void advancementFramesAwardTheConfiguredPoints() {
        assertEquals(1, WorldExpansionRules.rewardFor(false, "TASK"));
        assertEquals(10, WorldExpansionRules.rewardFor(false, "GOAL"));
        assertEquals(50, WorldExpansionRules.rewardFor(false, "CHALLENGE"));
        assertEquals(100, WorldExpansionRules.rewardFor(true, "TASK"));
        assertEquals(100, WorldExpansionRules.rewardFor(true, "GOAL"));
        assertEquals(100, WorldExpansionRules.rewardFor(true, "CHALLENGE"));
    }

    @Test
    void borderStartsAtOneAndGrowsByTwoPerPoint() {
        assertEquals(1.0D, WorldExpansionRules.borderSizeForPoints(0));
        assertEquals(3.0D, WorldExpansionRules.borderSizeForPoints(1));
        assertEquals(21.0D, WorldExpansionRules.borderSizeForPoints(10));
        assertEquals(101.0D, WorldExpansionRules.borderSizeForPoints(50));
        assertEquals(201.0D, WorldExpansionRules.borderSizeForPoints(100));
    }

    @Test
    void negativePointsCannotProduceInvalidBorderSizes() {
        assertEquals(1.0D, WorldExpansionRules.borderSizeForPoints(-5));
    }

    @Test
    void borderSizeNeverExceedsVanillaMaximum() {
        assertEquals(
                WorldExpansionRules.MAX_BORDER_SIZE,
                WorldExpansionRules.borderSizeForPoints(Integer.MAX_VALUE)
        );
    }

    @Test
    void recipeUnlockAdvancementsAreExcluded() {
        assertFalse(WorldExpansionRules.isCountableAdvancement("minecraft:recipes/stone_pickaxe"));
        assertFalse(WorldExpansionRules.isCountableAdvancement("modid:recipes/custom_item"));
        assertTrue(WorldExpansionRules.isCountableAdvancement("minecraft:story/root"));
        assertTrue(WorldExpansionRules.isCountableAdvancement("modid:custom_advancement"));
    }

    @Test
    void eachAdvancementIdCanOnlyBeCountedOnce() {
        Set<String> counted = new HashSet<>();

        assertTrue(WorldExpansionRules.recordUniqueAdvancement(counted, "minecraft:story/root"));
        assertFalse(WorldExpansionRules.recordUniqueAdvancement(counted, "minecraft:story/root"));
        assertEquals(1, counted.size());
    }
}
