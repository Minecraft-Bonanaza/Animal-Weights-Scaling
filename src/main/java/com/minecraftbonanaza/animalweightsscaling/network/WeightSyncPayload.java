package com.minecraftbonanaza.animalweightsscaling.network;

import com.minecraftbonanaza.animalweightsscaling.AnimalWeightsScaling;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WeightSyncPayload(int entityId, int weight) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WeightSyncPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(AnimalWeightsScaling.MOD_ID, "weight"));

    public static final StreamCodec<ByteBuf, WeightSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            WeightSyncPayload::entityId,
            ByteBufCodecs.VAR_INT,
            WeightSyncPayload::weight,
            WeightSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
