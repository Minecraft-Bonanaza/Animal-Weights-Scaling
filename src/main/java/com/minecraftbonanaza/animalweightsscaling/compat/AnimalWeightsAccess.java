package com.minecraftbonanaza.animalweightsscaling.compat;

import com.minecraftbonanaza.animalweightsscaling.AnimalWeightsScaling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.OptionalInt;

/**
 * Reads Animal Weights values without a hard compile-time package dependency.
 *
 * Prefers the public {@code WeightAttachment.getWeight(animal)} API, then
 * {@code AnimalWeightAttachment.get}/{@code getWeight}, then the
 * {@code animalweights:weight} attachment if present.
 */
public final class AnimalWeightsAccess {
    private static final ResourceLocation WEIGHT_ATTACHMENT_ID =
            ResourceLocation.fromNamespaceAndPath("animalweights", "weight");

    private static volatile Lookup lookup;

    private AnimalWeightsAccess() {
    }

    private static Lookup lookup() {
        Lookup current = lookup;
        if (current == null) {
            synchronized (AnimalWeightsAccess.class) {
                current = lookup;
                if (current == null) {
                    current = Lookup.resolve();
                    lookup = current;
                }
            }
        }
        return current;
    }

    public static boolean isTracked(Entity entity) {
        MethodHandle isTracked = lookup().isTracked();
        if (isTracked != null) {
            try {
                return (boolean) isTracked.invoke(entity);
            } catch (Throwable exception) {
                AnimalWeightsScaling.LOGGER.debug("Animal Weights isTracked lookup failed", exception);
            }
        }
        return entity instanceof Animal animal && isVanillaFarmAnimal(animal);
    }

    public static OptionalInt getWeight(LivingEntity entity) {
        MethodHandle getWeight = lookup().getWeight();
        if (getWeight != null) {
            try {
                int weight = (int) getWeight.invoke(entity);
                return OptionalInt.of(clamp(weight));
            } catch (Throwable exception) {
                AnimalWeightsScaling.LOGGER.debug("Animal Weights getWeight lookup failed", exception);
            }
        }
        return fromAttachment(entity);
    }

    private static OptionalInt fromAttachment(Entity entity) {
        AttachmentType<?> type = NeoForgeRegistries.ATTACHMENT_TYPES.get(WEIGHT_ATTACHMENT_ID);
        if (type == null || !entity.hasData(type)) {
            return OptionalInt.empty();
        }
        Object value = entity.getData(type);
        if (value instanceof Integer weight) {
            return OptionalInt.of(clamp(weight));
        }
        return OptionalInt.empty();
    }

    private static int clamp(int weight) {
        return Math.max(0, Math.min(8, weight));
    }

    private static boolean isVanillaFarmAnimal(Animal animal) {
        return animal instanceof Cow
                || animal instanceof MushroomCow
                || animal instanceof Pig
                || animal instanceof Sheep
                || animal instanceof Chicken
                || animal instanceof Rabbit;
    }

    private record Lookup(MethodHandle getWeight, MethodHandle isTracked) {
        private static Lookup resolve() {
            MethodHandles.Lookup handles = MethodHandles.publicLookup();
            MethodHandle getWeight = null;
            MethodHandle isTracked = null;

            for (ModFileScanData scanData : ModList.get().getAllScanData()) {
                for (ModFileScanData.ClassData classData : scanData.getClasses()) {
                    String className = classData.clazz().getClassName();
                    if (!isWeightClass(className)) {
                        continue;
                    }
                    try {
                        Class<?> type = Class.forName(className);
                        if (getWeight == null) {
                            getWeight = findStaticIntMethod(handles, type, "getWeight", "get");
                        }
                        if (isTracked == null) {
                            isTracked = findStaticBooleanMethod(handles, type, "isTracked");
                        }
                    } catch (ClassNotFoundException | LinkageError exception) {
                        AnimalWeightsScaling.LOGGER.debug("Could not load {}", className, exception);
                    }
                }
            }

            if (getWeight != null) {
                AnimalWeightsScaling.LOGGER.info("Bound Animal Weights weight accessor");
            } else {
                AnimalWeightsScaling.LOGGER.warn(
                        "Could not find WeightAttachment.getWeight; falling back to the animalweights:weight attachment");
            }
            return new Lookup(getWeight, isTracked);
        }

        private static boolean isWeightClass(String className) {
            int lastDot = className.lastIndexOf('.');
            String simpleName = lastDot >= 0 ? className.substring(lastDot + 1) : className;
            return simpleName.equals("WeightAttachment") || simpleName.equals("AnimalWeightAttachment");
        }

        private static MethodHandle findStaticIntMethod(
                MethodHandles.Lookup handles, Class<?> type, String... names) {
            Class<?>[] parameterTypes = {Animal.class, LivingEntity.class, Entity.class};
            for (String name : names) {
                for (Class<?> parameterType : parameterTypes) {
                    try {
                        Method method = type.getMethod(name, parameterType);
                        if (Modifier.isStatic(method.getModifiers()) && method.getReturnType() == int.class) {
                            AnimalWeightsScaling.LOGGER.info(
                                    "Bound {}.{}({})", type.getName(), name, parameterType.getSimpleName());
                            return handles.unreflect(method);
                        }
                    } catch (NoSuchMethodException | IllegalAccessException ignored) {
                        // try the next signature
                    }
                }
            }
            return null;
        }

        private static MethodHandle findStaticBooleanMethod(
                MethodHandles.Lookup handles, Class<?> type, String name) {
            Class<?>[] parameterTypes = {Entity.class, LivingEntity.class, Animal.class};
            for (Class<?> parameterType : parameterTypes) {
                try {
                    Method method = type.getMethod(name, parameterType);
                    if (Modifier.isStatic(method.getModifiers()) && method.getReturnType() == boolean.class) {
                        return handles.unreflect(method);
                    }
                } catch (NoSuchMethodException | IllegalAccessException ignored) {
                    // try the next signature
                }
            }
            return null;
        }
    }
}
