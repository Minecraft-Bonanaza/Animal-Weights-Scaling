package com.minecraftbonanaza.animalweightsscaling;

import com.minecraftbonanaza.animalweightsscaling.compat.AnimalWeightsAccess;
import com.minecraftbonanaza.animalweightsscaling.network.ClientWeightCache;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;

import java.util.OptionalInt;

/**
 * Resolves the current weight and matching scale factors for an animal.
 */
public final class AnimalScale {
    private AnimalScale() {
    }

    public static boolean shouldScale(LivingEntity entity) {
        return entity instanceof Animal && AnimalWeightsAccess.isTracked(entity);
    }

    public static OptionalInt weightOf(LivingEntity entity) {
        if (!shouldScale(entity)) {
            return OptionalInt.empty();
        }
        if (entity.level().isClientSide()) {
            OptionalInt cached = ClientWeightCache.get(entity.getId());
            if (cached.isPresent()) {
                return cached;
            }
        }
        return AnimalWeightsAccess.getWeight(entity);
    }

    public static WeightScales.ScaleFactors factorsOf(LivingEntity entity) {
        OptionalInt weight = weightOf(entity);
        if (weight.isEmpty()) {
            return WeightScales.ScaleFactors.IDENTITY;
        }
        return WeightScales.forWeight(weight.getAsInt());
    }
}
