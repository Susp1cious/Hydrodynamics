package com.example.exampleplugin.component;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.jspecify.annotations.Nullable;

public class FluidStorageComponent implements Component<ChunkStore> {

    private float maxStorage;
    // private float initialValue;
    private float currentStorage = -1;

    public static final BuilderCodec<FluidStorageComponent> CODEC =
            BuilderCodec.builder(FluidStorageComponent.class, FluidStorageComponent::new)
                    .append(new KeyedCodec<>("MaxStorage", Codec.FLOAT),
                            (c, v) -> c.maxStorage = v, c -> c.maxStorage)
                    .add()
                    .append(new KeyedCodec<>("InitialValue", Codec.FLOAT),
                            (c, v) -> {
                        if (c.currentStorage < 0) {
                            c.currentStorage = v;
                        }
                        else {
                            return;
                        }}, c -> c.currentStorage)
                    .add()
                    .build();

    public FluidStorageComponent() {}

    public FluidStorageComponent(float maxStorage, float currentValue) {
        this.maxStorage = maxStorage;
        this.currentStorage = Math.min(Math.max(0, currentValue), maxStorage);

    }

    public float getMaxStorage() {
        return maxStorage;
    }
    public float getCurrentStorage() {return currentStorage; }

    public void setCurrentStorage(float storage) {
        this.currentStorage = Math.min(Math.max(0, storage), maxStorage);
    }

    // implement other shit, like how flow rate changes and checking neighboring blocks

    public int toTen() {
        return Math.round(this.currentStorage / this.maxStorage * 10) ;
    }

//    @Override
//    public @Nullable Component<ChunkStore> clone() {
//        return new FluidStorageComponent(maxStorage, currentStorage);
//    }

    @Nullable
    @Override
    public Component<ChunkStore> clone() {
        FluidStorageComponent clone = new FluidStorageComponent();
        clone.maxStorage = this.maxStorage;
        clone.currentStorage = this.currentStorage;
        return clone;
    }
}
