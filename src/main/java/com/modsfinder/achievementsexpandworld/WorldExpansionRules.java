package com.modsfinder.achievementsexpandworld;

import java.util.Set;

/**
 * Pure progression rules shared by the server implementation and automated tests.
 */
public final class WorldExpansionRules {
    public static final double MAX_BORDER_SIZE = 59_999_968.0D;

    private WorldExpansionRules() {
    }

    public static double borderSizeForCount(int advancementCount) {
        int safeCount = Math.max(0, advancementCount);
        return Math.min(MAX_BORDER_SIZE, 1.0D + (2.0D * safeCount));
    }

    public static boolean isCountableAdvancement(String advancementId) {
        int namespaceSeparator = advancementId.indexOf(':');
        String path = namespaceSeparator >= 0
                ? advancementId.substring(namespaceSeparator + 1)
                : advancementId;
        return !path.startsWith("recipes/");
    }

    public static boolean recordUniqueAdvancement(Set<String> countedAdvancements, String advancementId) {
        return countedAdvancements.add(advancementId);
    }
}
