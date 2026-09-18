package com.minecraftbonanaza.animalweightsscaling.mixin;

import com.minecraftbonanaza.animalweightsscaling.AnimalScale;
import com.minecraftbonanaza.animalweightsscaling.WeightScales;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "scale", at = @At("TAIL"))
    private void animalweightsscaling$scaleByWeight(
            LivingEntity entity, PoseStack poseStack, float partialTick, CallbackInfo callback) {
        WeightScales.ScaleFactors factors = AnimalScale.factorsOf(entity);
        if (factors.isIdentity()) {
            return;
        }
        poseStack.scale(factors.width(), factors.height(), factors.width());
    }
}
