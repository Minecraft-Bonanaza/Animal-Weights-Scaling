package com.minecraftbonanaza.animalweightsscaling.network;

import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side copy of each tracked animal's weight. Animal Weights attachments
 * are not necessarily synced, so the server sends {@link WeightSyncPayload}.
 */
public final class ClientWeightCache {
    private static final ConcurrentHashMap<Integer, Integer> WEIGHTS = new ConcurrentHashMap<>();

    private ClientWeightCache() {
    }

    public static void put(int entityId, int weight) {
        WEIGHTS.put(entityId, weight);
    }

    public static OptionalInt get(int entityId) {
        Integer weight = WEIGHTS.get(entityId);
        return weight == null ? OptionalInt.empty() : OptionalInt.of(weight);
    }

    public static void remove(int entityId) {
        WEIGHTS.remove(entityId);
    }

    public static void clear() {
        WEIGHTS.clear();
    }
}
