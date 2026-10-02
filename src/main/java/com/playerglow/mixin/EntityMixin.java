package com.playerglow.mixin;

import com.playerglow.PlayerGlow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(
            method = "shouldRender(D)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void playerglow$useRenderDistanceForGlow(
            double squaredDistance,
            CallbackInfoReturnable<Boolean> cir
    ) {
        Entity entity = (Entity) (Object) this;

        // Only change rendering distance for glowing players
        if (!PlayerGlow.GLOWING_PLAYERS.contains(entity.getUuid())) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        if (client == null || client.options == null) {
            return;
        }

        // Minecraft Render Distance is measured in chunks.
        // 1 chunk = 16 blocks.
        double distanceBlocks =
                client.options.getClampedViewDistance() * 16.0D;

        double maxSquaredDistance =
                distanceBlocks * distanceBlocks;

        cir.setReturnValue(
                squaredDistance < maxSquaredDistance
        );
    }
}