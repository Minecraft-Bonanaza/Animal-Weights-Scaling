package com.minecraftbonanaza.animalweightsscaling.event;

import com.minecraftbonanaza.animalweightsscaling.AnimalWeightsScaling;
import com.minecraftbonanaza.animalweightsscaling.network.ClientWeightCache;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = AnimalWeightsScaling.MOD_ID, value = Dist.CLIENT)
public final class ClientCacheEvents {
    private ClientCacheEvents() {
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientWeightCache.clear();
    }
}
