package com.example.exampleplugin.component;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.protocol.Fluid;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.jspecify.annotations.Nullable;

public class FluidStorageComponent implements Component<ChunkStore> {

    private float maxStorage;
    private float currentStorage;

    public static final BuilderCodec<FluidStorageComponent> CODEC =
            BuilderCodec.builder(FluidStorageComponent.class, FluidStorageComponent::new)
                    .append(new KeyedCodec<>("MaxStorage", Codec.FLOAT),
                            (c, v) -> c.maxStorage = v, c -> c.maxStorage)
                    .add()
                    .append(new KeyedCodec<>("CurrentStorage", Codec.FLOAT),
                            (c, v) -> c.currentStorage = v, c -> c.currentStorage)
                    .add()
                    .build();

    public FluidStorageComponent() {}

    public FluidStorageComponent(float maxStorage, float currentStorage) {
        this.maxStorage = maxStorage;
        this.currentStorage = currentStorage;
    }

    public float getMaxStorage() {return maxStorage; }
    public float getCurrentStorage() {return currentStorage; }

    public FluidStorageComponent setCurrentStorage(float storage) {
        this.currentStorage = Math.min(Math.max(0, storage), maxStorage);
        return this;
    }

    // implement other shit, like how flow rate changes and checking neighbouring blocks

    public int toTen() {
        return Math.round(this.currentStorage / this.maxStorage * 10) ;
    }

    @Override
    public @Nullable Component<ChunkStore> clone() {
        return new FluidStorageComponent(maxStorage, currentStorage);
    }
}
