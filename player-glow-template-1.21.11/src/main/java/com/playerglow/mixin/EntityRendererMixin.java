package com.playerglow.mixin;

import com.playerglow.PlayerGlow;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(
            method = "updateRenderState",
            at = @At("TAIL")
    )
    private void playerglow$addGlow(
            Entity entity,
            EntityRenderState state,
            float tickProgress,
            CallbackInfo ci
    ) {
        if (PlayerGlow.GLOWING_PLAYERS.contains(entity.getUuid())) {
            state.outlineColor = 0xFF000000;
        }
    }
}