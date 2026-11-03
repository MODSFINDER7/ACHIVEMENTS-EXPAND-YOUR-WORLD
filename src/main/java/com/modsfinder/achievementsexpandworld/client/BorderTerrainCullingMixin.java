package com.modsfinder.achievementsexpandworld.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps render sections and frustum-tested objects outside the active world
 * border out of the client's visible set. The border is read live every time,
 * so newly expanded terrain becomes visible as soon as the server's border
 * update reaches the client.
 */
@Mixin(Frustum.class)
public abstract class BorderTerrainCullingMixin {
    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void achievementsexpandworld$cullOutsideBorder(
            AABB bounds,
            CallbackInfoReturnable<Boolean> callback
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        WorldBorder border = minecraft.level.getWorldBorder();
        if (bounds.maxX < border.getMinX()
                || bounds.minX > border.getMaxX()
                || bounds.maxZ < border.getMinZ()
                || bounds.minZ > border.getMaxZ()) {
            callback.setReturnValue(false);
        }
    }
}
