package com.minecraftbonanaza.animalweightsscaling;

import com.minecraftbonanaza.animalweightsscaling.network.WeightSyncPayload;
import com.minecraftbonanaza.animalweightsscaling.network.WeightSyncPayloadHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(AnimalWeightsScaling.MOD_ID)
public class AnimalWeightsScaling {
    public static final String MOD_ID = "animalweightsscaling";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AnimalWeightsScaling(IEventBus modEventBus) {
        modEventBus.addListener(AnimalWeightsScaling::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                WeightSyncPayload.TYPE,
                WeightSyncPayload.STREAM_CODEC,
                WeightSyncPayloadHandler::handle);
    }
}
