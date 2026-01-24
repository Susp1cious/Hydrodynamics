package com.example.exampleplugin.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.jspecify.annotations.Nullable;

public class FlowRateComponent implements Component<ChunkStore> {

    private float maxFlowRate = 100f;
    private float currentFlowRate = 0f;

    public static final BuilderCodec<FlowRateComponent> CODEC =
            BuilderCodec.builder(FlowRateComponent.class, FlowRateComponent::new)
                    .append(new KeyedCodec<>("MaxFlowRate", Codec.FLOAT),
                            (c, v) -> c.maxFlowRate = v, c -> c.maxFlowRate)
                    .add()
                    .append(new KeyedCodec<>("CurrentFlowRate", Codec.FLOAT),
                            (c, v) -> c.currentFlowRate = v, c -> c.currentFlowRate)
                    .add()
                    .build();

    public FlowRateComponent() {}

    public FlowRateComponent(float maxFlowRate, float currentFlowRate) {
        this.maxFlowRate = maxFlowRate;
        this.currentFlowRate = currentFlowRate;
    }

    public float getMaxFlowRate() {return maxFlowRate; }
    public float getCurrentFlowRate() {return currentFlowRate; }

    public void setCurrentFlowRate(float flowRate) {
        this.currentFlowRate = Math.min(flowRate, maxFlowRate);
    }

    // implement other shit, like how flow rate changes and checking neighbouring blocks

    @Override
    public @Nullable Component<ChunkStore> clone() {
        return new FlowRateComponent(maxFlowRate, currentFlowRate);
    }
}
