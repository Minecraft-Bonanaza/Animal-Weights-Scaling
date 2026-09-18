package com.minecraftbonanaza.animalweightsscaling.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class WeightSyncPayloadHandler {
    private WeightSyncPayloadHandler() {
    }

    public static void handle(WeightSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientWeightCache.put(payload.entityId(), payload.weight()));
    }
}
