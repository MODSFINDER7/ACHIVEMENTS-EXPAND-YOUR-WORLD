package com.modsfinder.achievementsexpandworld.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Filters the section compiler's world snapshot at block-position granularity.
 * A block is supplied to the mesher only when its complete 1x1 footprint is
 * inside the live border. Fluids and block entities use the same rule.
 */
@Mixin(RenderSectionRegion.class)
public abstract class BorderTerrainCullingMixin {
    private static boolean achievementsexpandworld$isInside(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return true;
        }

        WorldBorder border = minecraft.level.getWorldBorder();
        return pos.getX() >= border.getMinX()
                && pos.getX() + 1.0D <= border.getMaxX()
                && pos.getZ() >= border.getMinZ()
                && pos.getZ() + 1.0D <= border.getMaxZ();
    }

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
    private void achievementsexpandworld$hideOutsideBlocks(
            BlockPos pos,
            CallbackInfoReturnable<BlockState> callback
    ) {
        if (!achievementsexpandworld$isInside(pos)) {
            callback.setReturnValue(Blocks.AIR.defaultBlockState());
        }
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void achievementsexpandworld$hideOutsideFluids(
            BlockPos pos,
            CallbackInfoReturnable<FluidState> callback
    ) {
        if (!achievementsexpandworld$isInside(pos)) {
            callback.setReturnValue(Fluids.EMPTY.defaultFluidState());
        }
    }

    @Inject(method = "getBlockEntity", at = @At("HEAD"), cancellable = true)
    private void achievementsexpandworld$hideOutsideBlockEntities(
            BlockPos pos,
            CallbackInfoReturnable<BlockEntity> callback
    ) {
        if (!achievementsexpandworld$isInside(pos)) {
            callback.setReturnValue(null);
        }
    }
}
