package com.minecraftbonanaza.animalweightsscaling.event;

import com.minecraftbonanaza.animalweightsscaling.AnimalScale;
import com.minecraftbonanaza.animalweightsscaling.AnimalWeightsScaling;
import com.minecraftbonanaza.animalweightsscaling.compat.AnimalWeightsAccess;
import com.minecraftbonanaza.animalweightsscaling.network.ClientWeightCache;
import com.minecraftbonanaza.animalweightsscaling.network.WeightSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.OptionalInt;

@EventBusSubscriber(modid = AnimalWeightsScaling.MOD_ID)
public final class WeightSyncEvents {
    private static final String LAST_SENT_KEY = AnimalWeightsScaling.MOD_ID + ":last_sent_weight";

    private WeightSyncEvents() {
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        sendWeight(player, event.getTarget());
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Animal animal) || !AnimalScale.shouldScale(animal)) {
            return;
        }
        OptionalInt weight = AnimalWeightsAccess.getWeight(animal);
        if (weight.isEmpty()) {
            return;
        }
        animal.getPersistentData().putInt(LAST_SENT_KEY, weight.getAsInt());
        PacketDistributor.sendToPlayersTrackingEntity(animal, new WeightSyncPayload(animal.getId(), weight.getAsInt()));
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide() || !(entity instanceof Animal animal) || !AnimalScale.shouldScale(animal)) {
            return;
        }
        OptionalInt weight = AnimalWeightsAccess.getWeight(animal);
        if (weight.isEmpty()) {
            return;
        }
        int current = weight.getAsInt();
        int lastSent = animal.getPersistentData().getInt(LAST_SENT_KEY);
        if (animal.getPersistentData().contains(LAST_SENT_KEY) && lastSent == current) {
            return;
        }
        animal.getPersistentData().putInt(LAST_SENT_KEY, current);
        PacketDistributor.sendToPlayersTrackingEntity(animal, new WeightSyncPayload(animal.getId(), current));
    }

    @SubscribeEvent
    public static void onLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            ClientWeightCache.remove(event.getEntity().getId());
        }
    }

    private static void sendWeight(ServerPlayer player, Entity target) {
        if (!(target instanceof Animal animal) || !AnimalScale.shouldScale(animal)) {
            return;
        }
        OptionalInt weight = AnimalWeightsAccess.getWeight(animal);
        if (weight.isEmpty()) {
            return;
        }
        PacketDistributor.sendToPlayer(player, new WeightSyncPayload(animal.getId(), weight.getAsInt()));
    }
}
