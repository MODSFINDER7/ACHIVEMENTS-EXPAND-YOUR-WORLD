package com.modsfinder.achievementsexpandworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class WorldExpansionRulesTest {
    @Test
    void borderStartsAtOneAndGrowsByTwoPerUniqueAdvancement() {
        assertEquals(1.0D, WorldExpansionRules.borderSizeForCount(0));
        assertEquals(3.0D, WorldExpansionRules.borderSizeForCount(1));
        assertEquals(5.0D, WorldExpansionRules.borderSizeForCount(2));
        assertEquals(7.0D, WorldExpansionRules.borderSizeForCount(3));
    }

    @Test
    void negativeCountsCannotProduceInvalidBorderSizes() {
        assertEquals(1.0D, WorldExpansionRules.borderSizeForCount(-5));
    }

    @Test
    void borderSizeNeverExceedsVanillaMaximum() {
        assertEquals(
                WorldExpansionRules.MAX_BORDER_SIZE,
                WorldExpansionRules.borderSizeForCount(Integer.MAX_VALUE)
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
